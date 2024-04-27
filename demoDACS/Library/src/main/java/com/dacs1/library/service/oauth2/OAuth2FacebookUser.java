package com.dacs1.library.service.oauth2;

import java.util.Map;

public class OAuth2FacebookUser extends OAuth2UserDetails {

    public OAuth2FacebookUser(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getFirstName() {

        String[] name = nameUser();
        return name[0];
    }

    @Override
    public String getLastName() {
        String[] name = nameUser();
        StringBuilder lastName = new StringBuilder();
        for(int i = 1; i < name.length; i++)
            lastName.append(name[i]);
        return String.valueOf(lastName);
    }

    @Override
    public String getFullName() {
        return attributes.get("name").toString();
    }


    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getAvatar() {
        return null;
    }

    private String[] nameUser(){
        String fullName = (String) attributes.get("name");
        return fullName.split(" ");
    }
}
