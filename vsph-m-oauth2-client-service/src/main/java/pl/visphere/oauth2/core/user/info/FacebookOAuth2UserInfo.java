/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user.info;

import pl.visphere.oauth2.core.user.OAuth2UserInfo;

import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {
    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("first_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("last_name");
    }

    @Override
    public String getEmailAddress() {
        return (String) attributes.get("email");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getUserImageUrl() {
        if (!attributes.containsKey("picture")) {
            return null;
        }
        final Object pictureAttributeBeforeSanitization = attributes.get("picture");
        if (!(pictureAttributeBeforeSanitization instanceof Map)) {
            return null;
        }
        final Map<String, Object> pictureObj = (Map<String, Object>) pictureAttributeBeforeSanitization;
        if (!pictureObj.containsKey("data")) {
            return null;
        }
        final Object dataObjBeforeSanitization = pictureObj.get("data");
        if (!(dataObjBeforeSanitization instanceof Map)) {
            return null;
        }
        final Map<String, Object> dataObj = (Map<String, Object>) dataObjBeforeSanitization;
        if (!dataObj.containsKey("url")) {
            return null;
        }
        return (String) dataObj.get("url");
    }
}
