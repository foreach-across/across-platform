package com.foreach.across.modules.platform.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
public class ApiResourcesConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("platform-test-app");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**")
                .authorizeRequests().anyRequest().authenticated();
    }
}

