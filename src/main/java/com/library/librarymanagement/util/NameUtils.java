package com.library.librarymanagement.util;

public class NameUtils {
    public static String compactSpaces(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("\\s+", " "); // gộp mọi loại whitespace rồi trim
    }

}
