package org.crochet.service;

import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;

import java.util.List;

public interface BannerService {
    List<BannerResponse> batchInsertOrUpdate(List<BannerRequest> requests);

    List<BannerResponse> getAll();
}
