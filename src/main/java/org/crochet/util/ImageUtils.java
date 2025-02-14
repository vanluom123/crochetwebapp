package org.crochet.util;

import org.crochet.payload.response.FileResponse;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageUtils {
    /**
     * Sort files by last modified date and assign order
     *
     * @param <T>   Type of file extending FileResponse
     * @param files Collection of files to sort
     * @return Sorted list of files with order assigned, or null if input is empty
     */
    public static <T extends FileResponse> List<T> sortFiles(Collection<T> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        List<T> sortedFiles = files.stream()
            .filter(file -> file.getLastModified() != null)
            .sorted(Comparator.comparing(FileResponse::getLastModified))
            .toList();

        // Use an AtomicInteger to assign order
        AtomicInteger order = new AtomicInteger(0);
        sortedFiles.forEach(file -> file.setOrder(order.getAndIncrement()));

        return sortedFiles;
    }
}
