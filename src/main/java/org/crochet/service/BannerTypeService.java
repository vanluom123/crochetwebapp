package org.crochet.service;

import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;

import java.util.List;

public interface BannerTypeService {
    BannerTypeResponse createOrUpdate(BannerTypeRequest request);

    void delete(String id);

    List<BannerTypeResponse> getAll();

    BannerTypeResponse getById(String id);
}
