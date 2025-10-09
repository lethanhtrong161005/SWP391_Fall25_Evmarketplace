package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import org.springframework.beans.factory.annotation.Value;

public class MedialUtils {
    private static String END_POINT_IMAGE = "/api/files/images/";
    private static String END_POINT_VIDEO = "/api/files/videos/";

    public static String converMediaNametoMedialUrl(String mediaName, String mediaType, String serverUrl) {
        String medialUrl = "";
        if(MediaType.IMAGE.name().equalsIgnoreCase(mediaType)){
            medialUrl = serverUrl + END_POINT_IMAGE + mediaName;
        }else if(MediaType.VIDEO.name().equalsIgnoreCase(mediaType)){
            medialUrl = serverUrl + END_POINT_VIDEO + mediaName;
        }
        return medialUrl;
    }

}
