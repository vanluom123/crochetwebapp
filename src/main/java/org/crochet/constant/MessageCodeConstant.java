package org.crochet.constant;

import java.util.HashMap;
import java.util.Map;

public class MessageCodeConstant {
    public static final Map<String, Integer> MAP_CODE = new HashMap<>();

    static {
        MAP_CODE.put(MessageConstant.ACTIVE_NOW, 1);
        MAP_CODE.put(MessageConstant.MSG_ACCOUNT_ACTIVATION_LINK, 2);
        MAP_CODE.put(MessageConstant.CONFIRM_YOUR_EMAIL, 3);
        MAP_CODE.put(MessageConstant.MSG_NO_PERMISSION, 4);
        MAP_CODE.put(MessageConstant.MSG_CATEGORY_NOT_FOUND, 5);
        MAP_CODE.put(MessageConstant.MSG_EMAIL_NOT_VERIFIED, 6);
        MAP_CODE.put(MessageConstant.MSG_INCORRECT_PASSWORD, 7);
        MAP_CODE.put(MessageConstant.MSG_EMAIL_ALREADY_IN_USE, 8);
        MAP_CODE.put(MessageConstant.MSG_USER_NOT_FOUND_WITH_EMAIL, 9);
        MAP_CODE.put(MessageConstant.MSG_USER_NOT_FOUND_WITH_ID, 10);
        MAP_CODE.put(MessageConstant.MSG_USER_REGISTER_SUCCESS, 11);
        MAP_CODE.put(MessageConstant.MSG_RESEND_SUCCESS, 12);
        MAP_CODE.put(MessageConstant.MSG_EMAIL_ALREADY_CONFIRMED, 13);
        MAP_CODE.put(MessageConstant.MSG_TOKEN_EXPIRED, 14);
        MAP_CODE.put(MessageConstant.MSG_SUCCESSFUL_CONFIRMATION, 15);
        MAP_CODE.put(MessageConstant.RESET_PASSWORD_LINK, 16);
        MAP_CODE.put(MessageConstant.MSG_PASSWORD_RESET_TOKEN_EXPIRED, 17);
        MAP_CODE.put(MessageConstant.MSG_RESET_PASSWORD_SUCCESS, 18);
        MAP_CODE.put(MessageConstant.REFRESH_TOKEN_NOT_IN_DB, 19);
        MAP_CODE.put(MessageConstant.MSG_BLOG_NOT_FOUND, 20);
        MAP_CODE.put(MessageConstant.MSG_LOGIN_TO_COMMENT, 21);
        MAP_CODE.put(MessageConstant.MSG_USER_NOT_FOUND, 22);
        MAP_CODE.put(MessageConstant.MSG_COMMENT_NOT_FOUND, 23);
        MAP_CODE.put(MessageConstant.MSG_CONFIRM_TOKEN_NOT_FOUND, 24);
        MAP_CODE.put(MessageConstant.MSG_FAILED_SEND_EMAIL, 25);
        MAP_CODE.put(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND, 26);
        MAP_CODE.put(MessageConstant.MSG_PATTERN_NOT_FOUND, 27);
        MAP_CODE.put(MessageConstant.MSG_PAYMENT_SUCCESS, 28);
        MAP_CODE.put(MessageConstant.MSG_ORDER_PATTERN_MISSING, 29);
        MAP_CODE.put(MessageConstant.REQUIRE_LOGIN_TO_ORDER_PATTERN, 30);
        MAP_CODE.put(MessageConstant.MSG_USER_NOT_FOUND_WITH_TOKEN, 31);
        MAP_CODE.put(MessageConstant.MSG_PASSWORD_RESET_TOKEN_NOT_FOUND, 32);
        MAP_CODE.put(MessageConstant.MSG_USER_LOGIN_REQUIRED, 33);
        MAP_CODE.put(MessageConstant.MSG_PAYMENT_PATTERN_REQUIRED, 34);
        MAP_CODE.put(MessageConstant.MSG_PRODUCT_NOT_FOUND, 35);
        MAP_CODE.put(MessageConstant.MSG_REFRESH_TOKEN_EXPIRED, 36);
        MAP_CODE.put(MessageConstant.MSG_REFRESH_TOKEN_NOT_FOUND, 37);
        MAP_CODE.put(MessageConstant.ERROR_PARENT_CATEGORY_EXISTS, 38);
        MAP_CODE.put(MessageConstant.ERROR_CHILD_CATEGORY_EXISTS, 39);
        MAP_CODE.put(MessageConstant.UNAUTHORIZED, 40);
        MAP_CODE.put(MessageConstant.FORBIDDEN, 41);
        MAP_CODE.put(MessageConstant.DATA_INTEGRITY_VIOLATION, 42);
        MAP_CODE.put(MessageConstant.MSG_DUPLICATE_CATEGORY_NAME_UNDER_PROVIDED_PARENTS, 43);
        MAP_CODE.put(MessageConstant.MSG_BLOG_CATEGORY_NOT_FOUND, 44);
        MAP_CODE.put(MessageConstant.MSG_SETTINGS_NOT_FOUND, 45);
        MAP_CODE.put(MessageConstant.MSG_BANNER_TYPE_NOT_FOUND, 46);
        MAP_CODE.put(MessageConstant.MSG_BANNER_NOT_FOUND, 47);
        MAP_CODE.put(MessageConstant.ERROR_IMAGE_UPLOAD_FAILED, 48);
        MAP_CODE.put(MessageConstant.MSG_FORBIDDEN, 49);
        MAP_CODE.put(MessageConstant.MSG_UNAUTHORIZED, 50);
    }

    private MessageCodeConstant() {
    }
}
