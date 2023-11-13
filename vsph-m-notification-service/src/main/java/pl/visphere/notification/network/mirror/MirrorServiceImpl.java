/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.mirror;

import com.amazonaws.SdkClientException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.file.GzipCompressor;
import pl.visphere.lib.jwt.JwtException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.JwtState;
import pl.visphere.lib.jwt.JwtValidateState;
import pl.visphere.lib.s3.*;
import pl.visphere.notification.network.mirror.dto.MirrorMailReqDto;

import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
class MirrorServiceImpl implements MirrorService {
    private final GzipCompressor gzipCompressor;
    private final S3Client s3Client;
    private final JwtService jwtService;

    @Override
    public String extractMirrorMail(MirrorMailReqDto reqDto) {
        final JwtValidateState validateState = jwtService.validate(reqDto.getToken());
        if (validateState.state() != JwtState.VALID) {
            throw new JwtException.JwtGeneralException(validateState.state().getPlaceholder(), HttpStatus.FORBIDDEN);
        }
        final Claims claims = validateState.claims();
        final String messageUuid = claims.getSubject();
        String outputHtml;
        try {
            final String resourceKey = new StringJoiner(StringUtils.EMPTY)
                .add(S3ResourcePrefix.EMAIL.getPrefix() + "-")
                .add(messageUuid + ".")
                .add(FileExtension.HTML_GZ.getExt())
                .toString();
            final FileStreamInfo compressedStream = s3Client.getObjectByFullKey(S3Bucket.EMAILS, resourceKey);
            outputHtml = gzipCompressor.decodeAndReturnString(compressedStream.data());
        } catch (SdkClientException ex) {
            log.error("Unable to get email object from S3. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
        log.info("Successfully find mirror email in S3 and decompress to raw html.");
        return outputHtml;
    }
}
