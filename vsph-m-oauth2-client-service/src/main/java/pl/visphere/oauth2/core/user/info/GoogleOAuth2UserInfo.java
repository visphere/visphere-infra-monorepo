/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user.info;

import org.apache.commons.lang3.StringUtils;
import pl.visphere.oauth2.core.user.OAuth2UserInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getFirstName() {
        return ((String) attributes.get("name")).split(StringUtils.SPACE)[0];
    }

    @Override
    public String getLastName() {
        return ((String) attributes.get("name")).split(StringUtils.SPACE)[1];
    }

    @Override
    public String getEmailAddress() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUserImageUrl() {
        return (String) attributes.get("picture");
    }
}
