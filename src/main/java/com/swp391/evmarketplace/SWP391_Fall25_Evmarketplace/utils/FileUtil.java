package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class FileUtil {

    public static Path baseListings(String rootPath) {
        return Paths.get(rootPath, "listings");
    }

    public static Path baseAvatars(String rootPath) {
        return Paths.get(rootPath, "avatars");
    }

    public static String sanitizeName(String name) {
        String n = (name == null ? "" : name).trim().replace("\\", "/");
        n = n.substring(n.lastIndexOf('/') + 1);
        return n.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static String extByMime(String mime) {
        if ("image/png".equalsIgnoreCase(mime))  return ".png";
        if ("image/webp".equalsIgnoreCase(mime)) return ".webp";
        return ".jpg";
    }


    public static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","image/webp","image/jpg");
    public static final long MAX_SIZE = 10L * 1024 * 1024; // 10MB
    public static final int  MAX_FILES = 20;
}
