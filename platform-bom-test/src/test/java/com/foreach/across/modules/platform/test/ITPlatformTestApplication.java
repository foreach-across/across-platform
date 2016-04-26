package com.foreach.across.modules.platform.test;

import com.foreach.across.modules.platform.PlatformTestApplication;
import com.foreach.across.modules.platform.extensions.DebugWebSecurityConfiguration;
import com.foreach.across.modules.spring.security.authority.NamedGrantedAuthority;
import com.foreach.across.test.support.config.MockAcrossServletContextInitializer;
import com.foreach.across.test.support.config.MockMvcConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marc Vanbrabant
 * @since 1.1.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(
        classes = {PlatformTestApplication.class, MockMvcConfiguration.class},
        initializers = MockAcrossServletContextInitializer.class
)
public class ITPlatformTestApplication {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testThatDebugModuleRedirectsToApplicationInfoModuleDashboard() throws Exception {
        mockMvc.perform(get("/debug").with(basicAuthenticationForDebugPage())).andExpect(header().string("Location", "/debug/applicationInfo"));
    }

    @Test
    public void testThatDebugModuleIsSecured() throws Exception {
        mockMvc.perform(get("/debug/applicationInfo")).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        mockMvc.perform(get("/debug/applicationInfo").with(basicAuthenticationForDebugPage())).andExpect(status().isOk());
    }

    @Test
    public void testThatAdminWebModuleIsSecured() throws Exception {
        mockMvc.perform(get("/admin/entities/user"))
                .andExpect(status().is(HttpStatus.FOUND.value())).andExpect(header().string("Location", "http://localhost/admin/login"));

        mockMvc.perform(formLogin("/admin/login").user("admin").password("admin"))
                .andExpect(status().is(HttpStatus.FOUND.value())).andExpect(header().string("Location", "/admin/")).andExpect(authenticated()).andReturn();

        mockMvc.perform(get("/admin/entities/user").with(user("admin").authorities(new NamedGrantedAuthority("access administration"), new NamedGrantedAuthority("manage users"))))
                .andExpect(status().isOk());

        mockMvc.perform(logout("/admin/logout"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(header().string("Location", "http://localhost/admin/login"));
    }


    private static RequestPostProcessor basicAuthenticationForDebugPage() {
        return httpBasic(DebugWebSecurityConfiguration.DEBUG_USERNAME, DebugWebSecurityConfiguration.DEBUG_PASSWORD);
    }
}
