package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {



    // AVATAR (single)
    String saveOrReplaceAvatar(Long accountId, MultipartFile file);
    Resource loadAvatar(Long accountId);
    boolean deleteAvatar(Long accountId);
}
