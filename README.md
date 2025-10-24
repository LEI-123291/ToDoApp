# App README

- [ ] TODO Replace or update this README with instructions relevant to your application
https://www.youtube.com/watch?v=FysjBNtIFkk



## ⚙️ Pipeline de Automação (GitHub Actions)

O projeto inclui uma **pipeline de CI/CD** configurada com **GitHub Actions** para automatizar a construção (build) do artefacto `.jar` com o Maven.

Esta pipeline é executada **automaticamente em cada _push_** para a branch principal (`LEI-123291`).  
Ela realiza as seguintes etapas:

1. Faz o _checkout_ do repositório.  
2. Configura o ambiente Java (versão 21, distribuição Temurin).  
3. Executa o comando `mvn clean package` para compilar o projeto e gerar o `.jar`.  
4. Copia o `.jar` gerado para a raiz do repositório.  
5. Publica o ficheiro `.jar` como artefacto do workflow (disponível para download no separador **Actions**).

###  Excerto do ficheiro `build.yml`


name: Build JAR

on:
  push:
    branches: [ "LEI-123291" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

      - name: Build with Maven
        run: mvn -B -ntp clean package

      - name: Copy jar to root
        run: cp target/*.jar .

      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar


## Project Structure

The sources of your App have the following structure:


```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
App implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
