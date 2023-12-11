/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.lib.file.MimeType;
import pl.visphere.multimedia.i18n.LocaleSet;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FileException {
    @Slf4j
    public static class FileUnsupportedExtensionException extends AbstractRestException {
        public FileUnsupportedExtensionException(MimeType... supported) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.FILE_EXTENSION_NOT_SUPPORTED_EXCEPTION_MESSAGE, Map.of(
                "extensions", Arrays.stream(supported).map(MimeType::getName).collect(Collectors.joining(", "))
            ));
            log.error("Attempt to pass unsuported file extension. Supported extensions: '{}'.", Arrays.asList(supported));
        }
    }

    @Slf4j
    public static class FileExceededMaxSizeException extends AbstractRestException {
        public FileExceededMaxSizeException(String maxSizeMb, long passedMb) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.MAX_UPLOADED_FILE_SIZE_EXCEEDED_EXCEPTION_MESSAGE, Map.of(
                "maxSize", maxSizeMb
            ));
            log.error("Attempt to pass too large file. Max file size: '{}', passed: '{}'.", maxSizeMb,
                FileUtils.byteCountToDisplaySize(passedMb));
        }
    }

    @Slf4j
    public static class FileIsCorruptedException extends AbstractRestException {
        public FileIsCorruptedException(String reason) {
            super(HttpStatus.BAD_REQUEST, LocaleSet.FILE_IS_CORRUPTED_EXCEPTION_MESSAGE);
            log.error("Attempt to process unsupported or corrupted file. Cause: '{}'.", reason);
        }
    }
}
