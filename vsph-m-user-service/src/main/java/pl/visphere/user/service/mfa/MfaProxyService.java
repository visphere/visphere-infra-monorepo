/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.service.mfa;

public interface MfaProxyService {
    String generateSecret();
    String generateQrCodeUri(String secret, int digits, int period);
    String generateQrCodeUri(String secret);
    boolean isOtpValid(String secret, String code);
    boolean isOtpNotValid(String secret, String code);
}
