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
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import pl.visphere.lib.exception.GenericRestException;

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
        this.cdnBaseUrl = CdnProperty.CDN_BASE_URL.getValue(environment);
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

    public String createFullResourcePath(S3Bucket bucket, String resourceDir, FilePayload payload, String uuid) {
        final String fileName = String.format("%s-%s.%s", payload.prefix().getPrefix(), uuid, payload.extension().getExt());
        final StringJoiner joiner = new StringJoiner("/")
            .add(cdnBaseUrl)
            .add(bucket.getName())
            .add(resourceDir + "/" + fileName);
        return joiner.toString();
    }

    public String createFullResourcePath(S3Bucket bucket, Long resourceDir, FilePayload payload, String uuid) {
        return createFullResourcePath(bucket, String.valueOf(resourceDir), payload, uuid);
    }

    public InsertedObjectRes putObject(S3Bucket bucket, String resourceDir, FilePayload payload) {
        String uuid = UUID.randomUUID().toString();
        if (payload.uuid() != null) {
            uuid = payload.uuid();
        }
        final String fileName = String.format("%s-%s.%s", payload.prefix().getPrefix(), uuid, payload.extension().getExt());
        final String filePath = resourceDir.equals(StringUtils.EMPTY) ? fileName : resourceDir + "/" + fileName;
        convertBytesToTempFile(payload, file -> client.putObject(bucket.getName(), filePath, file));
        return InsertedObjectRes.builder()
            .uuid(uuid)
            .fullPath(createFullResourcePath(bucket, resourceDir, payload, uuid))
            .build();
    }

    public InsertedObjectRes putObject(S3Bucket bucket, Long resourceDir, FilePayload payload) {
        return putObject(bucket, String.valueOf(resourceDir), payload);
    }

    public void putObject(S3Bucket bucket, FilePayload payload) {
        putObject(bucket, StringUtils.EMPTY, payload);
    }

    public void clearObjects(S3Bucket bucket, String resourceDir, S3ResourcePrefix resourcePrefix) {
        final ObjectListing objects = client.listObjects(bucket.getName(), resourceDir);
        final List<String> objectKeys = objects.getObjectSummaries().stream()
            .map(S3ObjectSummary::getKey)
            .filter(key -> key.contains(resourcePrefix.getPrefix()))
            .toList();
        for (final String objectKey : objectKeys) {
            client.deleteObject(bucket.getName(), objectKey);
        }
    }

    public void clearObjects(S3Bucket bucket, Long resourceDir, S3ResourcePrefix resourcePrefix) {
        clearObjects(bucket, String.valueOf(resourceDir), resourcePrefix);
    }

    public FileStreamInfo getObjectByFullKey(S3Bucket bucket, String resourceKey) {
        final S3Object object = client.getObject(bucket.getName(), resourceKey);
        byte[] objectBytes;
        String mimeType;
        try {
            objectBytes = object.getObjectContent().readAllBytes();
            mimeType = object.getObjectMetadata().getContentType();
        } catch (IOException ex) {
            log.error("Unexpected error during read file bytes. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
        return FileStreamInfo.builder()
            .data(objectBytes)
            .mimeType(mimeType)
            .build();
    }

    private void convertBytesToTempFile(FilePayload payload, Consumer<File> consumer) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(payload.prefix().getPrefix(), "." + payload.extension().getExt());
            Files.write(tempFile.toPath(), payload.data());
            log.info("Successfully created temp file: '{}' and fill with bytes data", tempFile.getName());
            consumer.accept(tempFile);
        } catch (AmazonServiceException ex) {
            log.error("Unexpected error during AWS service call. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        } catch (SdkClientException ex) {
            log.error("Unable to call AWS service by client. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        } catch (IOException ex) {
            log.error("Unable to write bytes data to temp file. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        } finally {
            if (tempFile != null) {
                tempFile.deleteOnExit();
                log.info("Removed temp file: '{}'", tempFile.getName());
            }
        }
    }
}
