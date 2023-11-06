/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.visphere.lib.exception.GenericRestException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public void sendEmail(MailPayloadDto payloadDto) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());

            helper.setText(payloadDto.htmlContent(), true);

            for (final String toEmail : payloadDto.sendTo()) {
                helper.addTo(toEmail);
            }
            if (payloadDto.inlineResources() != null) {
                for (final MailResourceDto inlineResources : payloadDto.inlineResources()) {
                    helper.addInline(inlineResources.name(), inlineResources.file());
                }
            }
            if (payloadDto.attachments() != null) {
                for (final MailResourceDto attachments : payloadDto.attachments()) {
                    helper.addAttachment(attachments.name(), attachments.file());
                }
            }
            helper.setSubject(payloadDto.title());
            helper.setFrom(mailProperties.getSendFrom());
            helper.setReplyTo(mailProperties.getReplyTo(), mailProperties.getAppName());
            helper.setSentDate(new Date());

            javaMailSender.send(mimeMessage);

            log.info("Successfully send email message with payload: '{}'", payloadDto);
        } catch (MessagingException | IOException ex) {
            log.error("Unable to send email message with payload: '{}'. Cause: '{}'", payloadDto, ex.getMessage());
            throw new GenericRestException();
        }
    }
}
