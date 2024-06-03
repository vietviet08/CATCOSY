package com.dacs1.library.service.oauth2;

import java.util.Map;

public class OAuth2GoogleUser extends OAuth2UserDetails{

    public OAuth2GoogleUser(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("given_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("family_name");
    }

    @Override
    public String getFullName() {
        return (String) attributes.get("name");
    }


    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getAvatar() {
        return (String) attributes.get("picture");
    }
}
