package org.crochet.constant;

import java.util.HashMap;
import java.util.Map;

public class MessageCodeConstant {
    public static final Map<String, Integer> MAP_CODE = new HashMap<>();

    static {
        MAP_CODE.put(MessageConstant.ACTIVE_NOW, 1);
        MAP_CODE.put(MessageConstant.CLICK_TO_ACTIVE_CONTENT, 2);
        MAP_CODE.put(MessageConstant.CONFIRM_YOUR_EMAIL, 3);
        MAP_CODE.put(MessageConstant.NOT_HAVE_PERMISSION_TO_ACCESS_MESSAGE, 4);
        MAP_CODE.put(MessageConstant.MSG_CATEGORY_NOT_FOUND, 5);
        MAP_CODE.put(MessageConstant.EMAIL_NOT_VERIFIED_MESSAGE, 6);
        MAP_CODE.put(MessageConstant.INCORRECT_PASSWORD_MESSAGE, 7);
        MAP_CODE.put(MessageConstant.EMAIL_ADDRESS_ALREADY_IN_USE_MESSAGE, 8);
        MAP_CODE.put(MessageConstant.USER_NOT_FOUND_WITH_EMAIL_MESSAGE, 9);
        MAP_CODE.put(MessageConstant.USER_NOT_FOUND_WITH_ID_MESSAGE, 10);
        MAP_CODE.put(MessageConstant.USER_REGISTERED_SUCCESSFULLY_MESSAGE, 11);
        MAP_CODE.put(MessageConstant.RESEND_SUCCESSFULLY_MESSAGE, 12);
        MAP_CODE.put(MessageConstant.EMAIL_ALREADY_CONFIRMED_MESSAGE, 13);
        MAP_CODE.put(MessageConstant.TOKEN_EXPIRED_MESSAGE, 14);
        MAP_CODE.put(MessageConstant.SUCCESSFUL_CONFIRMATION_MESSAGE, 15);
        MAP_CODE.put(MessageConstant.LINK_RESET_PASSWORD, 16);
        MAP_CODE.put(MessageConstant.PASSWORD_RESET_TOKEN_IS_EXPIRED_MESSAGE, 17);
        MAP_CODE.put(MessageConstant.RESET_PASSWORD_SUCCESSFULLY_MESSAGE, 18);
        MAP_CODE.put(MessageConstant.REFRESH_TOKEN_IS_NOT_IN_DB_MESSAGE, 19);
        MAP_CODE.put(MessageConstant.BLOG_NOT_FOUND_MESSAGE, 20);
        MAP_CODE.put(MessageConstant.PLEASE_LOGIN_TO_COMMENT_MESSAGE, 21);
        MAP_CODE.put(MessageConstant.USER_NOT_FOUND_MESSAGE, 22);
        MAP_CODE.put(MessageConstant.COMMENT_NOT_FOUND_MESSAGE, 23);
        MAP_CODE.put(MessageConstant.CONFIRM_TOKEN_NOT_FOUND_MESSAGE, 24);
        MAP_CODE.put(MessageConstant.FAILED_TO_SEND_EMAIL_MESSAGE, 25);
        MAP_CODE.put(MessageConstant.FREE_PATTERN_NOT_FOUND_MESSAGE, 26);
        MAP_CODE.put(MessageConstant.PATTERN_NOT_FOUND_MESSAGE, 27);
        MAP_CODE.put(MessageConstant.PAYMENT_SUCCESS_MESSAGE, 28);
        MAP_CODE.put(MessageConstant.MSG_ORDER_PATTERN_MISSING, 29);
        MAP_CODE.put(MessageConstant.REQUIRE_LOGIN_TO_ORDER_PATTERN, 30);
        MAP_CODE.put(MessageConstant.USER_NOT_FOUND_WITH_TOKEN_MESSAGE, 31);
        MAP_CODE.put(MessageConstant.PASSWORD_RESET_TOKEN_NOT_FOUND_MESSAGE, 32);
        MAP_CODE.put(MessageConstant.USER_NOT_LOGGED_IN_MESSAGE, 33);
        MAP_CODE.put(MessageConstant.MSG_PAYMENT_PATTERN_REQUIRED, 34);
        MAP_CODE.put(MessageConstant.MSG_PRODUCT_NOT_FOUND, 35);
        MAP_CODE.put(MessageConstant.MSG_REFRESH_TOKEN_EXPIRED, 36);
        MAP_CODE.put(MessageConstant.MSG_REFRESH_TOKEN_NOT_FOUND, 37);
        MAP_CODE.put(MessageConstant.ERROR_PARENT_CATEGORY_EXISTS, 38);
        MAP_CODE.put(MessageConstant.ERROR_CHILD_CATEGORY_EXISTS, 39);
        MAP_CODE.put(MessageConstant.UNAUTHORIZED, 40);
        MAP_CODE.put(MessageConstant.FORBIDDEN, 41);
        MAP_CODE.put(MessageConstant.DATA_INTEGRITY_VIOLATION, 42);
    }

    private MessageCodeConstant() {
    }
}
