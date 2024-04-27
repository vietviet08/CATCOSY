package com.dacs1.library.service.oauth2;

import java.util.Map;

public abstract class OAuth2UserDetails {

    protected Map<String , Object> attributes;


    public OAuth2UserDetails(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public abstract String getId();
    public abstract String getFirstName();

    public abstract String getLastName();
    public abstract String getFullName();

    public abstract String getEmail();
    public abstract String getAvatar();




}
