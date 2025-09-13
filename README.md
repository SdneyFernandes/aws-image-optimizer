# 🚀 Otimizador de Imagens Serverless na AWS

Um backend robusto e 100% serverless, construído na AWS, que automatiza o redimensionamento e otimização de imagens. Transforme uploads pesados em assets leves e prontos para a web em segundos, com uma arquitetura orientada a eventos e gerenciada via Infraestrutura como Código.

## ✨ Descrição do Projeto

Este projeto endereça um desafio crítico para aplicações web modernas: o gerenciamento de imagens enviadas por usuários. Imagens em alta resolução e sem compressão podem impactar drasticamente a performance de um site, levando a tempos de carregamento lentos e custos elevados de armazenamento e transferência de dados.

Nossa solução oferece um pipeline de otimização de imagens totalmente automatizado, garantindo que cada imagem enviada seja processada e otimizada para a web, resultando em:
* **Redução significativa do tamanho do arquivo** (ex: de MB para KB).
* **Dimensões padronizadas** para melhor exibição em diversas telas.
* **Qualidade visual otimizada** para a web, sem comprometer a experiência do usuário.

Tudo isso acontece de forma transparente, sem a necessidade de intervenção manual ou gerenciamento de servidores.

## 📐 Arquitetura da Solução

O fluxo de processamento de imagens é inteiramente serverless, escalável e altamente disponível, orquestrado por eventos:

1.  **Upload da Imagem Original:** Uma imagem é enviada para o `SourceImageBucket` (Amazon S3), que atua como nosso ponto de entrada.
2.  **Gatilho de Evento:** A criação de um novo objeto no `SourceImageBucket` dispara um evento que invoca a `ImageOptimizerFunction`.
3.  **Processamento Serverless (AWS Lambda):**
    * A função AWS Lambda (escrita em Java) faz o download da imagem original do S3.
    * Utiliza a poderosa biblioteca `Thumbnailator` para redimensionar a imagem para uma largura padrão (ex: 1200px) e otimizá-la, convertendo-a para o formato JPG com uma qualidade balanceada.
    * A imagem processada e otimizada é então armazenada no `DestinationImageBucket` (Amazon S3).
4.  **Disponibilidade:** O `DestinationImageBucket` é configurado para servir as imagens otimizadas publicamente, prontas para serem consumidas por qualquer aplicação ou site.

Toda a infraestrutura, incluindo buckets S3, a função Lambda, políticas de permissão (IAM) e os gatilhos de eventos, é definida e provisionada como **Infraestrutura como Código (IaC)** utilizando **AWS SAM (Serverless Application Model)**, que se traduz em modelos CloudFormation. Isso garante reprodutibilidade, versionamento e facilidade de gerenciamento.

### Diagrama da Arquitetura Atual (Backend)

Aqui está uma representação visual do fluxo de trabalho atual do backend:

```mermaid
graph TD
    A[Usuário/Aplicação] -- Upload de Imagem Original --> B(SourceImageBucket - S3)
    B -- Evento: s3:ObjectCreated:* --> C(ImageOptimizerFunction - AWS Lambda)
    C -- GetObject (Download da Original) --> B
    C -- Processamento de Imagem (Java + Thumbnailator) --> D[Imagem Otimizada]
    D -- PutObject (Upload da Otimizada) --> E(DestinationImageBucket - S3)
    E -- Imagem Otimizada Pública --> F[Consumidor: Frontend/Website]


🛠 Tecnologias Utilizadas
Linguagem de Programação: Java 21

Plataforma Cloud: AWS (Amazon Web Services)

Serviços AWS Essenciais:

AWS Lambda: Para execução do código serverless e otimização das imagens.

Amazon S3: Armazenamento de objetos escalável para imagens originais e otimizadas.

AWS IAM: Gerenciamento de acessos e permissões seguras para a função Lambda e S3.

AWS CloudFormation: Provisionamento de todos os recursos da infraestrutura de forma declarativa.

Frameworks e Ferramentas:

AWS SAM CLI: Ferramenta poderosa para desenvolvimento, testes locais e deployment de aplicações serverless na AWS.

Maven: Sistema de build e gerenciamento de dependências para o projeto Java.

AWS SDK for Java v2: Interação programática com os serviços AWS.

Thumbnailator: Biblioteca eficiente para manipulação e otimização de imagens em Java.

Docker: Utilizado pelo SAM CLI para emular o ambiente Lambda localmente, garantindo consistência.

🚀 Como Executar Localmente
Para testar a lógica da função Lambda em seu ambiente local antes de fazer o deploy:

Pré-requisitos: Certifique-se de ter o AWS CLI, AWS SAM CLI e Docker Desktop instalados e configurados em sua máquina.

Clone o Repositório:

Bash

git clone [https://github.com/SdneyFernandes/aws-image-optimizer.git](https://github.com/SdneyFernandes/aws-image-optimizer.git)
cd aws-image-optimizer
Navegue para o Backend:

Bash

cd backend-lambda
Compile o Projeto Java:

Bash

mvn clean package
Crie um Evento de Teste (event.json):
Crie um arquivo event.json na pasta backend-lambda com o seguinte conteúdo, simulando um upload no S3:

JSON

{
  "Records": [
    {
      "eventVersion": "2.1",
      "eventSource": "aws:s3",
      "awsRegion": "sa-east-1",
      "eventTime": "1970-01-01T00:00:00.000Z",
      "eventName": "ObjectCreated:Put",
      "userIdentity": {
        "principalId": "EXAMPLE"
      },
      "requestParameters": {
        "sourceIPAddress": "127.0.0.1"
      },
      "responseElements": {
        "x-amz-request-id": "EXAMPLE123456789",
        "x-amz-id-2": "EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH"
      },
      "s3": {
        "s3SchemaVersion": "1.0",
        "configurationId": "testConfig",
        "bucket": {
          "name": "your-source-bucket-name", # <-- ATENÇÃO: Substitua pelo nome do seu source bucket de verdade
          "ownerIdentity": {
            "principalId": "EXAMPLE"
          },
          "arn": "arn:aws:s3:::your-source-bucket-name"
        },
        "object": {
          "key": "your-test-image.jpg", # <-- ATENÇÃO: Substitua pelo nome de um arquivo existente no seu source bucket
          "size": 1024,
          "eTag": "EXAMPLE",
          "sequencer": "0A1B2C3D4E5F678901"
        }
      }
    }
  ]
}
Execute a Função Localmente:

Bash

sam local invoke ImageOptimizerFunction --event event.json --env-vars env.json
(Note: Se você precisar passar variáveis de ambiente como DESTINATION_BUCKET localmente, crie um env.json com {"ImageOptimizerFunction": {"DESTINATION_BUCKET": "your-destination-bucket-name"}} e use --env-vars env.json)

☁️ Como Fazer o Deploy para a AWS
Com suas credenciais da AWS CLI configuradas e sua conta logada, o deploy da infraestrutura completa na nuvem é um processo guiado e simplificado:

Navegue até a pasta do backend: cd backend-lambda

Execute o comando de deploy guiado:

Bash

sam deploy --guided
Siga as instruções no terminal para configurar o nome da Stack, região e confirmar o deploy.

⏭️ Próximos Passos & Roadmap
Estamos sempre buscando aprimorar e expandir as funcionalidades deste projeto. Aqui estão os próximos itens em nosso roadmap:

[ ] Desenvolver um Frontend Interativo: Criar uma interface de usuário em Next.js para permitir que os usuários façam upload de imagens facilmente e visualizem os resultados.

Diagrama da Arquitetura Futura (com Frontend):

Snippet de código

graph TD
    A[Usuário] -- Navega para --> B(Frontend Next.js)
    B -- Seleciona Imagem --> B
    B -- Requisição para Upload URL --> C(API Gateway)
    C -- Invoca --> D(GetUploadUrlFunction - AWS Lambda)
    D -- Gera Presigned URL para S3 --> E(SourceImageBucket - S3)
    D -- Retorna Presigned URL --> B
    B -- Upload Direto de Imagem --> E
    E -- Evento: s3:ObjectCreated:* --> F(ImageOptimizerFunction - AWS Lambda)
    F -- GetObject (Download da Original) --> E
    F -- Processamento de Imagem --> G[Imagem Otimizada]
    G -- PutObject (Upload da Otimizada) --> H(DestinationImageBucket - S3)
    H -- Exibe/Download da Otimizada --> B
[ ] Implementar Geração de S3 Presigned URLs: Para permitir uploads seguros e diretos do navegador para o SourceImageBucket, sem expor credenciais AWS.

[ ] Monitoramento e Logs Aprimorados: Integrar com ferramentas de monitoramento mais avançadas e logs estruturados para melhor visibilidade da aplicação.

[ ] Adicionar Persistência de Metadados: Utilizar um banco de dados NoSQL (ex: Amazon DynamoDB) para armazenar metadados sobre as imagens processadas (nome original, nome otimizado, tamanho, data de processamento, etc.).

[ ] Suporte a Múltiplos Formatos de Saída: Oferecer opções para que o usuário escolha o formato de saída (WebP, PNG, etc.).

Se você tiver dúvidas, sugestões ou quiser colaborar, sinta-se à vontade para entrar em contato
