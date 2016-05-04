package com.foreach.across.modules.platform.application.config;

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
}
