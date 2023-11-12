/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.core.user.info.FacebookOAuth2UserInfo;
import pl.visphere.oauth2.core.user.info.GoogleOAuth2UserInfo;

import java.util.Map;
import java.util.StringJoiner;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attributes;

    public abstract String getId();
    public abstract String getFirstName();
    public abstract String getLastName();
    public abstract String getEmailAddress();
    public abstract String getUserImageUrl();

    public String getUsername() {
        final StringJoiner firstUsername = new StringJoiner(StringUtils.EMPTY)
            .add(getFirstName())
            .add(getLastName())
            .add(RandomStringUtils.randomNumeric(4));
        return firstUsername
            .toString()
            .toLowerCase();
    }

    @Override
    public String toString() {
        return "{" +
            "id=" + getId() +
            "username=" + getUsername() +
            "firstName=" + getFirstName() +
            "lastName=" + getLastName() +
            "emailAddress=" + getEmailAddress() +
            "userImageUrl=" + getUserImageUrl() +
            '}';
    }

    public static class OAuth2UserInfoFabricator {
        public static OAuth2UserInfo fabricate(OAuth2Supplier supplier, Map<String, Object> attributes) {
            return switch (supplier) {
                case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
                case FACEBOOK -> new FacebookOAuth2UserInfo(attributes);
                case LOCAL -> null;
            };
        }
    }
}
