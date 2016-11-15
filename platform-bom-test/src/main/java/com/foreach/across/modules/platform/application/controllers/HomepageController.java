package com.foreach.across.modules.platform.application.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Marc Vanbrabant
 */
@Controller
public class HomepageController
{
	@RequestMapping("/")
	public String showHomepage() {
		return "th/platformTest/homepage";
	}
}
