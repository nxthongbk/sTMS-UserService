package com.scity.user.config;

import com.scity.user.interceptor.RequestHeaders;
import com.scity.user.interceptor.RestTemplateRequestInterceptor;
import com.scity.user.interceptor.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonBeanInitConfig {

  @Autowired
  private RequestHeaders requestHeaders;

  @Bean
  public SessionHelper sessionHelper() {
    return new SessionHelper(requestHeaders);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    RestTemplate restTemplate = builder.build();
    restTemplate.getInterceptors().add(new RestTemplateRequestInterceptor());
    return restTemplate;
  }
}
