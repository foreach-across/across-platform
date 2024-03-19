package com.foreach.across.modules.platform.application.config;

import com.foreach.across.modules.debugweb.DebugWebModuleSettings;
import com.foreach.across.modules.debugweb.config.DebugWebSecurityConfiguration;
import com.foreach.across.modules.platform.application.controllers.PreAuthorizedController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Marc Vanbrabant
 * @since 1.1.2
 */
@Configuration
public class PlatformTestApplicationControllerConfiguration
{
	@Bean
	public PreAuthorizedController preAuthorizedController() {
		return new PreAuthorizedController();
	}

//	@Bean
//	public SecurityFilterChain debugSecurityFilterChain( HttpSecurity http ) {
//		http.requestMatcher(  )
//	}

/*
	@Bean
	DebugWebModuleSettings debugWebModuleSettings() {
		return new DebugWebModuleSettings();
	}

	@Bean
	DebugWebModuleSettings.SecuritySettings debugWebModuleSecuritySettings() {
		return new DebugWebModuleSettings.SecuritySettings();
	}

	@Bean
	DebugWebSecurityConfiguration debugWebSecurityConfiguration() {
		return new DebugWebSecurityConfiguration();
	}
*/
}
