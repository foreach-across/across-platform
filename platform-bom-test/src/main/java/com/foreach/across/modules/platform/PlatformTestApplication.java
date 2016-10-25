package com.foreach.across.modules.platform;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.ehcache.EhcacheModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.spring.batch.SpringBatchModule;
import com.foreach.across.modules.spring.mobile.SpringMobileModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.test.support.config.ResetDatabaseConfigurer;
import com.foreach.across.test.support.config.TestDataSourceConfigurer;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@AcrossApplication(modules = {
		DebugWebModule.NAME, EhcacheModule.NAME, UserModule.NAME, LoggingModule.NAME, SpringMobileModule.NAME,
		ApplicationInfoModule.NAME, SpringBatchModule.NAME, FileManagerModule.NAME, EntityModule.NAME,
		AdminWebModule.NAME,
		SpringSecurityAclModule.NAME
}
)
@Import({ TestDataSourceConfigurer.class, ResetDatabaseConfigurer.class })
public class PlatformTestApplication extends SpringBootServletInitializer
{

	public static void main( String[] args ) {
		SpringApplication.run( PlatformTestApplication.class );
	}

	@Bean
	public OAuth2Module oauth2module() {
		OAuth2Module oauth2module = new OAuth2Module();
		oauth2module.expose( AuthorizationServerTokenServices.class, AuthorizationServerEndpointsConfiguration.class,
		                     TokenStore.class );
		return oauth2module;
	}

	@Bean
	public AcrossHibernateJpaModule acrossHibernateJpaModule() {
		AcrossHibernateJpaModule acrossHibernateJpaModule = new AcrossHibernateJpaModule();
		if ( "mssql-acrossplatform".equals( System.getProperty( "acrossTest.datasource" ) ) ) {
			acrossHibernateJpaModule.setHibernateProperty( AvailableSettings.DIALECT,
			                                               SQLServer2008Dialect.class.getName() );
		}
		return acrossHibernateJpaModule;
	}
}