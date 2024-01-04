/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.domain.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyClass
public class ChatPrimaryKey {

    @PrimaryKeyColumn(name = "id", ordinal = 0)
    private UUID id;

    @PrimaryKeyColumn(name = "text_channel_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private Long textChannelId;

    @PrimaryKeyColumn(name = "user_id", ordinal = 2)
    private Long userId;

    @PrimaryKeyColumn(name = "created_timestamp", ordinal = 3, ordering = Ordering.DESCENDING)
    private Instant createdTimestamp;

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    Long getTextChannelId() {
        return textChannelId;
    }

    void setTextChannelId(Long textChannelId) {
        this.textChannelId = textChannelId;
    }

    public Long getUserId() {
        return userId;
    }

    void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", textChannelId=" + textChannelId +
            ", userId=" + userId +
            ", createdTimestamp=" + createdTimestamp +
            '}';
    }
}
