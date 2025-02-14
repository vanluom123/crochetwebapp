package org.crochet.util;

import org.crochet.payload.response.ResponseData;
import org.springframework.http.HttpStatus;

import static org.crochet.constant.AppConstant.SUCCESS;

/**
 * Tiện ích để tạo các đối tượng ResponseData chuẩn hóa
 */
public class ResponseUtil {

    private ResponseUtil() {
        // Ngăn khởi tạo
    }

    /**
     * Tạo phản hồi thành công với dữ liệu
     *
     * @param data Dữ liệu cần trả về
     * @param <T>  Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success(T data) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(data)
                .build();
    }

    /**
     * Tạo phản hồi thành công với dữ liệu và mã trạng thái tùy chỉnh
     *
     * @param data   Dữ liệu cần trả về
     * @param status Mã trạng thái HTTP
     * @param <T>    Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success(T data, HttpStatus status) {
        return ResponseData.<T>builder()
                .success(true)
                .code(status.value())
                .message(SUCCESS)
                .data(data)
                .build();
    }

    /**
     * Tạo phản hồi thành công với dữ liệu và thông báo tùy chỉnh
     *
     * @param data    Dữ liệu cần trả về
     * @param message Thông báo
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success(T data, String message) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo phản hồi thành công với dữ liệu, mã trạng thái và thông báo tùy chỉnh
     *
     * @param data    Dữ liệu cần trả về
     * @param status  Mã trạng thái HTTP
     * @param message Thông báo
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success(T data, HttpStatus status, String message) {
        return ResponseData.<T>builder()
                .success(true)
                .code(status.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo phản hồi thành công không có dữ liệu
     *
     * @param <T> Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success() {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .build();
    }

    /**
     * Tạo phản hồi thành công không có dữ liệu với thông báo tùy chỉnh
     *
     * @param message Thông báo
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success(String message) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    /**
     * Tạo phản hồi thành công không có dữ liệu với mã trạng thái và thông báo tùy chỉnh
     *
     * @param status  Mã trạng thái HTTP
     * @param message Thông báo
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> success(HttpStatus status, String message) {
        return ResponseData.<T>builder()
                .success(true)
                .code(status.value())
                .message(message)
                .build();
    }

    /**
     * Tạo phản hồi lỗi
     *
     * @param status  Mã trạng thái HTTP
     * @param message Thông báo lỗi
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> error(HttpStatus status, String message) {
        return ResponseData.<T>builder()
                .success(false)
                .code(status.value())
                .message(message)
                .build();
    }

    /**
     * Tạo phản hồi lỗi với thông tin lỗi chi tiết
     *
     * @param status  Mã trạng thái HTTP
     * @param message Thông báo lỗi
     * @param error   Đối tượng lỗi
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> error(HttpStatus status, String message, Throwable error) {
        return ResponseData.<T>builder()
                .success(false)
                .code(status.value())
                .message(message)
                .error(error)
                .build();
    }

    /**
     * Tạo phản hồi lỗi với mã tùy chỉnh
     *
     * @param code    Mã lỗi tùy chỉnh
     * @param message Thông báo lỗi
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> error(int code, String message) {
        return ResponseData.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    /**
     * Tạo phản hồi lỗi với mã tùy chỉnh và thông tin lỗi chi tiết
     *
     * @param code    Mã lỗi tùy chỉnh
     * @param message Thông báo lỗi
     * @param error   Đối tượng lỗi
     * @param <T>     Kiểu dữ liệu
     * @return Đối tượng ResponseData
     */
    public static <T> ResponseData<T> error(int code, String message, Throwable error) {
        return ResponseData.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .error(error)
                .build();
    }
} 