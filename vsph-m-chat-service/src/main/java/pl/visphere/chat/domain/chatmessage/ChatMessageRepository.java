/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.chat.domain;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends CassandraRepository<ChatMessageEntity, UUID> {
    Slice<ChatMessageEntity> findAllByTextChannelId(Long textChannelId, Pageable pageable);
    void deleteAllByTextChannelIdInAndUserId(List<Long> textChannelId, Long userId);
    void deleteAllByTextChannelIdIn(List<Long> textChannelIds);
}
