# Otimizador de Imagens Serverless com AWS Lambda e S3

![Licença](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![AWS](https://img.shields.io/badge/AWS-Serverless-yellow.svg)

## 📖 Sobre o Projeto

Este projeto implementa um serviço de otimização de imagens 100% serverless na AWS. A solução utiliza uma arquitetura orientada a eventos para processar automaticamente imagens enviadas a um bucket S3, redimensionando-as e comprimindo-as para uso otimizado na web.

O objetivo é resolver um problema comum de performance em aplicações web e mobile: o carregamento lento causado por imagens pesadas. Com este serviço, o processo de otimização é totalmente automatizado, escalável e de baixo custo.

---

## 🏛️ Arquitetura e Fluxo de Trabalho

O fluxo de trabalho é iniciado por um evento de criação de objeto no S3. A arquitetura foi desenhada para ser eficiente e desacoplada:

```mermaid
graph TD
    A[👤 Usuário] -->|1. Upload da imagem original (ex: foto.png)| B(🪣 Bucket S3 de Origem);
    B -->|2. Aciona o gatilho (S3 Event)| C{🚀 Função Lambda (Java 21)};
    C -->|3. Processa e otimiza a imagem| D[⚙️ Lógica de Otimização <br> com Thumbnailator];
    C -->|4. Salva a imagem otimizada (ex: foto.jpg)| E(🪣 Bucket S3 de Destino);
    F[🌐 Aplicação Web/Mobile] -->|5. Acesso público à imagem otimizada| E;
```

1.  **Upload:** Um usuário ou sistema faz o upload de uma imagem para o `Bucket S3 de Origem`.
2.  **Gatilho:** O evento `s3:ObjectCreated:*` aciona automaticamente a função Lambda.
3.  **Processamento:** A função Lambda, escrita em **Java 21**, faz o download da imagem, a redimensiona para uma largura máxima de 1200px, ajusta sua qualidade e a converte para o formato JPG usando a biblioteca `Thumbnailator`.
4.  **Armazenamento:** A nova imagem otimizada é salva no `Bucket S3 de Destino`.
5.  **Acesso:** O `Bucket de Destino` é configurado com acesso público, permitindo que a imagem otimizada seja facilmente referenciada em qualquer aplicação.

---

## ✨ Principais Funcionalidades

-   **Automação via Eventos S3:** Processamento de imagens iniciado automaticamente no momento do upload.
-   **Redimensionamento e Compressão:** Redução significativa do tamanho dos arquivos de imagem sem perda drástica de qualidade.
-   **Infraestrutura como Código (IaC):** Toda a infraestrutura AWS (Buckets, Lambda, Permissões) é definida e gerenciada via **AWS SAM (CloudFormation)**.
-   **Arquitetura Serverless:** Solução de baixo custo, escalável e que não requer gerenciamento de servidores.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído com as seguintes tecnologias:

-   **Backend:**
    -   Java 21
    -   AWS Lambda Java Core & Events
    -   Thumbnailator (para manipulação de imagens)
    -   SLF4J (para logging)

-   **Cloud (AWS):**
    -   AWS Lambda
    -   Amazon S3 (Simple Storage Service)
    -   AWS IAM (Identity and Access Management)

-   **Infraestrutura e DevOps:**
    -   AWS SAM (Serverless Application Model)
    -   Apache Maven (Gerenciamento de dependências e build)
    -   Git & GitHub

---

## 🚀 Como Executar o Projeto

Para implantar esta solução em sua própria conta AWS, siga os passos abaixo.

### Pré-requisitos

-   Conta na AWS com credenciais configuradas
-   [AWS CLI](https://aws.amazon.com/cli/)
-   [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
-   Java 21 (ou superior)
-   Apache Maven 3.8 (ou superior)

### Passos para Implantação

1.  **Clone o repositório:**

2.  **Compile e empacote o projeto Java:**
    O comando abaixo irá compilar o código e criar o arquivo `.jar` necessário para a Lambda.
    ```bash
    mvn clean package
    ```

3.  **Construa o pacote da aplicação SAM:**
    Este comando prepara os artefatos para a implantação.
    ```bash
    sam build
    ```

4.  **Faça o deploy da stack na AWS:**
    Inicie o processo de deploy guiado. O SAM CLI irá pedir algumas informações, como o nome da stack e a região.
    ```bash
    sam deploy --guided
    ```
    Confirme as alterações no changeset para que o AWS CloudFormation crie todos os recursos.

### Como Usar

Após o deploy bem-sucedido:

1.  Acesse o Console da AWS e vá para o serviço **S3**.
2.  Localize o bucket de origem (o nome estará nos outputs do `sam deploy`).
3.  Faça o upload de um arquivo de imagem (PNG, JPG, etc.).
4.  Após alguns segundos, verifique o bucket de destino. A versão otimizada da sua imagem deverá estar lá!
5.  Você pode verificar os logs de execução no serviço **AWS CloudWatch Log Groups**.
