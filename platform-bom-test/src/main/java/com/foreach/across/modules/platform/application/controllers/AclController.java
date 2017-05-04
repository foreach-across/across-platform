package com.foreach.across.modules.platform.application.controllers;

import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.web.template.ClearTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Marc Vanbrabant
 */
@RestController
@ClearTemplate
public class AclController
{
	@Autowired
	private AclSecurityService aclSecurityService;
	@Autowired
	private GroupService groupService;

	@RequestMapping("/acl/{groupName}")
	public ObjectIdentity viewAcl( @PathVariable("groupName") String groupName ) {
		Group group = groupService.getGroupByName( groupName );
		if ( group == null ) {
			throw new RuntimeException( "Group not found" );
		}

		return aclSecurityService.getAcl( group ).getObjectIdentity();
	}
}
