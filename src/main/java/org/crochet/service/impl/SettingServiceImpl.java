package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Settings;
import org.crochet.payload.request.SettingRequest;
import org.crochet.payload.response.SettingResponse;
import org.crochet.repository.SettingsRepo;
import org.crochet.service.SettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

@RequiredArgsConstructor
@Service
public class SettingServiceImpl implements SettingService {
    private final SettingsRepo settingsRepo;

    @Transactional
    @Override
    public void create(SettingRequest request) {
        Settings settings = Settings.builder()
                .key(request.getKey())
                .value(request.getValue())
                .build();
        settingsRepo.save(settings);
    }

    @Transactional
    @Override
    public void update(SettingRequest request) {
        Settings settings = settingsRepo.findById(request.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_SETTINGS_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_SETTINGS_NOT_FOUND)));
        settings.setValue(request.getValue());
        settingsRepo.save(settings);
    }

    @Transactional
    @Override
    public void delete(String key) {
        settingsRepo.deleteById(key);
    }

    @Override
    public List<SettingResponse> getAll() {
        var settings = settingsRepo.findAll();
        return settings.stream()
                .map(s -> SettingResponse.builder()
                        .key(s.getKey())
                        .value(s.getValue())
                        .build())
                .toList();
    }

    @Override
    public SettingResponse getById(String key) {
        var setting = settingsRepo.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_SETTINGS_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_SETTINGS_NOT_FOUND)));
        return SettingResponse.builder()
                .key(setting.getKey())
                .value(setting.getValue())
                .build();
    }
}
