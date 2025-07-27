package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//    @Value("${uploadPath}")
//    String uploadPath;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/images/**").addResourceLocations(uploadPath);
//    }
//}
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 클라이언트: /images/item/abc.jpg → 실제 파일: C:/shop/item/abc.jpg
        registry.addResourceHandler("/images/item/**")
                .addResourceLocations("file:///" + itemImgLocation + "/");
    }
}
