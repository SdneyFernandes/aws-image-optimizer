package br.com.optimizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import net.coobird.thumbnailator.Thumbnails;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ImageHandler implements RequestHandler<S3Event, String> {

    private final S3Client s3Client = S3Client.builder().build();
    private static final int TARGET_WIDTH = 1200;

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);
            String sourceBucket = record.getS3().getBucket().getName();
            String sourceKey = URLDecoder.decode(record.getS3().getObject().getKey(), StandardCharsets.UTF_8);

            System.out.printf("Evento recebido: Arquivo '%s' no bucket '%s'%n", sourceKey, sourceBucket);

            //Define o nome do bucket de destino. A variável de ambiente DESTINATION_BUCKET será definida no template.yml
            String destinationBucket = System.getenv("DESTINATION_BUCKET");
            if (destinationBucket == null || destinationBucket.isEmpty()) {
                throw new IllegalStateException("Variável de ambiente DESTINATION_BUCKET não está definida.");
            }

            //Faz o download da imagem original do S3
            System.out.println("Fazendo download da imagem original...");
            ResponseInputStream<?> s3Object = s3Client.getObject(
                GetObjectRequest.builder().bucket(sourceBucket).key(sourceKey).build()
            );

            //Redimensiona a imagem em memória
            System.out.printf("Redimensionando a imagem para a largura de %d pixels...%n", TARGET_WIDTH);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(s3Object)
                    .size(TARGET_WIDTH, TARGET_WIDTH)
                    .outputFormat("jpg")
                    .outputQuality(0.85)
                    .toOutputStream(outputStream);
            s3Object.close();

            //Faz o upload da nova imagem para o bucket de destino
            System.out.printf("Fazendo upload da imagem processada para o bucket '%s'...%n", destinationBucket);
            s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(destinationBucket)
                        .key(sourceKey)
                        .build(),
                RequestBody.fromBytes(outputStream.toByteArray())
            );

            System.out.println("Processo concluído com sucesso!");
            return "OK";

        } catch (Exception e) {
            System.err.println("Erro ao processar a imagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}