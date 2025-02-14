package org.crochet.util;

import org.crochet.model.Settings;
import org.crochet.repository.SettingsRepo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SettingsUtil {
    private final SettingsRepo settingsRepo;

    public SettingsUtil(SettingsRepo settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    /**
     * Retrieves all settings as a map with setting key as map key
     * 
     * @return Map of settings with key-value pairs, or empty map if no settings found
     */
    public Map<String, Settings> getSettingsMap() {
        return Optional.ofNullable(settingsRepo.findSettings())
                .filter(list -> !list.isEmpty())
                .map(settings -> settings.stream()
                        .collect(Collectors.toMap(Settings::getKey, Function.identity())))
                .orElse(Collections.emptyMap());
    }
}
