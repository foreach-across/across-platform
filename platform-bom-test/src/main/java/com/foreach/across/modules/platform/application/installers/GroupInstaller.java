package com.foreach.across.modules.platform.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.spring.security.infrastructure.services.CloseableAuthentication;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.QGroup;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import liquibase.exception.LiquibaseException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Marc Vanbrabant
 */
@Installer(
		description = "Installs a dummy group.",
		version = 1,
		phase = InstallerPhase.AfterModuleBootstrap
)
public class GroupInstaller extends AcrossLiquibaseInstaller
{
	@Autowired
	private GroupService groupService;
	@Autowired
	private MachinePrincipalService machinePrincipalService;
	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Override
	public void install() throws LiquibaseException {
		MachinePrincipal system = machinePrincipalService.getMachinePrincipalByName( "system" );
		try (CloseableAuthentication ignored = securityPrincipalService.authenticate( system )) {
			Group group = groupService.findOne( QGroup.group.name.eq( "dummy group" ) );
			if ( group == null ) {
				group = new Group();
				group.setName( "dummy group" );
				groupService.save( group );
			}
		}
	}
}
