/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.Property;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum QueueTopic implements Property {
    JWT_IS_ON_BLACKLIST("jwt-is-on-blacklist"),
    CHECK_USER("check-user"),
    USER_DETAILS("user-details"),
    PERSIST_NEW_USER("persist-new-user"),
    GET_OAUTH2_USER_DETAILS("get-oauth2-user-details"),
    UPDATE_OAUTH2_USER_DETAILS("update-oauth2-user-details"),
    LOGIN_OAUTH2_USER("login-oauth2-user"),
    GET_OAUTH2_DETAILS("get-oauth2-details"),
    EMAIL_ACTIVATE_ACCOUNT("email-activate-account", false),
    EMAIL_NEW_ACCOUNT("email-new-account", false),
    EMAIL_CHANGE_PASSWORD("email-change-password", false),
    EMAIL_PASSWORD_CHANGED("email-password-changed", false),
    EMAIL_MFA_CODE("email-mfa-code", false),
    EMAIL_REQ_CHANGE_EMAIL("email-req-change-email", false),
    EMAIL_REQ_CHANGE_SECOND_EMAIL("email-req-change-second-email", false),
    EMAIL_CHANGED_EMAIL("email-changed-email", false),
    EMAIL_CHANGED_SECOND_EMAIL("email-changed-second-email", false),
    EMAIL_REMOVED_SECOND_EMAIL("email-removed-second-email", false),
    GENERATE_DEFAULT_USER_PROFILE("generate-default-user-profile"),
    GENERATE_DEFAULT_GUILD_PROFILE("generate-default-guild-profile"),
    UPDATE_DEFAULT_GUILD_PROFILE("update-default-guild-profile"),
    PROFILE_IMAGE_DETAILS("profile-image-details"),
    GET_USER_PERSISTED_RELATED_SETTINGS("get-user-persisted-related-settings"),
    INSTANTIATE_USER_RELATED_SETTINGS("instantiate-user-related-settings"),
    ;

    private final String topicKey;
    private final boolean hasReply;

    QueueTopic(String topicKey) {
        this.topicKey = topicKey;
        this.hasReply = true;
    }

    public static String[] getAllReplyTopics(Environment environment) {
        return Arrays.stream(values())
            .filter(key -> {
                final Object replyKey = environment.getProperty("visphere.kafka.reply-topic." + key.topicKey);
                return key.hasReply && replyKey != null;
            })
            .map(key -> environment.getProperty("visphere.kafka.topic." + key.topicKey))
            .filter(Objects::nonNull)
            .map(topic -> topic + SyncQueueHandler.REPLY_TOPIC_SUFFIX + SyncQueueHandler.getReplyHash(environment))
            .toArray(String[]::new);
    }

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.kafka.topic." + topicKey);
    }
}
