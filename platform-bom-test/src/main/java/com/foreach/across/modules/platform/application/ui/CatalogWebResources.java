package com.foreach.across.modules.platform.application.ui;

import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.foreach.across.modules.web.resource.WebResource.*;

/**
 * @author Arne Vandamme
 */
@Component
public class CatalogWebResources extends SimpleWebResourcePackage
{
	public static final String NAME = "CatalogWebResources";

	@Autowired
	public CatalogWebResources( AcrossDevelopmentMode developmentMode ) {
		String minified = developmentMode.isActive() ? "" : ".min";

		List<WebResource> webResources = new ArrayList<>();
		webResources.add( new WebResource(
				CSS,
				"bootstrap-css",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap" + minified + ".css",
				EXTERNAL
		) );
		webResources.add( new WebResource(
				JAVASCRIPT_PAGE_END,
				"jquery",
				"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery" + minified + ".js",
				EXTERNAL
		) );
		webResources.add( new WebResource(
				JAVASCRIPT_PAGE_END,
				"boostrap-js",
				"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap" + minified + ".js",
				EXTERNAL
		) );

		setWebResources( webResources );
	}

	@Autowired
	public void registerInPackageManager( @Qualifier("webResourcePackageManager") WebResourcePackageManager packageManager ) {
		packageManager.register( NAME, this );
	}
}
