package br.com.petsniffer.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
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
        private final AmazonS3 s3Client;


        public AWSConfig(@Value("${AWS_ACCESS_KEY_ID}") String accessKey,
                        @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey,
                        @Value("${AWS_REGION}") String region) {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion(region)
                    .build();
        }


    }


