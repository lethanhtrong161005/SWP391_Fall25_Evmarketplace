package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface StorageService {
    String save(String bucket, String key, MultipartFile file, boolean replaceAll);
    Resource load(String bucket, String key, String filename);
    List<String> list(String bucket, String key);
    boolean delete(String bucket, String key, String filename);    // filename=null => delete all
}
