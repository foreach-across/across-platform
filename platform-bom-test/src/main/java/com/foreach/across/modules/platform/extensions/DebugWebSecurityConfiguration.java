package com.foreach.across.modules.platform.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@ModuleConfiguration(DebugWebModule.NAME)
public class DebugWebSecurityConfiguration extends SpringSecurityWebConfigurerAdapter {

    public static final String DEBUG_USERNAME = "debug";
    public static final String DEBUG_PASSWORD = "test";

    @Autowired
    private DebugWeb debugWeb;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(DEBUG_USERNAME).password(DEBUG_PASSWORD).roles("DEBUG_USER");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher(debugWeb.path("/**"))
                // Allow a set of IPs without a password and allow non-known IPs with a password
                .authorizeRequests().anyRequest().access("hasRole('ROLE_DEBUG_USER')")
                .and()
                .formLogin().disable()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();
    }
}
