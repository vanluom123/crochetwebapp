package org.crochet.util;

import org.crochet.model.Settings;
import org.crochet.repository.SettingsRepo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class SettingsUtil {
    private final SettingsRepo settingsRepo;
    private volatile Map<String, Settings> settingsCache = null;

    public SettingsUtil(SettingsRepo settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    /**
     * Retrieves all settings as a map with setting key as map key
     * Uses an in-memory cache to avoid repeated database queries
     *
     * @return Map of settings with key-value pairs, or empty map if no settings found
     */
    public Map<String, Settings> getSettingsMap() {
        if (settingsCache == null) {
            synchronized (this) {
                if (settingsCache == null) {
                    refreshCache();
                }
            }
        }
        return new ConcurrentHashMap<>(settingsCache);
    }

    /**
     * Refreshes the settings cache by fetching all settings from the repository
     * and storing them in a map with setting key as map key
     */
    public synchronized void refreshCache() {
        var settings = settingsRepo.findSettings();
        settingsCache = ObjectUtils.toMap(settings, Settings::getKey, Function.identity());
    }

    /**
     * Clears the settings cache, forcing a refresh on the next access
     */
    public synchronized void clearCache() {
        settingsCache = null;
    }
}
