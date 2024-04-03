package com.foreach.across.modules.platform.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.oauth2.OAuth2Module;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

//@ModuleConfiguration(OAuth2Module.NAME)
//@Configuration
public class ApiResourcesConfiguration extends ResourceServerConfigurerAdapter
{
	@Override
	public void configure( ResourceServerSecurityConfigurer resources ) throws Exception {
		resources.resourceId( "platform-test-app" );
	}

	@Override
	public void configure( HttpSecurity http ) throws Exception {
		http
				.requestMatcher(
						new OrRequestMatcher( new AntPathRequestMatcher( "/api/**" ),
						                      new AntPathRequestMatcher( "/oauth/user_token" ) )
				)
				.authorizeRequests().anyRequest().authenticated()
				.and()
				.headers().cacheControl().and().xssProtection().and()
				.and()
				.anonymous();
	}
}

