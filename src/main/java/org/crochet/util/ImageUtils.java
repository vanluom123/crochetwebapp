package org.crochet.util;

import org.crochet.payload.response.FileResponse;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ImageUtils {
    /**
     * Sort files by last modified date
     *
     * @param <T>   Type of file
     * @param files List of files
     * @return List<T>
     */
    public static <T extends FileResponse> List<T> sortFiles(Collection<T> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        return files.stream()
                .filter(f -> f.getLastModified() != null)
                .sorted(Comparator.comparing(FileResponse::getLastModified))
                .peek(f -> f.setOrder(files.stream()
                        .filter(file -> file.getLastModified() != null)
                        .sorted(Comparator.comparing(FileResponse::getLastModified))
                        .toList()
                        .indexOf(f)))
                .toList();
    }
}
