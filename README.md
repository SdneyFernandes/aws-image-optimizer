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
