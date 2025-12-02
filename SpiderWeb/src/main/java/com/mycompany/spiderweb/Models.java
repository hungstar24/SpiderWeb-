package com.mycompany.spiderweb;

import java.util.List;
import java.util.prefs.Preferences;

public class Models {

    public record ResultItem(String title, String link, String snippet, String displayLink) { }
    public record DomainItem(String domain, int count) { }
    public record SearchResponse(long totalResults, List<ResultItem> items) { }

    public static class SettingsStore {
        private static final Preferences PREF = Preferences.userNodeForPackage(SettingsStore.class);
        private static final String PREF_KEY = "GOOGLE_CSE_KEY";
        private static final String PREF_CX  = "GOOGLE_CSE_CX";

        private String apiKey = PREF.get(PREF_KEY, "");
        private String cx     = PREF.get(PREF_CX, "");

        public boolean isReady() { return !apiKey.isBlank() && !cx.isBlank(); }

        public String apiKey() { return apiKey; }
        public String cx() { return cx; }

        public void save(String key, String cx) {
            this.apiKey = key == null ? "" : key.trim();
            this.cx = cx == null ? "" : cx.trim();
            PREF.put(PREF_KEY, this.apiKey);
            PREF.put(PREF_CX, this.cx);
        }
    }
}
