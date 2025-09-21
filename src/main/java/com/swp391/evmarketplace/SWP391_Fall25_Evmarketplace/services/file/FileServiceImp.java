package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.storage.LocalStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@Service
public class FileServiceImp implements FileService {

    @Autowired
    private LocalStorageService storage;

    private static final String BUCKET_LISTINGS = "listings";
    private static final String BUCKET_AVATARS  = "avatars";

    /* ===== LISTING MEDIA ===== */
    @Override
    public String saveListingFile(Long listingId, MultipartFile file) {
        return storage.save(BUCKET_LISTINGS, String.valueOf(listingId), file, false);
    }

    @Override
    public Resource loadListingFile(Long listingId, String filename) {
        return storage.load(BUCKET_LISTINGS, String.valueOf(listingId), filename);
    }

    @Override
    public List<String> listListingFiles(Long listingId) {
        return storage.list(BUCKET_LISTINGS, String.valueOf(listingId));
    }

    @Override
    public boolean deleteListingFile(Long listingId, String filename) {
        return storage.delete(BUCKET_LISTINGS, String.valueOf(listingId), filename);
    }

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
