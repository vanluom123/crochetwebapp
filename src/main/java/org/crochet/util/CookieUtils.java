package org.crochet.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

  /**
   * Retrieves a cookie by its name from the provided HttpServletRequest.
   *
   * @param request The HttpServletRequest to retrieve the cookie from.
   * @param name    The name of the cookie to retrieve.
   * @return An Optional containing the cookie if found, or an empty Optional if not found.
   */
  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    // Get all the cookies from the request
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      // Iterate over the cookies array
      for (Cookie cookie : cookies) {
        // Check if the current cookie matches the provided name
        if (cookie.getName().equals(name)) {
          // Return an Optional containing the cookie
          return Optional.of(cookie);
        }
      }
    }

    // Return an empty Optional if the cookie is not found
    return Optional.empty();
  }


  /**
   * Add cookie
   *
   * @param response HttpServletResponse
   * @param name name
   * @param value value
   * @param maxAge max age
   */
  public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  /**
   * Deletes a cookie by its name from the provided HttpServletRequest and HttpServletResponse.
   *
   * @param request  The HttpServletRequest to delete the cookie from.
   * @param response The HttpServletResponse to add the modified cookie.
   * @param name     The name of the cookie to delete.
   */
  public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
    // Get all the cookies from the request
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      // Iterate over the cookies array
      for (Cookie cookie : cookies) {
        // Check if the current cookie matches the provided name
        if (cookie.getName().equals(name)) {
          // Clear the value, set the path to root, and set max age to 0 to delete the cookie
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);

          // Add the modified cookie to the response to delete it
          response.addCookie(cookie);
        }
      }
    }
  }


  /**
   * Serialize
   *
   * @param object object
   * @return Serialize string
   */
  public static String serialize(Object object) {
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(object));
  }

  /**
   * Deserialize
   *
   * @param cookie Cookie
   * @param cls class type
   * @return deserialize string
   */
  public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    return cls.cast(SerializationUtils.deserialize(
        Base64.getUrlDecoder().decode(cookie.getValue())));
  }
}
