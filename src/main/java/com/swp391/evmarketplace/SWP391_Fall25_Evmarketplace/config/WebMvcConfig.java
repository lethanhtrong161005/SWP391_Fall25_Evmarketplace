package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> c : converters) {
            if (c instanceof MappingJackson2HttpMessageConverter mj) {
                List<MediaType> types = new ArrayList<>(mj.getSupportedMediaTypes());
                types.add(MediaType.APPLICATION_OCTET_STREAM);
                types.add(MediaType.TEXT_PLAIN);
                mj.setSupportedMediaTypes(types);
            }
        }
    }

}
