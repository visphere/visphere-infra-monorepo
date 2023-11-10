/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.mfa;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.lib.exception.GenericRestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaProxyServiceImpl implements MfaProxyService {
    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;
    private final MfaProperties mfaProperties;

    @Override
    public String generateSecret() {
        final String secret = secretGenerator.generate();
        log.info("Successfully generated secret.");
        return secret;
    }

    @Override
    public String generateQrCodeUri(String secret, int digits, int period) {
        final QrData qrData = new QrData.Builder()
            .label(mfaProperties.getLabel())
            .secret(secret)
            .issuer(mfaProperties.getIssuer())
            .algorithm(HashingAlgorithm.SHA512)
            .digits(digits)
            .period(period)
            .build();
        byte[] imageData;
        try {
            imageData = qrGenerator.generate(qrData);
        } catch (QrGenerationException ex) {
            log.error("Unable to generate QR code. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
        log.info("Successfully generated QR image data.");
        return Utils.getDataUriForImage(imageData, qrGenerator.getImageMimeType());
    }

    @Override
    public String generateQrCodeUri(String secret) {
        return generateQrCodeUri(secret, 6, 30);
    }

    @Override
    public boolean isOtpValid(String secret, String code) {
        final boolean isVerified = codeVerifier.isValidCode(secret, code);
        log.info("Successfully verified OTA with result: '{}'", isVerified);
        return isVerified;
    }

    @Override
    public boolean isOtpNotValid(String secret, String code) {
        return !isOtpValid(secret, code);
    }
}
