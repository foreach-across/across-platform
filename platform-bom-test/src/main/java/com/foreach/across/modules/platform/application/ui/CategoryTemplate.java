package com.foreach.across.modules.platform.application.ui;

import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.template.LayoutTemplateProcessorAdapterBean;
import org.springframework.stereotype.Component;

/**
 * @author Marc Vanbrabant
 */
@Component
public class CategoryTemplate extends LayoutTemplateProcessorAdapterBean
{
	public static final String NAME = "CategoryTemplate";

	public CategoryTemplate() {
		super( NAME, "th/platformTest/layouts/category" );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry registry ) {
		registry.addPackage( CatalogWebResources.NAME );
	}

	@Override
	protected void buildMenus( MenuFactory menuFactory ) {
		menuFactory.buildMenu( "navigationMenu" );
	}
}