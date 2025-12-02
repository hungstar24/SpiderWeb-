package com.mycompany.spiderweb;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void openBrowser(String url) {
        if (url == null || url.isBlank()) return;
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception ignored) {}
    }

    public static String hostOf(String url) {
        try { return URI.create(url).getHost(); }
        catch (Exception e) { return url; }
    }

    public static List<String> collectKeywords(String k1, String k2, String k3) {
        List<String> ks = new ArrayList<>();
        addIfNotBlank(ks, k1);
        addIfNotBlank(ks, k2);
        addIfNotBlank(ks, k3);
        return ks;
    }

    private static void addIfNotBlank(List<String> list, String s) {
        if (s == null) return;
        s = s.trim();
        if (!s.isEmpty()) list.add(s);
    }

    public static long pageCount(long total, int size) {
        if (total <= 0) return 1;
        return (total + size - 1) / size;
    }

    public static long pageOf(int start, int size) {
        return ((long) start - 1) / size + 1;
    }
}
