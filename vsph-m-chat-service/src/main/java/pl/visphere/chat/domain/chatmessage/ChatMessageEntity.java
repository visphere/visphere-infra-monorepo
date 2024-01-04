/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.domain.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.List;

@Table(value = "chat_messages")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private ChatPrimaryKey key;

    private String message;

    @Column(value = "time_zone")
    private ZoneId timeZone;

    @Column(value = "files_list")
    private List<ChatFileDefinition> filesList;

    public ChatPrimaryKey getKey() {
        return key;
    }

    void setKey(ChatPrimaryKey key) {
        this.key = key;
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

    public List<ChatFileDefinition> getFilesList() {
        return filesList;
    }

    void setFilesList(List<ChatFileDefinition> filesList) {
        this.filesList = filesList;
    }

    @Override
    public String toString() {
        return "{" +
            "key=" + key +
            ", message=" + message +
            ", timeZone=" + timeZone +
            ", filesList=" + filesList +
            '}';
    }
}
