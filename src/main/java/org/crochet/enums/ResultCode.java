package org.crochet.enums;

public enum ResultCode {

    ACTIVE_NOW(1, "Active now"),
    MSG_ACCOUNT_ACTIVATION_LINK(2, "Thank you for registering. Please click on the below link to activate your account:"),
    CONFIRM_YOUR_EMAIL(3, "Confirm your email"),
    MSG_CATEGORY_NOT_FOUND(5, "Category not found"),
    MSG_EMAIL_NOT_VERIFIED(6, "Email not verified"),
    MSG_INCORRECT_PASSWORD(7, "Incorrect password"),
    MSG_EMAIL_ALREADY_IN_USE(8, "Email address already in use"),
    MSG_USER_NOT_FOUND_WITH_EMAIL(9, "User not found with email "),
    MSG_USER_NOT_FOUND_WITH_ID(10, "User not found with id "),
    MSG_USER_REGISTER_SUCCESS(11, "User register success"),
    MSG_RESEND_SUCCESS(12, "Resend success"),
    MSG_EMAIL_ALREADY_CONFIRMED(13, "Email already confirmed"),
    MSG_TOKEN_EXPIRED(14, "Token expired"),
    MSG_SUCCESSFUL_CONFIRMATION(15, "Xác thực thành công (Verified success)"),
    RESET_PASSWORD_LINK(16, "Send successfully with link reset password: "),
    MSG_PASSWORD_RESET_TOKEN_EXPIRED(17, "Password reset token is expired"),
    MSG_RESET_PASSWORD_SUCCESS(18, "Reset password success"),
    REFRESH_TOKEN_NOT_IN_DB(19, "Refresh Token is not in DB..!!"),
    MSG_BLOG_NOT_FOUND(20, "Blog not found"),
    MSG_USER_NOT_FOUND(22, "User not found"),
    MSG_COMMENT_NOT_FOUND(23, "Comment not found"),
    MSG_CONFIRM_TOKEN_NOT_FOUND(24, "Token not found"),
    MSG_FAILED_SEND_EMAIL(25, "Failed to send email"),
    MSG_FREE_PATTERN_NOT_FOUND(26, "Free pattern not found"),
    MSG_PATTERN_NOT_FOUND(27, "Pattern not found"),
    MSG_USER_NOT_FOUND_WITH_TOKEN(31, "User not found with token: "),
    MSG_PASSWORD_RESET_TOKEN_NOT_FOUND(32, "Password reset token not found"),
    MSG_USER_LOGIN_REQUIRED(33, "User not logged in"),
    MSG_PRODUCT_NOT_FOUND(35, "Product not found"),
    MSG_REFRESH_TOKEN_EXPIRED(36, "Refresh token is expired. Please make a new login..!"),
    MSG_REFRESH_TOKEN_NOT_FOUND(37, "Refresh token not found: "),
    ERROR_PARENT_CATEGORY_EXISTS(38, "A category with the same name already exists as a parent."),
    ERROR_CHILD_CATEGORY_EXISTS(39, "A category with the same name already exists as a child."),
    ERROR_IMAGE_UPLOAD_FAILED(40, "Cannot upload image to Firebase Cloud Storage"),
    MSG_BANNER_TYPE_NOT_FOUND(41, "Banner type not found"),
    MSG_BANNER_NOT_FOUND(42, "Banner not found"),
    MSG_BLOG_CATEGORY_NOT_FOUND(43, "Blog category not found."),
    MSG_SETTINGS_NOT_FOUND(44, "Settings not found"),
    MSG_FORBIDDEN(45, "No permission to access this resource"),
    MSG_COLLECTION_NOT_FOUND(47, "Collection not found"),
    MSG_NO_PERMISSION_MODIFY_COLLECTION(49, "No permission to modify this collection"),
    MSG_NO_PERMISSION_DELETE_COLLECTION(51, "No permission to delete this collection"),
    MSG_NO_PERMISSION_REMOVE_FREE_PATTERN_FROM_COLLECTION(52, "No permission to remove free pattern from collection"),
    MSG_DELETE_SUCCESS(53, "Delete successfully"),
    MSG_CREATE_OR_UPDATE_SUCCESS(54, "Create or update successfully"),
    DATA_INTEGRITY_VIOLATION(55, "Data integrity violation"),
    RESET_NOTIFICATION(56, "Reset notification"),
    MSG_RESET_PASSWORD_LINK(57, "Reset password link"),
    RESET_PASSWORD(58, "Reset password"),
    MSG_DUPLICATE_CATEGORY_NAME_UNDER_PROVIDED_PARENTS(59, "Duplicate category name under provided parents"),
    MSG_LOGOUT_SUCCESS(60, "Logged out success"),
    MSG_NO_PERMISSION_UPDATE_CATEGORY(62, "No permission to update this category"),
    MSG_NO_PERMISSION(63, "No permission"),
    MSG_NO_PERMISSION_DELETE_CATEGORY(61, "No permission to delete this category");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
