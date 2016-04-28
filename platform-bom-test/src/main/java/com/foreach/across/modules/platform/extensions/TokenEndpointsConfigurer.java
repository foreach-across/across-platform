package com.foreach.across.modules.platform.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.oauth2.OAuth2Module;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

/**
 * @author Marc Vanbrabant
 */
@ModuleConfiguration(OAuth2Module.NAME)
public class TokenEndpointsConfigurer extends AuthorizationServerConfigurerAdapter {
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }
}
