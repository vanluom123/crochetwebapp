package org.crochet.util;

import org.crochet.payload.response.FileResponse;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ImageUtils {
    /**
     * Sort files by last modified date
     *
     * @param <T> Type of file
     * @param files List of files
     * @return List<T>
     */
    public static <T extends FileResponse> List<T> sortFiles(Collection<T> files) {
        List<T> sortedFiles = files.stream()
                .sorted(Comparator.comparing(FileResponse::getLastModified))
                .toList();
        for (int i = 0; i < sortedFiles.size(); i++) {
            sortedFiles.get(i).setOrder(i);
        }
        return sortedFiles;
    }
}
