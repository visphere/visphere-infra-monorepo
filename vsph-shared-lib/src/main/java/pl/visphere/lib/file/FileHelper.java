/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

public class FileHelper {
    public boolean checkIfExtensionIsSupported(MultipartFile file, MimeType... types) {
        return Arrays.stream(types).anyMatch(t -> Objects.equals(t.getMime(), file.getContentType()));
    }

    public boolean checkIfExceededMaxSize(MultipartFile file, int maxSizeMb) {
        final long fileSizeB = file.getSize();
        final long maxSizeB = (long) maxSizeMb * 1024 * 1024;
        return fileSizeB > maxSizeB;
    }

    public String mbFormat(int mbSize) {
        return mbSize + "MB";
    }
}
