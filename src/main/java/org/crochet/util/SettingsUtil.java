package org.crochet.util;

import org.crochet.model.Settings;
import org.crochet.repository.SettingsRepo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SettingsUtil {
    private final SettingsRepo settingsRepo;

    public SettingsUtil(SettingsRepo settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    public Map<String, Settings> getSettingsMap() {
        List<Settings> settings = settingsRepo.findSettings();
        if (settings == null || settings.isEmpty()) {
            return Collections.emptyMap();
        }
        return settings.stream()
                .collect(Collectors.toMap(Settings::getKey, Function.identity()));
    }
}
