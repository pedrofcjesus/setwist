package com.setwist.backend.util;

import java.util.Locale;

import com.setwist.backend.model.Song;

public final class SongMatcher {

    private SongMatcher() {
    }

    // Mesma música = mesmo id, ou mesmo título e artista (sem distinguir maiúsculas)
    public static boolean isSameSong(Song a, Song b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getId() != null && a.getId().equals(b.getId())) {
            return true;
        }
        return normalize(a.getTitle()).equals(normalize(b.getTitle()))
                && normalize(a.getArtist()).equals(normalize(b.getArtist()));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}