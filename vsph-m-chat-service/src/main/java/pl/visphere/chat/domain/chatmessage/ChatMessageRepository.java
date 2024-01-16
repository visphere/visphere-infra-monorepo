/*
 * Copyright (c) 2024 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.domain.chatmessage;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends CassandraRepository<ChatMessageEntity, ChatPrimaryKey> {
    Slice<ChatMessageEntity> findAllByKey_TextChannelId(Long textChannelId, Pageable pageable);
    Optional<ChatMessageEntity> findByKey_TextChannelIdAndKey_UserIdAndKey_Id(Long textChannelId, Long userId, UUID keyId);
    void deleteAllByKey_TextChannelIdInAndKey_UserId(List<Long> textChannelId, Long userId);
    void deleteAllByKey_TextChannelIdIn(List<Long> textChannelIds);
}
