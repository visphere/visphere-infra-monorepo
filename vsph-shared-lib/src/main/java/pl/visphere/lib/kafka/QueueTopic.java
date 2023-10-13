/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import pl.visphere.lib.ISpringProp;

import java.util.Arrays;
import java.util.Objects;

import static pl.visphere.lib.kafka.SyncQueueHandler.REPLY_TOPIC_SUFFIX;

@RequiredArgsConstructor
public enum QueueTopic implements ISpringProp {
    CHECK_USER("check-user", true);

    private final String topicKey;
    private final boolean hasReply;

    public static String[] getAllReplyTopics(Environment environment) {
        return Arrays.stream(values())
            .filter(key -> key.hasReply)
            .map(key -> environment.getProperty("visphere.kafka.topic." + key.topicKey))
            .filter(Objects::nonNull)
            .map(topic -> topic + REPLY_TOPIC_SUFFIX)
            .toArray(String[]::new);
    }

    @Override
    public String getKey(Environment environment) {
        return environment.getProperty("visphere.kafka.topic." + topicKey);
    }
}
