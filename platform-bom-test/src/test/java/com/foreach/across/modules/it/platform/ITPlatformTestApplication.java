package com.foreach.across.modules.it.platform;

import com.foreach.across.modules.platform.PlatformTestApplication;
import com.foreach.across.modules.platform.extensions.DebugWebSecurityConfiguration;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Marc Vanbrabant
 * @since 1.1.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = PlatformTestApplication.class)
public class ITPlatformTestApplication {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void testThatDebugModuleRedirectsToApplicationInfoModuleDashboard() throws Exception {
        RestTemplate restTemplate = restTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url("/debug"), HttpMethod.GET, createHeaders(DebugWebSecurityConfiguration.DEBUG_USERNAME, DebugWebSecurityConfiguration.DEBUG_PASSWORD), String.class);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        assertEquals(url("/debug/applicationInfo"), response.getHeaders().get("Location").get(0));
    }

    @Test
    public void testThatDebugModuleIsSecuredForUnknownUser() throws Exception {
        RestTemplate restTemplate = restTemplate(true);
        ResponseEntity<String> response = restTemplate.getForEntity(url("/debug/applicationInfo"), String.class);
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testThatDebugModuleIsSecuredForAuthenticatedUser() throws Exception {
        RestTemplate restTemplate = restTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url("/debug/applicationInfo"), HttpMethod.GET, createHeaders(DebugWebSecurityConfiguration.DEBUG_USERNAME, DebugWebSecurityConfiguration.DEBUG_PASSWORD), String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testThatAdminWebModuleRedirectsToLoginPage() throws Exception {
        RestTemplate restTemplate = restTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url("/admin/entities/user"), String.class);
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        assertEquals(url("/admin/login"), response.getHeaders().get("Location").get(0));
    }

    @Test
    public void testThatAdminWebModuleListsUserOverviewForAuthenticatedUser() {
        RestTemplate restTemplate = restTemplate(true);

        ResponseEntity<String> loginPage = restTemplate.getForEntity(url("/admin/login"), String.class);
        Document doc = Jsoup.parse(loginPage.getBody());
        String csrf = doc.select("input[name=_csrf]").val();
        String cookie = loginPage.getHeaders().get("Set-Cookie").get(0);
        if (csrf != null) {
            ResponseEntity<String> response = restTemplate.exchange(url("/admin/login"), HttpMethod.POST, new HttpEntity<Object>(new LinkedMultiValueMap<String, String>() {{
                set("username", "admin");
                set("password", "admin");
            }}, new HttpHeaders() {{
                set("X-CSRF-Token", csrf);
                set("Cookie", cookie);
            }}), String.class);
            assertEquals(response.getStatusCode(), HttpStatus.FOUND);
            assertEquals(url("/admin/"), response.getHeaders().get("Location").get(0));

            ResponseEntity<String> responseForUserList = restTemplate.exchange(url("/admin/entities/user"), HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders() {{
                set("Cookie", response.getHeaders().get("Set-Cookie").get(0));
            }}), String.class);
            assertEquals(responseForUserList.getStatusCode(), HttpStatus.OK);
        }
    }

    @Test
    public void testThatPreAuthorizedControllerIsOnlyAccessibleWhenAuthenticated() throws Exception {
        RestTemplate restTemplate = restTemplate(true);
        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/testshouldbeauthenticated?access_token=" + UUID.randomUUID()), String.class);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testThatOauthClientTokenFlowWorksForAdmin() {
        RestTemplate restTemplate = restTemplate(true);
        ResponseEntity<LinkedHashMap> clientTokenResponse = restTemplate.getForEntity(url("/oauth/token?client_id=client.com&client_secret=t3st&response_type=token&scope=full&grant_type=client_credentials"), LinkedHashMap.class);
        assertNotNull(clientTokenResponse);
        assertEquals(clientTokenResponse.getStatusCode(), HttpStatus.OK);
        String client_access_token = (String) clientTokenResponse.getBody().get("access_token");
        assertNotNull(client_access_token);

        // Test our custom user token endpoint
        ResponseEntity<LinkedHashMap> userTokenResponse = restTemplate.getForEntity(url("/oauth/user_token?access_token=" + client_access_token + "&username=admin&scope=full"), LinkedHashMap.class);
        assertNotNull(userTokenResponse);
        assertEquals(userTokenResponse.getStatusCode(), HttpStatus.OK);
        String user_access_token = (String) userTokenResponse.getBody().get("access_token");
        String user_refresh_token = (String) userTokenResponse.getBody().get("refresh_token");
        assertNotNull(user_access_token);
        assertNotNull(user_refresh_token);

        // Test our protected controller
        ResponseEntity<LinkedHashMap> apiRestResponse = restTemplate.getForEntity(url("/api/testshouldbeauthenticated?access_token=" + user_access_token), LinkedHashMap.class);
        assertNotNull(apiRestResponse);
        assertEquals(apiRestResponse.getStatusCode(), HttpStatus.OK);
        assertEquals("admin", apiRestResponse.getBody().get("principalName"));
        assertEquals("admin@localhost", apiRestResponse.getBody().get("email"));
    }

    private String url(String relativePath) {
        return "http://localhost:" + port + relativePath;
    }

    private RestTemplate restTemplate() {
        return restTemplate(false);
    }

    private RestTemplate restTemplate(boolean ignoreErrorHandler) {
        RestTemplate restTemplate = new RestTemplate();
        if (ignoreErrorHandler) {
            restTemplate.setErrorHandler(new NoResponseErrorHandler());
        }
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(false);
            }
        });
        return restTemplate;
    }

    private HttpEntity createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders() {
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
        return new HttpEntity(headers);
    }

    private static class NoResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {

        }
    }
}
