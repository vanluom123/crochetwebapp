package org.crochet.service;

import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;

import java.util.List;
import java.util.UUID;

public interface BannerService {
    BannerResponse createOrUpdateBanner(BannerRequest request);

    void delete(UUID id);

    List<BannerResponse> getAll();

    BannerResponse getById(UUID id);

    List<BannerResponse> getAllByType(String bannerTypeName);

    List<BannerResponse> getAllByType(UUID bannerTypeId);
}
