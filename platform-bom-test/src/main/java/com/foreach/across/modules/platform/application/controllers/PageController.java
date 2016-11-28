package com.foreach.across.modules.platform.application.controllers;

import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.web.template.ClearTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Arne Vandamme
 */
@Controller
@RequestMapping("springMobile")
@ClearTemplate
public class PageController
{
	@Autowired
	private AclSecurityEntityService aclSecurityService;

	@RequestMapping({ "/thymeleaf" })
	public String thymleafPage( Device device, ModelMap model ) {
		String name = "normal";
		if ( device.isMobile() ) {
			name = "mobile";
		}
		else if ( device.isTablet() ) {
			name = "tablet";
		}
		model.addAttribute( "deviceTypeName", name );

		return "th/platformTest/page";
	}

	@RequestMapping("/dialect")
	public String deviceDialectTest( Device device, ModelMap model ) {
		String name = "normal";
		return "th/platformTest/test";
	}
}
