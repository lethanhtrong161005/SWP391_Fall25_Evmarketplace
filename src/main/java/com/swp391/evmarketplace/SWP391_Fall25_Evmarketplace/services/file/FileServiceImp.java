package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.storage.LocalStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Service
public class FileServiceImp implements FileService {

    @Autowired
    private LocalStorageService storage;

    @Value("${fileUpload.rootPath}")
    private String rootPath;

    private static final String BUCKET_LISTINGS = "listings";
    private static final String BUCKET_AVATARS  = "avatars";

    private Path baseListings() { return Paths.get(rootPath, "listings"); }



    /* ===== AVATAR (single) ===== */
    @Override
    public String saveOrReplaceAvatar(Long accountId, MultipartFile file) {
        // replaceAll = true -> xoá hết trước khi lưu 1 file (avatar)
        return storage.save(BUCKET_AVATARS, String.valueOf(accountId), file, true);
    }

    @Override
    public Resource loadAvatar(Long accountId) {
        // filename=null => storage sẽ lấy file đầu tiên (avatar duy nhất)
        return storage.load(BUCKET_AVATARS, String.valueOf(accountId), null);
    }

    @Override
    public boolean deleteAvatar(Long accountId) {
        // filename=null => xóa tất cả file trong thư mục avatar của user
        return storage.delete(BUCKET_AVATARS, String.valueOf(accountId), null);
    }

}
