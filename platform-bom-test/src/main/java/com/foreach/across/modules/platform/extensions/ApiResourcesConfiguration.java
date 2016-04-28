package com.foreach.across.modules.platform.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.oauth2.OAuth2Module;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@ModuleConfiguration(OAuth2Module.NAME)
public class ApiResourcesConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("platform-test-app");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(
                        "/api/**",
                        "/oauth/user_token").authenticated()
                .and()
                .headers().cacheControl().and().xssProtection().and()
                .and()
                .anonymous();
    }
}

