/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Objects;

import static pl.visphere.lib.kafka.SyncQueueHandler.REPLY_TOPIC_SUFFIX;

@Getter
@RequiredArgsConstructor
public enum QueueTopic {
    CHECK_USER("check-user", true);

    private final String topicKey;
    private final boolean hasReply;

    public String getDecoded(Environment environment) {
        return environment.getProperty("visphere.kafka.topic." + topicKey);
    }

    public static String[] getAllReplyTopics(Environment environment) {
        return Arrays.stream(values())
            .filter(key -> key.hasReply)
            .map(key -> environment.getProperty("visphere.kafka.topic." + key.topicKey))
            .filter(Objects::nonNull)
            .map(topic -> topic + REPLY_TOPIC_SUFFIX)
            .toArray(String[]::new);
    }
}
