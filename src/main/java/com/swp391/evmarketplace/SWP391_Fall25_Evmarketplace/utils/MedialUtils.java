package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MedialUtils {

    private static String serverUrl;

    @Value("${server.url}")
    public void setServerUrl(String url) {
        MedialUtils.serverUrl = url;
    }

    private static final String END_POINT_IMAGE = "/api/files/images/";
    private static final String END_POINT_VIDEO = "/api/files/videos/";
    private static final String END_POINT_CONTRACT = "/api/files/contract/";

    public static String converMediaNametoMedialUrl(String mediaName, String mediaType) {
        String base = serverUrl != null ? serverUrl : "";

        if (mediaName != null && mediaName.startsWith("http")) {
            return mediaName;
        }

        if (MediaType.IMAGE.name().equalsIgnoreCase(mediaType)) {
            return base + END_POINT_IMAGE + mediaName;
        } else if (MediaType.VIDEO.name().equalsIgnoreCase(mediaType)) {
            return base + END_POINT_VIDEO + mediaName;
        } else {
            return base + END_POINT_CONTRACT + mediaName;
        }
    }

}
