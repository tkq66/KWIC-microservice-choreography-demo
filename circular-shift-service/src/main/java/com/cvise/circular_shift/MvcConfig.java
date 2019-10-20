package com.cvise.circular_shift;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Value("${user.local.static.file.path}")
	private String staticFilePath;
	@Value("${user.local.static.file.endpoint}")
	private String staticFileEndpoint;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry
	      .addResourceHandler(staticFileEndpoint + "**")
	      .addResourceLocations("file:" + staticFilePath);
	 }
}
