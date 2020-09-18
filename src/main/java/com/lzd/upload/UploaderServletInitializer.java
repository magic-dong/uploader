package com.lzd.upload;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


/**
 * web容器中进行部署
 * @author lzd
 * @date 2019年3月29日
 * @version
 */
public class UploaderServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UploaderApplication.class);
    }

}
