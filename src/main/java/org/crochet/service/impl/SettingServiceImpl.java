package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Settings;
import org.crochet.payload.request.SettingRequest;
import org.crochet.payload.response.SettingResponse;
import org.crochet.repository.SettingsRepo;
import org.crochet.service.SettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SettingServiceImpl implements SettingService {
    private final SettingsRepo settingsRepo;

    /**
     * Create a new setting
     *
     * @param request SettingRequest
     */
    @Transactional
    @Override
    public void create(SettingRequest request) {
        Settings settings = Settings.builder()
                .key(request.getKey())
                .value(request.getValue())
                .build();
        settingsRepo.save(settings);
    }

    /**
     * Update an existing setting
     *
     * @param request SettingRequest
     */
    @Transactional
    @Override
    public void update(SettingRequest request) {
        Settings settings = settingsRepo.findById(request.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.MSG_SETTINGS_NOT_FOUND.message(),
                        ResultCode.MSG_SETTINGS_NOT_FOUND.code()));
        settings.setValue(request.getValue());
        settingsRepo.save(settings);
    }

    /**
     * Delete a setting
     *
     * @param key String
     */
    @Transactional
    @Override
    public void delete(String key) {
        settingsRepo.deleteById(key);
    }

    /**
     * Get all settings
     *
     * @return List<SettingResponse>
     */
    @Override
    public List<SettingResponse> getAll() {
        var settings = settingsRepo.findSettings();
        return settings.stream()
                .map(s -> SettingResponse.builder()
                        .key(s.getKey())
                        .value(s.getValue())
                        .build())
                .toList();
    }

    /**
     * Get a setting by key
     *
     * @param key String
     * @return SettingResponse
     */
    @Override
    public SettingResponse getById(String key) {
        var setting = settingsRepo.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.MSG_SETTINGS_NOT_FOUND.message(),
                        ResultCode.MSG_SETTINGS_NOT_FOUND.code()));
        return SettingResponse.builder()
                .key(setting.getKey())
                .value(setting.getValue())
                .build();
    }
}
