package com.dacs1.library.service.oauth2;

import com.dacs1.library.enums.Provider;

import java.util.Map;

public class OAuth2UserDetailFactory {

    public static OAuth2UserDetails getOAuth2Detail(String registrationId, Map<String, Object> attributes) {

        if (registrationId.equals(Provider.google.name())) {
            return new OAuth2GoogleUser(attributes);
        } else if (registrationId.equals(Provider.facebook.name())) {
            return new OAuth2FacebookUser(attributes);
        } else return null;

    }

}
