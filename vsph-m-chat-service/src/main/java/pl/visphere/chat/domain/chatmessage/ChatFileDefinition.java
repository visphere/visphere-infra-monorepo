/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.domain.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatFileDefinition {
    private String path;

    @Column(value = "mime_type")
    private String mimeType;

    @Column(value = "oroginal_name")
    private String originalName;

    public String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalName() {
        return originalName;
    }

    void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Override
    public String toString() {
        return "{" +
            "path=" + path +
            ", mimeType=" + mimeType +
            ", originalName=" + originalName +
            '}';
    }
}
