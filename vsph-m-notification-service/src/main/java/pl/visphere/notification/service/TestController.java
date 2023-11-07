/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.notification.config.ExternalServiceConfig;
import pl.visphere.notification.hbs.HbsProcessingService;
import pl.visphere.notification.hbs.HbsTemplate;
import pl.visphere.notification.mail.MailPayloadDto;
import pl.visphere.notification.mail.MailSenderService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/notification/test")
@RequiredArgsConstructor
public class TestController {
    private final HbsProcessingService hbsProcessingService;
    private final MailSenderService mailSenderService;
    private final ExternalServiceConfig externalServiceConfig;
    private final I18nService i18nService;

    @GetMapping("/process")
    ResponseEntity<String> template() {
        Map<String, Object> activateAccountValues = new HashMap<>();
        activateAccountValues.put("username", "Jan Kowalski");
        activateAccountValues.put("token", "1233211233");
        activateAccountValues.put("tokenLife", 48);

        final MailPayloadDto activateAccount = MailPayloadDto.builder()
            .title("Activate account")
            .htmlContent(hbsProcessingService.parseToRawHtml(HbsTemplate.ACTIVATE_ACCOUNT, "Activate account", activateAccountValues))
            .sendTo(Set.of("miloszgilga@gmail.com"))
            .build();

        Map<String, Object> changePasswordValues = new HashMap<>();
        changePasswordValues.put("username", "Jan Kowalski");
        changePasswordValues.put("token", "1233211233");
        changePasswordValues.put("tokenLife", 10);
        changePasswordValues.put("profileUrl", externalServiceConfig.getS3Url() + "/users/10/profile-53c92a2c-b2e7-49b2-b3f0-dc251d7c18fe.png");

        final MailPayloadDto changePassword = MailPayloadDto.builder()
            .title("Change password")
            .htmlContent(hbsProcessingService.parseToRawHtml(HbsTemplate.CHANGE_PASSWORD, "Change password", changePasswordValues))
            .sendTo(Set.of("miloszgilga@gmail.com"))
            .build();

        Map<String, Object> changedPasswordValues = new HashMap<>();
        changedPasswordValues.put("username", "Jan Kowalski");
        changedPasswordValues.put("profileUrl", externalServiceConfig.getS3Url() + "/users/10/profile-53c92a2c-b2e7-49b2-b3f0-dc251d7c18fe.png");

        final MailPayloadDto changedPassword = MailPayloadDto.builder()
            .title("Change password")
            .htmlContent(hbsProcessingService.parseToRawHtml(HbsTemplate.PASSWORD_CHANGED, "Change password", changedPasswordValues))
            .sendTo(Set.of("miloszgilga@gmail.com"))
            .build();

        Map<String, Object> newAccountValues = new HashMap<>();
        newAccountValues.put("username", "Jan Kowalski");
        newAccountValues.put("profileUrl", externalServiceConfig.getS3Url() + "/users/10/profile-53c92a2c-b2e7-49b2-b3f0-dc251d7c18fe.png");
        newAccountValues.put("nickname", "johnsand123");
        newAccountValues.put("profileEditLink", externalServiceConfig.getClientUrl() + "/profile/edit?lang=" + i18nService.getCurrentLocaleCode());
        newAccountValues.put("createGuildLink", externalServiceConfig.getClientUrl() + "/sphere/new?lang=" + i18nService.getCurrentLocaleCode());

        final MailPayloadDto newAccount = MailPayloadDto.builder()
            .title("New account")
            .htmlContent(hbsProcessingService.parseToRawHtml(HbsTemplate.NEW_ACCOUNT, "New account", newAccountValues))
            .sendTo(Set.of("miloszgilga@gmail.com"))
            .build();


        mailSenderService.sendEmail(activateAccount);
        mailSenderService.sendEmail(changePassword);
        mailSenderService.sendEmail(changedPassword);
        mailSenderService.sendEmail(newAccount);


        return ResponseEntity.ok("Successfully send email");
    }

}
