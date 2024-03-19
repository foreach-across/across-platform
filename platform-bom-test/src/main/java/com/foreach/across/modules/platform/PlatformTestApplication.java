package com.foreach.across.modules.platform;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminwebthemes.AdminWebThemesModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.ehcache.EhcacheModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.ldap.LdapModule;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.webcms.WebCmsModule;
import com.foreach.across.test.support.config.ResetDatabaseConfigurer;
import com.foreach.across.test.support.config.TestDataSourceConfigurer;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@EnableWebSecurity(debug = true)
@Configuration
@AcrossApplication(modules = {
		AdminWebModule.NAME,
		ApplicationInfoModule.NAME,
		AdminWebThemesModule.NAME,
		DebugWebModule.NAME,
		EhcacheModule.NAME,
		EntityModule.NAME,
		FileManagerModule.NAME,
		// Error: "Unable to resolve module ImageServerAdminWebModule":
		//ImageServerAdminWebModule.NAME,
		//ImageServerCoreModule.NAME,
		LdapModule.NAME,
		LoggingModule.NAME,
		UserModule.NAME,
		WebCmsModule.NAME,
}
)
@Import({ TestDataSourceConfigurer.class, ResetDatabaseConfigurer.class, LocaleConfigurer.class })
public class PlatformTestApplication extends SpringBootServletInitializer
{

	public static void main( String[] args ) {
		SpringApplication.run( PlatformTestApplication.class, args );
	}

/*
	@Bean
	public OAuth2Module oauth2module() {
		OAuth2Module oauth2module = new OAuth2Module();
		oauth2module.expose( AuthorizationServerTokenServices.class, AuthorizationServerEndpointsConfiguration.class,
		                     TokenStore.class );
		return oauth2module;
	}
*/

	@Bean
	public AcrossHibernateJpaModule acrossHibernateJpaModule() {
		AcrossHibernateJpaModule acrossHibernateJpaModule = new AcrossHibernateJpaModule();
//		if ( "mssql-acrossplatform".equals( System.getProperty( "acrossTest.datasource" ) ) ) {
//			acrossHibernateJpaModule.setHibernateProperty( AvailableSettings.DIALECT,
//			                                               SQLServer2008Dialect.class.getName() );
//		}
		return acrossHibernateJpaModule;
	}
}