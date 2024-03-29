/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.exception.app;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.lib.file.MimeType;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FileException {
    @Slf4j
    public static class FileUnsupportedExtensionException extends AbstractRestException {
        public FileUnsupportedExtensionException(MimeType... supported) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.FILE_EXTENSION_NOT_SUPPORTED_EXCEPTION_MESSAGE, Map.of(
                "extensions", Arrays.stream(supported).map(MimeType::getName).collect(Collectors.joining(", "))
            ));
            log.error("Attempt to pass unsuported file extension. Supported extensions: '{}'.", Arrays.asList(supported));
        }
    }

    @Slf4j
    public static class FileExceededMaxSizeException extends AbstractRestException {
        public FileExceededMaxSizeException(String maxSizeMb, long passedMb) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.MAX_UPLOADED_FILE_SIZE_EXCEEDED_EXCEPTION_MESSAGE, Map.of(
                "maxSize", maxSizeMb
            ));
            log.error("Attempt to pass too large file. Max file size: '{}', passed: '{}'.", maxSizeMb,
                FileUtils.byteCountToDisplaySize(passedMb));
        }
    }

    @Slf4j
    public static class FileIsCorruptedException extends AbstractRestException {
        public FileIsCorruptedException(String reason) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.FILE_IS_CORRUPTED_EXCEPTION_MESSAGE);
            log.error("Attempt to process unsupported or corrupted file. Cause: '{}'.", reason);
        }
    }

    @Slf4j
    public static class MaxFilesInRequestExceedException extends AbstractRestException {
        public MaxFilesInRequestExceedException(int sendFiles, int maxFiles) {
            super(HttpStatus.BAD_REQUEST, LibLocaleSet.MAX_FILES_IN_REQUEST_EXCEED_EXCEPTION_MESSAGE);
            log.error("Attempt to send '{}' files when request only take: '{}'.", sendFiles, maxFiles);
        }
    }
}
