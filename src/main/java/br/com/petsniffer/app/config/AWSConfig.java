package br.com.petsniffer.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Profile;

@Getter
    @Configuration
    @Profile("!test")
    public class AWSConfig {


    // Injeta a região a partir das propriedades. Isso é seguro e útil.
    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AmazonS3 s3Client() {
        // O SDK da AWS usará o "DefaultAWSCredentialsProviderChain".
        // Em produção (ECS), ele encontrará a IAM Role da tarefa.
        // Em desenvolvimento local, ele pode encontrar variáveis de ambiente
        // ou o arquivo ~/.aws/credentials.
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
    }

//=================================================================================================\\
//        private final AmazonS3 s3Client;



    //      public AWSConfig(@Value("${AWS_ACCESS_KEY_ID}") String accessKey,
//                        @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey,
//                        @Value("${AWS_REGION}") String region) {
//            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
//
//            this.s3Client = AmazonS3ClientBuilder.standard()
//                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                    .withRegion(region)
//                    .build();
//        }


    }


