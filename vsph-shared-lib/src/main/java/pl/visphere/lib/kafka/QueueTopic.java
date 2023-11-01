/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.Property;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum QueueTopic implements Property {
    JWT_IS_ON_BLACKLIST("jwt-is-on-blacklist"),
    CHECK_USER("check-user"),
    USER_DETAILS("user-details"),
    EMAIL_ACTIVATE_ACCOUNT("email-activate-account", false),
    EMAIL_NEW_ACCOUNT("email-new-account", false),
    EMAIL_CHANGE_PASSWORD("email-change-password", false),
    EMAIL_PASSWORD_CHANGED("email-password-changed", false),
    GENERATE_DEFAULT_USER_PROFILE("generate-default-user-profile"),
    PROFILE_IMAGE_DETAILS("profile-image-details");

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
