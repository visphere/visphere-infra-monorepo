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
    EMAIL_UPDATED_MFA_STATE("email-updated-mfa-state", false),
    EMAIL_RESET_MFA_STATE("email-reset-mfa-state", false),
    EMAIL_REQ_CHANGE_EMAIL("email-req-change-email", false),
    EMAIL_REQ_CHANGE_SECOND_EMAIL("email-req-change-second-email", false),
    EMAIL_CHANGED_EMAIL("email-changed-email", false),
    EMAIL_CHANGED_SECOND_EMAIL("email-changed-second-email", false),
    EMAIL_REMOVED_SECOND_EMAIL("email-removed-second-email", false),
    EMAIL_ENABLED_ACCOUNT("email-enabled-account", false),
    EMAIL_DISABLED_ACCOUNT("email-disabled-account", false),
    EMAIL_DELETED_ACCOUNT("email-deleted-account", false),
    GENERATE_DEFAULT_USER_PROFILE("generate-default-user-profile"),
    UPDATE_DEFAULT_USER_PROFILE("update-default-user-profile"),
    REPLACE_PROFILE_IMAGE_WITH_LOCKED("replace-profile-image-with-locked"),
    REPLACE_LOCKED_WITH_PROFILE_IMAGE("replace-locked-with-profile-image"),
    GENERATE_DEFAULT_GUILD_PROFILE("generate-default-guild-profile"),
    UPDATE_DEFAULT_GUILD_PROFILE("update-default-guild-profile"),
    PROFILE_IMAGE_DETAILS("profile-image-details"),
    GET_USER_PERSISTED_RELATED_SETTINGS("get-user-persisted-related-settings"),
    INSTANTIATE_USER_RELATED_SETTINGS("instantiate-user-related-settings"),
    PERSIST_NOTIF_USER_SETTINGS("persist-notif-user-settings"),
    GET_GUILD_IMAGES_BY_GUILD_IDS("get-guild-images-by-guild-ids"),
    GET_GUILD_PROFILE_IMAGE_DETAILS("get-guild-profile-image-details"),
    GET_GUILD_DETAILS("get-guild-details"),
    CHECK_USER_CREDENTIALS("check-user-crendetials"),
    CHECK_USER_SPHERE_GUILDS("check-user-sphere-guilds"),
    DELETE_USER_IMAGE_DATA("delete-user-image-data"),
    DELETE_GUILD_IMAGE_DATA("delete-guild-image-data"),
    DELETE_OAUTH2_USER_DATA("delete-oauth2-user-data"),
    DELETE_USER_SETTINGS_DATA("delete-user-settings-data"),
    DELETE_NOTIF_USER_SETTINGS("delete-notif-user-settings"),
    CHECK_USER_SESSION("check-user-session"),
    CHECK_USER_GUILD_ASSIGNMENTS("check-user-guild-assignments"),
    GET_USERS_IMAGES_DETAILS("get-users-images-details"),
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
