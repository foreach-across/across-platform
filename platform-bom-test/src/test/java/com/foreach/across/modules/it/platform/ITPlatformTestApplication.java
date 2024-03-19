package com.foreach.across.modules.it.platform;

import com.foreach.across.modules.platform.PlatformTestApplication;
import com.foreach.across.modules.user.services.GroupService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marc Vanbrabant
 * @since 2.0.0
 */
@EnableWebSecurity
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PlatformTestApplication.class)
public class ITPlatformTestApplication
{
	@Value("${local.server.port}")
	private int port;

	@Autowired
	private GroupService groupService;

	@Test
	public void debugModuleRedirectsToApplicationInfoModuleDashboard() throws Exception {
		RestTemplate restTemplate = restTemplate();
		ResponseEntity<String> response = restTemplate.exchange( url( "/debug" ), HttpMethod.GET,
		                                                         defaultDebugAuthentication(),
		                                                         String.class );
		assertNotNull( response );
		assertEquals( HttpStatus.FOUND, response.getStatusCode() );
		assertEquals( url( "/debug/applicationInfo" ), response.getHeaders().get( "Location" ).get( 0 ) );
	}

	@Test
	public void debugModuleIsSecuredForUnknownUser() throws Exception {
		RestTemplate restTemplate = restTemplate( true );
		ResponseEntity<String> response = restTemplate.getForEntity( url( "/debug/applicationInfo" ), String.class );
		assertEquals( HttpStatus.UNAUTHORIZED, response.getStatusCode() );
	}

	@Test
	public void debugModuleIsSecuredForAuthenticatedUser() throws Exception {
		RestTemplate restTemplate = restTemplate();
		ResponseEntity<String> response = restTemplate.exchange( url( "/debug/applicationInfo" ), HttpMethod.GET,
		                                                         defaultDebugAuthentication(),
		                                                         String.class );
		assertEquals( HttpStatus.OK, response.getStatusCode() );
	}

	@Test
	public void adminWebModuleRedirectsToLoginPage() throws Exception {
		RestTemplate restTemplate = restTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity( url( "/admin/entities/user" ), String.class );
		assertEquals( HttpStatus.FOUND, response.getStatusCode() );
		assertEquals( url( "/admin/login" ), response.getHeaders().get( "Location" ).get( 0 ) );
	}

	@Test
	public void adminWebModuleListsUserOverviewForAuthenticatedUser() {
		RestTemplate restTemplate = restTemplate( true );

		ResponseEntity<String> loginPage = restTemplate.getForEntity( url( "/admin/login" ), String.class );
		Document doc = Jsoup.parse( loginPage.getBody() );
		String csrf = doc.select( "input[name=_csrf]" ).val();
		List<String> setCookieHeader = loginPage.getHeaders().get( "Set-Cookie" );
		assertNotNull( setCookieHeader, () -> "Set-Cookie header missing in: " + loginPage.getHeaders() );
		String cookie = setCookieHeader.get( 0 );
		if ( csrf != null ) {
			ResponseEntity<String> response = restTemplate.exchange( url( "/admin/login" ), HttpMethod.POST,
			                                                         new HttpEntity<Object>(
					                                                         new LinkedMultiValueMap<String, String>()
					                                                         {{
						                                                         set( "username", "admin" );
						                                                         set( "password", "admin" );
					                                                         }}, new HttpHeaders()
			                                                         {{
				                                                         set( "X-XSRF-TOKEN", csrf );
				                                                         set( "Cookie", cookie );
			                                                         }} ), String.class );
			assertEquals( HttpStatus.FOUND, response.getStatusCode() );
			assertEquals( url( "/admin/" ), response.getHeaders().get( "Location" ).get( 0 ) );

			String sessionId = response.getHeaders().get( "Set-Cookie" ).stream()
			                           .filter( c -> c.contains( "JSESSIONID" ) )
			                           .findFirst()
			                           .orElseThrow( () -> new IllegalStateException( "Expected JSESSIONID cookie" ) );

			{
				ResponseEntity<String> entityResponse
						= restTemplate.exchange( url( "/admin/entities/user" ),
						                         HttpMethod.GET, new HttpEntity<>( null,
						                                                           new HttpHeaders()
						                                                           {{
							                                                           set( "Cookie", sessionId );
						                                                           }} ),
						                         String.class );
				assertEquals( HttpStatus.OK, entityResponse.getStatusCode() );
			}

			{
				ResponseEntity<String> entityResponse = restTemplate.exchange(
						url( "/admin/entities/ldapConnector/create" ),
						HttpMethod.GET, new HttpEntity<>( null,
						                                  new HttpHeaders()
						                                  {{
							                                  set( "Cookie", sessionId );
						                                  }} ),
						String.class );
				assertEquals( HttpStatus.OK, entityResponse.getStatusCode() );
				Document jsoup = Jsoup.parse( entityResponse.getBody() );
				assertEquals( 1, jsoup.select( "div.panel-default.hidden" ).size() );
				assertEquals( "Create a new ldap connector", jsoup.select( "header > h3" ).first().text() );
			}
		}
	}

	@Test
	public void fileManagerCreatesFile() {
		RestTemplate restTemplate = restTemplate( true );

		ResponseEntity<CustomFileDescriptor> response = restTemplate.exchange( url( "/fileManager/create" ),
		                                                                       HttpMethod.GET,
		                                                                       null,
		                                                                       CustomFileDescriptor.class );
		assertNotNull( response );
		assertEquals( HttpStatus.OK, response.getStatusCode() );

		CustomFileDescriptor fd = response.getBody();
		assertEquals( "default", fd.getRepositoryId() );
		assertEquals( FastDateFormat.getInstance( "yyyy/MM/dd" ).format( System.currentTimeMillis() ),
		              fd.getFolderId() );

	}

	@Test
	public void ehCacheModuleLoadsAndCachesSystemUser() throws Exception {
		RestTemplate restTemplate = restTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				url( "/debug/ehcache/view?managerName=__DEFAULT__&cache=securityPrincipalCache" ), HttpMethod.GET,
				defaultDebugAuthentication(),
				String.class );
		assertNotNull( response );
		assertEquals( HttpStatus.OK, response.getStatusCode() );
		Document jsoup = Jsoup.parse( response.getBody() );
		assertEquals( "securityPrincipalCache", jsoup.select( "h3" ).first().text() );

		jsoup.select( "tr > td:first-child" ).stream().filter( e -> e.text().equals( "system" ) ).findFirst()
		     .orElseThrow( AssertionError::new );
		jsoup.select( "tr > td:first-child" ).stream().filter( e -> e.text().equals( "1" ) ).findFirst().orElseThrow(
				AssertionError::new );
	}

/*
	@Test
	public void preAuthorizedControllerIsOnlyAccessibleWhenAuthenticated() throws Exception {
		RestTemplate restTemplate = restTemplate( true );
		ResponseEntity<String> response = restTemplate.getForEntity(
				url( "/api/testshouldbeauthenticated?access_token=" + UUID.randomUUID() ), String.class );
		assertNotNull( response );
		assertEquals( HttpStatus.UNAUTHORIZED, response.getStatusCode() );
	}
*/

/*
	@Test
	public void oauthClientTokenFlowWorksForAdmin() {
		RestTemplate restTemplate = restTemplate( true );
		ResponseEntity<LinkedHashMap> clientTokenResponse = restTemplate.getForEntity(
				url( "/oauth/token?client_id=client.com&client_secret=t3st&response_type=token&scope=full&grant_type=client_credentials" ),
				LinkedHashMap.class );
		assertNotNull( clientTokenResponse );
		assertEquals( HttpStatus.OK, clientTokenResponse.getStatusCode() );
		String client_access_token = (String) clientTokenResponse.getBody().get( "access_token" );
		assertNotNull( client_access_token );

		// Test our custom user token endpoint
		ResponseEntity<LinkedHashMap> userTokenResponse = restTemplate.getForEntity(
				url( "/oauth/user_token?access_token=" + client_access_token + "&username=admin&scope=full" ),
				LinkedHashMap.class );
		assertNotNull( userTokenResponse );
		assertEquals( HttpStatus.OK, userTokenResponse.getStatusCode() );
		String user_access_token = (String) userTokenResponse.getBody().get( "access_token" );
		String user_refresh_token = (String) userTokenResponse.getBody().get( "refresh_token" );
		assertNotNull( user_access_token );
		assertNotNull( user_refresh_token );

		// Test our protected controller
		ResponseEntity<LinkedHashMap> apiRestResponse = restTemplate.getForEntity(
				url( "/api/testshouldbeauthenticated?access_token=" + user_access_token ), LinkedHashMap.class );
		assertNotNull( apiRestResponse );
		assertEquals( HttpStatus.OK, apiRestResponse.getStatusCode() );
		assertEquals( "admin", apiRestResponse.getBody().get( "principalName" ) );
		assertEquals( "admin@localhost", apiRestResponse.getBody().get( "email" ) );
	}
*/

	@Test
	public void springSecurityDialectLoads() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> loginPage = restTemplate.getForEntity( url( "/admin/login" ), String.class );
		Document doc = Jsoup.parse( loginPage.getBody() );
		assertEquals( 1, doc.select( "input[name=username]" ).size() );
		assertEquals( 1, doc.select( "input[name=password]" ).size() );
		assertTrue( !loginPage.getBody().contains( "sec:authorize" ) );
		assertTrue( !loginPage.getBody().contains( "isAuthenticated()" ) );
	}

	@Test
	public void acrossContextBrowserInfoPageWorks() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/debug/across/browser/info/-1" ), HttpMethod.GET,
		                                                         defaultDebugAuthentication(),
		                                                         String.class );
		assertNotNull( response );
	}

	@Test
	public void acrossContextBrowserBeansPageWorks() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/debug/across/browser/beans/1" ), HttpMethod.GET,
		                                                         defaultDebugAuthentication(),
		                                                         String.class );
		assertNotNull( response );
	}

	@Test
	public void acrossContextBrowserPropertiesPageWorks() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/debug/across/browser/properties/1" ),
		                                                         HttpMethod.GET,
		                                                         defaultDebugAuthentication(),
		                                                         String.class );
		assertNotNull( response );
	}

	@Test
	public void acrossContextBrowserEventHandlersPageWorks() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/debug/across/browser/handlers/1" ),
		                                                         HttpMethod.GET,
		                                                         defaultDebugAuthentication(),
		                                                         String.class );
		assertNotNull( response );
	}

	@Test
	public void mainTemplateIsAppliedByDefault() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/" ),
		                                                         HttpMethod.GET,
		                                                         null,
		                                                         String.class );
		assertNotNull( response );
		assertFalse( StringUtils.contains( response.getBody(), "Custom category content" ) );
		Document doc = Jsoup.parse( response.getBody() );
		assertEquals( 1, doc.select( "h1" ).size() );
		assertEquals( 1, doc.select( "h2" ).size() );

		assertEquals( "Across bids you a warm welcome!", doc.select( "h1" ).get( 0 ).text() );
		assertEquals( "Hello world", doc.select( "h2" ).get( 0 ).text() );
	}

	@Test
	public void categoryTemplateIsAppliedToCategoriesMapping() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/category/tv" ),
		                                                         HttpMethod.GET,
		                                                         null,
		                                                         String.class );
		assertNotNull( response );
		assertTrue( StringUtils.contains( response.getBody(), "Custom category content" ) );
		Document doc = Jsoup.parse( response.getBody() );
		assertEquals( 1, doc.select( "h1" ).size() );
		assertEquals( 0, doc.select( "h2" ).size() );

		assertEquals( "Products for category: TV", doc.select( "h1" ).get( 0 ).text() );
	}

	@Test
	public void categoryTemplateIsAppliedToCategoriesMappingInDutch() {
		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> response = restTemplate.exchange( url( "/category/tv?language=nl" ),
		                                                         HttpMethod.GET,
		                                                         null,
		                                                         String.class );
		assertNotNull( response );
		assertTrue( StringUtils.contains( response.getBody(), "Custom category content" ) );
		Document doc = Jsoup.parse( response.getBody() );
		assertEquals( 1, doc.select( "h1" ).size() );
		assertEquals( 0, doc.select( "h2" ).size() );

		assertEquals( "Producten voor categorie: TV", doc.select( "h1" ).get( 0 ).text() );
	}

	@Test
	public void unknownCategoryUsesExceptionHandlerAndCustomTemplate() {
		RestTemplate restTemplate = restTemplate( true );

		ResponseEntity<String> response = restTemplate.exchange( url( "/category/foobar" ),
		                                                         HttpMethod.GET,
		                                                         null,
		                                                         String.class );
		assertNotNull( response );
		assertFalse( StringUtils.contains( response.getBody(), "Custom category content" ) );
		Document doc = Jsoup.parse( response.getBody() );
		assertEquals( 0, doc.select( "h1" ).size() );
		assertEquals( 1, doc.select( "h2" ).size() );

		assertEquals( "Oops, we could not find what you're looking for.", doc.select( "h2" ).get( 0 ).text() );
	}

	@Test
	@Disabled("No longer the case as of Spring Boot 2.0 - test probably irrelevant and Actuator security will be covered elsewhere")
	public void securityIsAppliedToActuatorEndpoints() {
		RestTemplate restTemplate = restTemplate( true );
		ResponseEntity<String> response = restTemplate.getForEntity( url( "/health" ), String.class );
		assertEquals( HttpStatus.OK, response.getStatusCode() );
		assertFalse( StringUtils.contains( response.getBody(), "diskSpace" ) );

		response = restTemplate.exchange(
				url( "/health" ),
				HttpMethod.GET,
				createHeaders( "admin", "admin" ),
				String.class
		);
		assertEquals( HttpStatus.OK, response.getStatusCode() );
		assertTrue( StringUtils.contains( response.getBody(), "diskSpace" ) );
	}

	private HttpEntity defaultDebugAuthentication() {
		return createHeaders( "debug", "test" );
	}

	private String url( String relativePath ) {
		return "http://localhost:" + port + relativePath;
	}

	private RestTemplate restTemplate() {
		return restTemplate( false );
	}

	private RestTemplate restTemplate( boolean ignoreErrorHandler ) {
		RestTemplate restTemplate = new RestTemplate();
		if ( ignoreErrorHandler ) {
			restTemplate.setErrorHandler( new NoResponseErrorHandler() );
		}
		restTemplate.setRequestFactory( new SimpleClientHttpRequestFactory()
		{
			@Override
			protected void prepareConnection( HttpURLConnection connection, String httpMethod ) throws IOException {
				super.prepareConnection( connection, httpMethod );
				connection.setInstanceFollowRedirects( false );
			}
		} );
		return restTemplate;
	}

	private HttpEntity createHeaders( String username, String password ) {
		HttpHeaders headers = new HttpHeaders()
		{
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64( auth.getBytes() );
				String authHeader = "Basic " + new String( encodedAuth );
				set( "Authorization", authHeader );
			}
		};
		return new HttpEntity( headers );
	}

	private static class NoResponseErrorHandler implements ResponseErrorHandler
	{

		@Override
		public boolean hasError( ClientHttpResponse response ) throws IOException {
			return false;
		}

		@Override
		public void handleError( ClientHttpResponse response ) throws IOException {

		}
	}

	public static class CustomFileDescriptor
	{
		private String repositoryId;
		private String fileId;
		private String folderId;
		private String uri;

		public String getRepositoryId() {
			return repositoryId;
		}

		public void setRepositoryId( String repositoryId ) {
			this.repositoryId = repositoryId;
		}

		public String getFileId() {
			return fileId;
		}

		public void setFileId( String fileId ) {
			this.fileId = fileId;
		}

		public String getFolderId() {
			return folderId;
		}

		public void setFolderId( String folderId ) {
			this.folderId = folderId;
		}

		public String getUri() {
			return uri;
		}

		public void setUri( String uri ) {
			this.uri = uri;
		}

	}

}
