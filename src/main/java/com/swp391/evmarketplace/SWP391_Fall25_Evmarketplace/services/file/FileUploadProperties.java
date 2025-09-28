package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file-upload")
public class FileUploadProperties {
    private RootPath rootPath = new RootPath();
}
