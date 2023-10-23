/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.NullableObjectWrapper;
import pl.visphere.lib.kafka.payload.UserDetailsResDto;

@RequiredArgsConstructor
public class StatelesslessUserDetailsService implements UserDetailsService {
    private final SyncQueueHandler syncQueueHandler;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsResDto userDetails;
        if (username == null) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }
        try {
            userDetails = syncQueueHandler
                .sendWithBlockThread(QueueTopic.CHECK_USER, username, UserDetailsResDto.class)
                .map(NullableObjectWrapper::content)
                .orElseThrow(RuntimeException::new);
        } catch (GenericRestException ignored) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }
        return new AuthUserDetails(userDetails);
    }
}
