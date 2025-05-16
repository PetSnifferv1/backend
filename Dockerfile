
# Etapa 1: Compilação
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copia apenas os arquivos necessários para resolver dependências
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw

# Baixa as dependências e compila o projeto (usa cache se nada mudou)
#RUN ./mvnw clean install

# Baixa as dependências (usa cache se nada mudou)
RUN ./mvnw dependency:go-offline

# Copia o código-fonte restante
COPY src ./src

# Compila o projeto e gera o .jar
RUN ./mvnw clean package

