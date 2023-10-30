/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class S3Client {
    private final Environment environment;
    private final String cdnBaseUrl;

    private AmazonS3 client;

    public S3Client(Environment environment) {
        this.environment = environment;
        this.cdnBaseUrl = S3Property.CDN_BASE_URL.getValue(environment);
    }

    public void initialize() {
        final AWSCredentials credentials = new BasicAWSCredentials(
            S3Property.ACCESS_KEY.getValue(environment),
            S3Property.SECRET_KEY.getValue(environment)
        );
        final AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
            S3Property.URL.getValue(environment),
            S3Property.REGION.getValue(environment)
        );
        client = AmazonS3ClientBuilder
            .standard()
            .withPathStyleAccessEnabled(S3Property.PATH_STYLE_ACCESS_ENABLED.getValue(environment, Boolean.class))
            .withEndpointConfiguration(endpoint)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
        try {
            final List<String> buckets = client.listBuckets().stream().map(Bucket::getName).toList();
            log.info("Successfully connected with S3 service. Found buckets: '{}'", buckets);
        } catch (AmazonServiceException ex) {
            log.error("Unable to connect with S3 service. Cause: '{}'", ex.getMessage());
        }
    }

    public InsertedObjectRes putObject(S3Bucket bucket, String resourceDir, FilePayload payload) {
        final String uuid = UUID.randomUUID().toString();
        final String fileName = String.format("%s-%s.%s", payload.name(), uuid, payload.extension().getExt());
        final String filePath = resourceDir + "/" + fileName;
        convertBytesToTempFile(payload, file -> client.putObject(bucket.getName(), filePath, file));
        final StringJoiner joiner = new StringJoiner("/")
            .add(cdnBaseUrl)
            .add(bucket.getName())
            .add(filePath);
        return InsertedObjectRes.builder()
            .uuid(uuid)
            .fullPath(joiner.toString())
            .build();
    }

    private void convertBytesToTempFile(FilePayload payload, Consumer<File> consumer) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(payload.name(), "." + payload.extension().getExt());
            Files.write(tempFile.toPath(), payload.data());
            log.info("Successfully created temp file: '{}' and fill with bytes data", tempFile.getName());
            consumer.accept(tempFile);
        } catch (AmazonServiceException ex) {
            log.error("Unexpected error during AWS service call. Details: '{}'", ex.getMessage());
            throw new RuntimeException();
        } catch (SdkClientException ex) {
            log.error("Unable to call AWS service by client. Cause: '{}'", ex.getMessage());
            throw new RuntimeException();
        } catch (IOException ex) {
            log.error("Unable to write bytes data to temp file. Cause: '{}'", ex.getMessage());
            throw new RuntimeException();
        } finally {
            if (tempFile != null) {
                tempFile.deleteOnExit();
                log.info("Removed temp file: '{}'", tempFile.getName());
            }
        }
    }
}
