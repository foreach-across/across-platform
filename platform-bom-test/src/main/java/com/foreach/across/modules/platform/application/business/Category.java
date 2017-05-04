package com.foreach.across.modules.platform.application.business;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Marc Vanbrabant
 */
public class Category
{
	public static final List<Category> LIST = Collections.unmodifiableList(
			Arrays.asList(
					new Category( "tv", "TV",
					              Arrays.asList(
							              new CategoryItem( "Samsung", new BigDecimal( "750" ) ),
							              new CategoryItem( "Panasonic", new BigDecimal( "800.99" ) )
					              )
					),
					new Category( "radio", "Radio",
					              Collections.singletonList( new CategoryItem( "Onkyo", new BigDecimal( "999.99" ) ) )
					)
			)
	);

	private String path, name;
	private List<CategoryItem> items;

	public Category() {
	}

	public Category( String path, String name, List<CategoryItem> items ) {
		this.path = path;
		this.name = name;
		this.items = items;
	}

	public String getPath() {
		return path;
	}

	public void setPath( String path ) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public List<CategoryItem> getItems() {
		return items;
	}

	public void setItems( List<CategoryItem> items ) {
		this.items = items;
	}

	public static Category from( String path ) {
		for ( Category c : LIST ) {
			if ( StringUtils.equals( path, c.getPath() ) ) {
				return c;
			}
		}
		return null;
	}
}