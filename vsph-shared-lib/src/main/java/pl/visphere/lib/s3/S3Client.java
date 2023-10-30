/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class S3Client {
    private final Environment environment;

    public AmazonS3 initialize() {
        final AWSCredentials credentials = new BasicAWSCredentials(
            S3Property.ACCESS_KEY.getValue(environment),
            S3Property.SECRET_KEY.getValue(environment)
        );
        final AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
            S3Property.URL.getValue(environment),
            S3Property.REGION.getValue(environment)
        );
        final AmazonS3 client = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(endpoint)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
        try {
            final List<String> buckets = client.listBuckets().stream().map(Bucket::getName).toList();
            log.info("Successfully connected with S3 service. Found buckets: '{}'", buckets);
        } catch (AmazonServiceException ex) {
            log.error("Unable to connect with S3 service. Cause: {}", ex.getMessage());
        }
        return client;
    }
}
