package br.com.petsniffer.app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class AWSConfig {
        private final AmazonS3 s3Client;

         public AWSConfig() {
            // Caso esteja usando credenciais fixas
            BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIASK5MCDTOOXLAURJ2", "TN3u95Md62u+edbNNaj04jzfXwaXAnoqlTwG3oKW");
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion("us-east-1") // Substitua pela sua região
                    .build();
        }

        public AmazonS3 getS3Client() {
            return s3Client;
        }





 }


