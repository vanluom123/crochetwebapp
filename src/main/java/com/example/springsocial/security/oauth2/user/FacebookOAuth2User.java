package com.example.springsocial.security.oauth2.user;

import java.util.Map;

public class FacebookOAuth2User extends AbstractOAuth2User {
  public FacebookOAuth2User(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getName() {
    return (String) attributes.get("name");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getImageUrl() {
    Map<String, Object> pictureObj = uncheckedCast(attributes.get("picture"));
    if (pictureObj != null) {
      Map<String, Object> dataObj = uncheckedCast(pictureObj.get("data"));
      if (dataObj != null) {
        return uncheckedCast(dataObj.get("url"));
      }
    }
    return null;
  }

  private static <T> T uncheckedCast(Object obj) {
    try {
      return (T) obj;
    } catch (ClassCastException e) {
      throw new ClassCastException("Cannot cast object to target type");
    }
  }
}
