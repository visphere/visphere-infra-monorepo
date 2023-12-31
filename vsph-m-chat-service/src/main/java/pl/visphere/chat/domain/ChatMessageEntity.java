/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Table(value = "chat_messages")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "text_channel_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long textChannelId;

    @PrimaryKeyColumn(name = "created_timestamp", ordinal = 1, ordering = Ordering.DESCENDING)
    private Instant createdTimestamp;

    private UUID id;

    private String message;

    @Column(value = "time_zone")
    private ZoneId timeZone;

    @Column(value = "user_id")
    private Long userId;

    Long getTextChannelId() {
        return textChannelId;
    }

    void setTextChannelId(Long textChannelId) {
        this.textChannelId = textChannelId;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public Long getUserId() {
        return userId;
    }

    void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "{" +
            "textChannelId=" + textChannelId +
            ", createdTimestamp=" + createdTimestamp +
            ", id=" + id +
            ", message=" + message +
            ", timeZone=" + timeZone +
            ", userId=" + userId +
            '}';
    }
}
