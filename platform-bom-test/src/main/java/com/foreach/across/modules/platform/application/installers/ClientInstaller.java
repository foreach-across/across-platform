package com.foreach.across.modules.platform.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.services.RoleService;
import liquibase.exception.LiquibaseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author Marc Vanbrabant
 */
@Installer(
		description = "Installs a dummy client id.",
		version = 1,
		phase = InstallerPhase.AfterModuleBootstrap
)
public class ClientInstaller extends AcrossLiquibaseInstaller
{

	@Autowired
	private OAuth2Service oAuth2Service;
	@Autowired
	private RoleService roleService;

	@Override
	public void install() throws LiquibaseException {
		OAuth2Scope oAuth2Scope = new OAuth2Scope();
		oAuth2Scope.setName( "full" );
		oAuth2Service.saveScope( oAuth2Scope );

		OAuth2Client client = new OAuth2Client();
		client.setClientId( "client.com" );
		client.setClientSecret( "t3st" );
		client.setSecretRequired( true );
		HashSet<String> trustedGrantTypes = new HashSet<>();
		trustedGrantTypes.add( "client_credentials" );
		trustedGrantTypes.add( "password" );
		trustedGrantTypes.add( "refresh_token" );
		client.getAuthorizedGrantTypes().addAll( trustedGrantTypes );
		client.getResourceIds().addAll( Collections.singleton( "platform-test-app" ) );
		Role role = roleService.getRole( "ROLE_ADMIN" );
		client.getRoles().addAll( Collections.singleton( role ) );

		OAuth2ClientScope clientScope = new OAuth2ClientScope();
		clientScope.setAutoApprove( false );
		clientScope.setOAuth2Scope( oAuth2Scope );
		clientScope.setOAuth2Client( client );

		oAuth2Service.saveClient( client );
	}
}
