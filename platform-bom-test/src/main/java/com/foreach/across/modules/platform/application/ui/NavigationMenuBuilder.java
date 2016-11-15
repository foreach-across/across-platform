package com.foreach.across.modules.platform.application.ui;

import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.EventName;
import com.foreach.across.modules.platform.application.business.Category;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Arne Vandamme
 */
@Component
public class NavigationMenuBuilder
{
	private final MessageSource messageSource;

	@Autowired
	public NavigationMenuBuilder( MessageSource messageSource ) {
		this.messageSource = messageSource;
	}

	@Event
	public void buildMainMenu( @EventName("navigationMenu") BuildMenuEvent<Menu> menuEvent ) {
		menuEvent.builder()
		         .item( "/home", message( "nav.home" ), "/" ).order( 0 ).and()
		         .group( "/category", message( "nav.browse" ) ).order( 1 ).and()
		         .item( "/search", message( "nav.search" ), "http://www.google.com" ).order( 3 );

		registerCategories( menuEvent.builder() );
	}

	private void registerCategories( PathBasedMenuBuilder builder ) {
		Category.LIST.forEach( c -> builder.item( "/category/" + c.getPath(), c.getName() ) );
	}

	private String message( String messageCode ) {
		return messageSource.getMessage( messageCode, null, LocaleContextHolder.getLocale() );
	}
}