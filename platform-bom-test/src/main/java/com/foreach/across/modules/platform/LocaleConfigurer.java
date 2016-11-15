package com.foreach.across.modules.platform;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * @author Marc Vanbrabant
 */
@Configuration
public class LocaleConfigurer extends WebMvcConfigurerAdapter
{
	@Override
	public void addInterceptors( InterceptorRegistry registry ) {
		// allow language to be changed by providing a request parameter 'language' with the locale string
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName( "language" );
		localeChangeInterceptor.setIgnoreInvalidLocale( true );

		registry.addInterceptor( localeChangeInterceptor );
	}

	@Bean
	public CookieLocaleResolver localeResolver() {
		// read (and store) the selected locale from cookie
		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale( Locale.ENGLISH );
		return cookieLocaleResolver;
	}
}
