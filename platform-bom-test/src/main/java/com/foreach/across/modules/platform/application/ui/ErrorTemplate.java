package com.foreach.across.modules.platform.application.ui;

import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.template.LayoutTemplateProcessorAdapterBean;
import org.springframework.stereotype.Component;

/**
 * @author Marc Vanbrabant
 */
@Component
public class ErrorTemplate extends LayoutTemplateProcessorAdapterBean
{
	public static final String NAME = "ErrorTemplate";

	public ErrorTemplate() {
		super( NAME, "th/platformTest/layouts/error" );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry registry ) {
		registry.addPackage( CatalogWebResources.NAME );
	}
}
