package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private int status;
    private boolean success;
    private String message;
    private T data;
    private Map<String, List<String>> fieldErrors;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
}
