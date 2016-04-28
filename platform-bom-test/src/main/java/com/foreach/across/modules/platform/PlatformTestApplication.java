package com.foreach.across.modules.platform;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.ehcache.EhcacheModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.spring.batch.SpringBatchModule;
import com.foreach.across.modules.spring.mobile.SpringMobileModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.user.UserModule;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

@Configuration
@AcrossApplication(modules = {
        DebugWebModule.NAME, EhcacheModule.NAME, UserModule.NAME, LoggingModule.NAME, SpringMobileModule.NAME,
        ApplicationInfoModule.NAME, SpringBatchModule.NAME, FileManagerModule.NAME, EntityModule.NAME, AdminWebModule.NAME,
        SpringSecurityAclModule.NAME
})
public class PlatformTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformTestApplication.class);
    }

    @Bean
    public DataSource acrossDataSource(ConfigurableEnvironment configurableEnvironment) {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }

    @Bean
    public OAuth2Module oauth2module() {
        OAuth2Module oauth2module = new OAuth2Module();
        oauth2module.expose(AuthorizationServerTokenServices.class, AuthorizationServerEndpointsConfiguration.class, TokenStore.class);
        return oauth2module;
    }
}