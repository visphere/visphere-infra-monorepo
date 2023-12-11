/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.file;

import lombok.extern.slf4j.Slf4j;
import pl.visphere.lib.exception.GenericRestException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class GzipCompressor {
    public byte[] encode(byte[] rawData) {
        byte[] compressedData;
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(rawData);
            gzipOutputStream.close();
            compressedData = outputStream.toByteArray();
        } catch (IOException ex) {
            throw new GenericRestException("Unable to perform GZIP compression on byte content. Cause: '{}'.",
                ex.getMessage());
        }
        return compressedData;
    }

    public byte[] decode(byte[] compressedData) {
        byte[] decompressedData;
        try (
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
            final GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            decompressedData = outputStream.toByteArray();
        } catch (IOException ex) {
            throw new GenericRestException("Unable to perform GZIP decompression on byte content. Cause: '{}'",
                ex.getMessage());
        }
        return decompressedData;
    }

    public String decodeAndReturnString(byte[] compressedData) {
        return new String(decode(compressedData), StandardCharsets.UTF_8);
    }
}
