/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user;

import lombok.RequiredArgsConstructor;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.core.user.info.FacebookOAuth2UserInfo;
import pl.visphere.oauth2.core.user.info.GoogleOAuth2UserInfo;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attributes;

    public abstract String getId();
    public abstract String getFirstName();
    public abstract String getLastName();
    public abstract String getEmailAddress();
    public abstract String getUserImageUrl();

    @Override
    public String toString() {
        return "{" +
            "id=" + getId() +
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
            };
        }
    }
}
