/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.visphere.lib.file.GzipCompressor;
import pl.visphere.lib.file.MimeType;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendStateEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
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
    private final SyncQueueHandler syncQueueHandler;

    @Override
    public void activateAccount(SendTokenEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateTokenVariables(reqDto,
            otaTokenProperties.getActivateAccountHours(), false);
        senderVariables.put("clientRedirectUrl", generateClientRouteLink("/auth/activate-account/" + reqDto.getOtaToken()));
        sendEmail(reqDto, LocaleSet.MAIL_ACTIVATE_ACCOUNT_TITLE, HbsTemplate.ACTIVATE_ACCOUNT, senderVariables);
    }

    @Override
    public void newAccount(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        senderVariables.put("nickname", reqDto.getUsername());
        senderVariables.put("profileEditLink", generateClientRouteLink("/settings/my-account"));
        senderVariables.put("createGuildLink", generateClientRouteLink("/sphere/new"));
        sendEmail(reqDto, LocaleSet.MAIL_NEW_ACCOUNT_TITLE, HbsTemplate.NEW_ACCOUNT, senderVariables);
    }

    @Override
    public void changePassword(SendTokenEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateTokenVariables(reqDto,
            otaTokenProperties.getChangePasswordMinutes(), true);
        senderVariables.put("clientRedirectUrl", generateClientRouteLink("/auth/change-password/" + reqDto.getOtaToken()));
        sendEmail(reqDto, LocaleSet.MAIL_CHANGE_PASSWORD_TITLE, HbsTemplate.CHANGE_PASSWORD, senderVariables);
    }

    @Override
    public void passwordChanged(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_PASSWORD_CHANGED_TITLE, HbsTemplate.PASSWORD_CHANGED, senderVariables);
    }

    @Override
    public void mfaCode(SendTokenEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateTokenVariables(reqDto,
            otaTokenProperties.getMfaEmailMinutes(), true);
        sendEmail(reqDto, LocaleSet.MAIL_MFA_CODE_TITLE, HbsTemplate.MFA_CODE, senderVariables);
    }

    @Override
    public void updatedMfaState(SendStateEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        senderVariables.put("isEnabled", reqDto.getIsStateActive());
        sendEmail(reqDto, LocaleSet.MAIL_UPDATED_MFA_STATE_TITLE, HbsTemplate.UPDATED_MFA_STATE, senderVariables);
    }

    @Override
    public void resetMfaState(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_RESET_MFA_STATE_TITLE, HbsTemplate.RESET_MFA_STATE, senderVariables);
    }

    @Override
    public void reqChangeEmail(SendTokenEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateTokenVariables(reqDto,
            otaTokenProperties.getChangeEmailMinutes(), true);
        sendEmail(reqDto, LocaleSet.MAIL_REQ_UPDATE_EMAIL_TITLE, HbsTemplate.REQ_UPDATE_EMAIL, senderVariables);
    }

    @Override
    public void reqChangeSecondEmail(SendTokenEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateTokenVariables(reqDto,
            otaTokenProperties.getChangeEmailMinutes(), true);
        sendEmail(reqDto, LocaleSet.MAIL_REQ_UPDATE_SECOND_EMAIL_TITLE, HbsTemplate.REQ_UPDATE_SECOND_EMAIL,
            senderVariables);
    }

    @Override
    public void changedEmail(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_UPDATED_EMAIL_TITLE, HbsTemplate.UPDATED_EMAIL, senderVariables);
    }

    @Override
    public void changedSecondEmail(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_UPDATED_SECOND_EMAIL_TITLE, HbsTemplate.UPDATED_SECOND_EMAIL, senderVariables);
    }

    @Override
    public void removedSecondEmail(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_REMOVED_SECOND_EMAIL_TITLE, HbsTemplate.REMOVED_SECOND_EMAIL, senderVariables);
    }

    @Override
    public void enabledAccount(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_ENABLED_ACCOUNT_TITLE, HbsTemplate.ENABLED_ACCOUNT, senderVariables);
    }

    @Override
    public void disabledAccount(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, true);
        sendEmail(reqDto, LocaleSet.MAIL_DISABLED_ACCOUNT_TITLE, HbsTemplate.DISABLED_ACCOUNT, senderVariables);
    }

    @Override
    public void deletedAccount(SendBaseEmailReqDto reqDto) {
        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", reqDto.getFullName());
        sendEmail(reqDto, LocaleSet.MAIL_DELETED_ACCOUNT_TITLE, HbsTemplate.DELETED_ACCOUNT, senderVariables);
    }

    private Map<String, Object> generateBaseVariables(SendEmailReqDto reqDto, boolean hasImage) {
        final Map<String, Object> senderVariables = new HashMap<>();
        senderVariables.put("username", reqDto.getFullName());
        senderVariables.put("profileUrl", getUserProfileImageUrl(reqDto, hasImage));
        return senderVariables;
    }

    private Map<String, Object> generateTokenVariables(SendTokenEmailReqDto reqDto, int tokenLife, boolean hasImage) {
        final Map<String, Object> senderVariables = generateBaseVariables(reqDto, hasImage);
        senderVariables.put("token", reqDto.getOtaToken());
        senderVariables.put("tokenLife", tokenLife);
        return senderVariables;
    }

    private String generateClientRouteLink(String destination) {
        return UriComponentsBuilder
            .fromUriString(externalServiceConfig.getClientUrl() + destination)
            .queryParam("lang", i18nService.getCurrentLocaleCode())
            .build()
            .toUriString();
    }

    private String getUserProfileImageUrl(SendEmailReqDto reqDto, boolean hasImage) {
        String imageUrl = StringUtils.EMPTY;
        boolean isCustomImage = true;
        if (!hasImage) {
            return imageUrl;
        }
        if (reqDto.getIsExternalCredentialsSupplier()) {
            final OAuth2DetailsResDto detailsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.GET_OAUTH2_DETAILS, reqDto.getUserId(), OAuth2DetailsResDto.class);
            if (detailsResDto.profileImageSuppliedByProvider()) {
                isCustomImage = false;
                imageUrl = detailsResDto.profileImageUrl();
            }
        }
        if (isCustomImage) {
            final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, reqDto.getUserId(),
                    ProfileImageDetailsResDto.class);
            final String fileName = String.format("%s/%s-%s.%s", reqDto.getUserId(), S3ResourcePrefix.PROFILE.getPrefix(),
                profileImageDetails.profileImageUuid(), FileExtension.PNG);
            final FileStreamInfo imageStream = s3Client.getObjectByFullKey(S3Bucket.USERS, fileName);
            imageUrl = "data:" + MimeType.PNG.getMime() + ";base64," + Base64.getEncoder()
                .encodeToString(imageStream.data());
        }
        return imageUrl;
    }

    private void sendEmail(SendEmailReqDto reqDto, LocaleSet title, HbsTemplate template, Map<String, Object> variables) {
        final String messageUuid = UUID.randomUUID().toString();
        final String messageTitle = String.format("%s | (%s) %s", mailProperties.getAppName(), reqDto.getFullName(),
            i18nService.getMessage(title));

        final String htmlContent = hbsProcessingService
            .parseToRawHtml(template, messageTitle, variables, messageUuid);

        final FilePayload filePayload = FilePayload.builder()
            .prefix(S3ResourcePrefix.EMAIL)
            .data(gzipCompressor.encode(htmlContent.getBytes(StandardCharsets.UTF_8)))
            .uuid(messageUuid)
            .extension(FileExtension.HTML_GZ)
            .build();

        final MailPayloadDto payloadDto = MailPayloadDto.builder()
            .title(messageTitle)
            .htmlContent(htmlContent)
            .sendTo(Set.of(reqDto.getEmailAddress()))
            .build();

        s3Client.putObject(S3Bucket.EMAILS, filePayload);
        mailSenderService.sendEmail(payloadDto);

        log.info("Sending email for: '{}' ended. Call finished successfully.", reqDto);
    }
}
