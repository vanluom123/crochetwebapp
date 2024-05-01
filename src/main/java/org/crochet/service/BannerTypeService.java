package org.crochet.service;

import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;

import java.util.List;
import java.util.UUID;

public interface BannerTypeService {
    BannerTypeResponse createOrUpdate(BannerTypeRequest request);

    void delete(UUID id);

    List<BannerTypeResponse> getAll();

    BannerTypeResponse getById(UUID id);
}
