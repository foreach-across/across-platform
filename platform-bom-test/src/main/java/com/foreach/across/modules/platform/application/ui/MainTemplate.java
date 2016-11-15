package com.foreach.across.modules.platform.application.ui;

import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.template.LayoutTemplateProcessorAdapterBean;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Marc Vanbrabant
 */
@Component
public class MainTemplate extends LayoutTemplateProcessorAdapterBean
{
	public static final String NAME = "MainTemplate";

	public MainTemplate() {
		super( NAME, "th/platformTest/layouts/main" );
	}

	@Autowired
	public void registerAsDefaultTemplate( WebTemplateRegistry webTemplateRegistry ) {
		webTemplateRegistry.setDefaultTemplateName( NAME );
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