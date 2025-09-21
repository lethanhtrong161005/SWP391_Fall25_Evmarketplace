package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    // LISTING MEDIA
    String saveListingFile(Long listingId, MultipartFile file);
    Resource loadListingFile(Long listingId, String filename);
    List<String> listListingFiles(Long listingId);
    boolean deleteListingFile(Long listingId, String filename);

    // AVATAR (single)
    String saveOrReplaceAvatar(Long accountId, MultipartFile file);
    Resource loadAvatar(Long accountId);
    boolean deleteAvatar(Long accountId);
}
