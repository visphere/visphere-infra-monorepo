/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.visphere.lib.file.GzipCompressor;
import pl.visphere.lib.file.MimeType;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.s3.*;
import pl.visphere.notification.config.ExternalServiceConfig;
import pl.visphere.notification.hbs.HbsProcessingService;
import pl.visphere.notification.hbs.HbsTemplate;
import pl.visphere.notification.i18n.LocaleSet;
import pl.visphere.notification.mail.MailPayloadDto;
import pl.visphere.notification.mail.MailProperties;
import pl.visphere.notification.mail.MailSenderService;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailSenderService mailSenderService;
    private final HbsProcessingService hbsProcessingService;
    private final S3Client s3Client;
    private final OtaTokenProperties otaTokenProperties;
    private final I18nService i18nService;
    private final MailProperties mailProperties;
    private final ExternalServiceConfig externalServiceConfig;
    private final GzipCompressor gzipCompressor;

    @Override
    public void activateAccount(SendTokenEmailReqDto reqDto) {
        final String messageUuid = UUID.randomUUID().toString();

        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", reqDto.getFullName());
        senderVariables.put("token", reqDto.getOtaToken());
        senderVariables.put("tokenLife", otaTokenProperties.getActivateAccountHours());

        final String title = createTitle(LocaleSet.MAIL_ACTIVATE_ACCOUNT_TITLE, reqDto.getFullName());
        final String htmlContent = hbsProcessingService
            .parseToRawHtml(HbsTemplate.ACTIVATE_ACCOUNT, title, senderVariables, messageUuid);

        persistMirrorInS3(htmlContent, messageUuid);
        sendEmail(reqDto, title, htmlContent, messageUuid);
    }

    @Override
    public void newAccount(SendBaseEmailReqDto reqDto) {
        final String messageUuid = UUID.randomUUID().toString();

        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", "Jan Kowalski");
        senderVariables.put("nickname", "johnsand123");
        senderVariables.put("profileEditLink", generateClientRouteLink("/profile/edit"));
        senderVariables.put("createGuildLink", generateClientRouteLink("/sphere/new"));
        senderVariables.put("profileBase64", getUserProfileImageAsBase64(reqDto));

        final String title = createTitle(LocaleSet.MAIL_NEW_ACCOUNT_TITLE, reqDto.getFullName());
        final String htmlContent = hbsProcessingService
            .parseToRawHtml(HbsTemplate.NEW_ACCOUNT, title, senderVariables, messageUuid);

        persistMirrorInS3(htmlContent, messageUuid);
        sendEmail(reqDto, title, htmlContent, messageUuid);
    }

    @Override
    public void changePassword(SendTokenEmailReqDto reqDto) {
        final String messageUuid = UUID.randomUUID().toString();

        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", reqDto.getFullName());
        senderVariables.put("token", reqDto.getOtaToken());
        senderVariables.put("tokenLife", otaTokenProperties.getChangePasswordMinutes());
        senderVariables.put("profileBase64", getUserProfileImageAsBase64(reqDto));

        final String title = createTitle(LocaleSet.MAIL_CHANGE_PASSWORD_TITLE, reqDto.getFullName());
        final String htmlContent = hbsProcessingService
            .parseToRawHtml(HbsTemplate.CHANGE_PASSWORD, title, senderVariables, messageUuid);

        sendEmail(reqDto, title, htmlContent, messageUuid);
    }

    @Override
    public void passwordChanged(SendBaseEmailReqDto reqDto) {
        final String messageUuid = UUID.randomUUID().toString();

        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", reqDto.getFullName());
        senderVariables.put("profileBase64", getUserProfileImageAsBase64(reqDto));

        final String title = createTitle(LocaleSet.MAIL_PASSWORD_CHANGED_TITLE, reqDto.getFullName());
        final String htmlContent = hbsProcessingService
            .parseToRawHtml(HbsTemplate.PASSWORD_CHANGED, title, senderVariables, messageUuid);

        sendEmail(reqDto, title, htmlContent, messageUuid);
    }

    @Override
    public void mfaCode(SendTokenEmailReqDto reqDto) {
        final String messageUuid = UUID.randomUUID().toString();

        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", reqDto.getFullName());
        senderVariables.put("token", reqDto.getOtaToken());
        senderVariables.put("tokenLife", otaTokenProperties.getMfaEmailMinutes());
        senderVariables.put("profileBase64", getUserProfileImageAsBase64(reqDto));

        final String title = createTitle(LocaleSet.MAIL_MFA_CODE_TITLE, reqDto.getFullName());
        final String htmlContent = hbsProcessingService
            .parseToRawHtml(HbsTemplate.MFA_CODE, title, senderVariables, messageUuid);

        sendEmail(reqDto, title, htmlContent, messageUuid);
    }

    private String generateClientRouteLink(String destination) {
        return UriComponentsBuilder
            .fromUriString(externalServiceConfig.getClientUrl() + destination)
            .queryParam("lang", i18nService.getCurrentLocaleCode())
            .build()
            .toUriString();
    }

    private void persistMirrorInS3(String htmlContent, String messageUuid) {
        final FilePayload filePayload = FilePayload.builder()
            .prefix(S3ResourcePrefix.EMAIL)
            .data(gzipCompressor.encode(htmlContent.getBytes(StandardCharsets.UTF_8)))
            .uuid(messageUuid)
            .extension(FileExtension.HTML_GZ)
            .build();
        s3Client.putObject(S3Bucket.EMAILS, filePayload);
    }

    private String getUserProfileImageAsBase64(SendEmailReqDto reqDto) {
        final String fileName = String.format("%s/%s-%s.%s", reqDto.getUserId(), S3ResourcePrefix.PROFILE.getPrefix(),
            reqDto.getProfileImageUuid(), FileExtension.PNG);
        final FileStreamInfo imageStream = s3Client.getObjectByFullKey(S3Bucket.USERS, fileName);
        return "data:" + MimeType.PNG.getMime() + ";base64," + Base64.getEncoder().encodeToString(imageStream.data());
    }

    private String createTitle(LocaleSet title, String fullName) {
        return String.format("%s | (%s) %s", mailProperties.getAppName(), fullName, i18nService.getMessage(title));
    }

    private void sendEmail(SendEmailReqDto reqDto, String title, String htmlContent, String messageUuid) {
        persistMirrorInS3(htmlContent, messageUuid);
        final MailPayloadDto payloadDto = MailPayloadDto.builder()
            .title(title)
            .htmlContent(htmlContent)
            .sendTo(Set.of(reqDto.getEmailAddress()))
            .build();
        mailSenderService.sendEmail(payloadDto);
        log.info("Sending email for: '{}' ended. Call finished successfully.", reqDto);
    }
}
