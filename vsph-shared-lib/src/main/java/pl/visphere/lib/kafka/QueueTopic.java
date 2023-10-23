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

import static pl.visphere.lib.kafka.SyncQueueHandler.REPLY_TOPIC_SUFFIX;

@RequiredArgsConstructor
public enum QueueTopic implements Property {
    CHECK_USER("check-user"),
    JWT_IS_ON_BLACKLIST("jwt-is-on-blacklist"),
    ACCOUNT_DETAILS("account-details"),
    PERSIST_USER("persist-user"),
    GENERATE_OTA_ACTIVATE_ACCOUNT("generate-ota-activate-account"),
    GENERATE_OTA_CHANGE_PASSWORD("generate-ota-change-password"),
    CHECK_OTA("check-ota"),
    GENERATE_DEFAULT_USER_PROFILE("generate-default-user-profile");

    private final String topicKey;
    private final boolean hasReply;

    QueueTopic(String topicKey) {
        this.topicKey = topicKey;
        this.hasReply = true;
    }

    public static String[] getAllReplyTopics(Environment environment) {
        return Arrays.stream(values())
            .filter(key -> key.hasReply)
            .map(key -> environment.getProperty("visphere.kafka.topic." + key.topicKey))
            .filter(Objects::nonNull)
            .map(topic -> topic + REPLY_TOPIC_SUFFIX + SyncQueueHandler.getReplyHash(environment))
            .toArray(String[]::new);
    }

    @Override
    public String getValue(Environment environment) {
        return environment.getProperty("visphere.kafka.topic." + topicKey);
    }
}
