package org.crochet.util;

import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ConvertUtils {
    @SneakyThrows
    public static List<String> convertMultipartToString(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (var file : files) {
            urls.add(Base64.getEncoder().encodeToString(file.getBytes()));
        }
        return urls;
    }
}
