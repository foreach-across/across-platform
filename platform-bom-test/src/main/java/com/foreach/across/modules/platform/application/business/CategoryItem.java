package com.foreach.across.modules.platform.application.business;

import java.math.BigDecimal;

/**
 * @author Marc Vanbrabant
 */
public class CategoryItem
{
	private String name;
	private BigDecimal price;

	public CategoryItem() {
	}

	public CategoryItem( String name, BigDecimal price ) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice( BigDecimal price ) {
		this.price = price;
	}
}