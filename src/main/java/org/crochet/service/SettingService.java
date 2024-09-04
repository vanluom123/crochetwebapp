package org.crochet.service;

import org.crochet.payload.request.SettingRequest;
import org.crochet.payload.response.SettingResponse;

import java.util.List;

public interface SettingService {
    void create(SettingRequest request);

    void update(SettingRequest request);

    void delete(String key);

    List<SettingResponse> getAll();

    SettingResponse getById(String key);
}
