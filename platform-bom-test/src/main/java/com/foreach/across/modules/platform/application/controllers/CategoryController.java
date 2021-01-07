package com.foreach.across.modules.platform.application.controllers;

import com.foreach.across.modules.platform.application.business.Category;
import com.foreach.across.modules.platform.application.ui.CategoryTemplate;
import com.foreach.across.modules.platform.application.ui.ErrorTemplate;
import com.foreach.across.modules.web.template.Template;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Marc Vanbrabant
 */
@Controller
@Template(CategoryTemplate.NAME)
public class CategoryController
{
	@RequestMapping("/category/{categoryPath}")
	public String listCategoryItems( @PathVariable(value = "categoryPath", required = false) Category selectedCategory,
	                                 Model model ) {
		if ( selectedCategory == null ) {
			throw new IllegalArgumentException( "Illegal category requested." );
		}

		model.addAttribute( "selectedCategory", selectedCategory );

		return "th/platformTest/categoryItems";
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@Template(ErrorTemplate.NAME)
	public String categoryNotFound( Exception e, Model model ) {
		model.addAttribute( "feedback", e.getMessage() );
		return "th/platformTest/404";
	}
}
