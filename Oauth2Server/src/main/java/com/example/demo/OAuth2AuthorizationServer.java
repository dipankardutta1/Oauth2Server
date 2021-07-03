package com.example.demo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.DefaultSecurityContextAccessor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.SecurityContextAccessor;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import com.example.demo.entity.User;



@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
	
	@Autowired
    private AuthClientDetailsService authClientDetailsService;
	
	/*
	
    @Value("${user.oauth.clientId}")
    private String ClientID;
    @Value("${user.oauth.clientSecret}")
    private String ClientSecret;
    @Value("${user.oauth.redirectUris}")
    private String RedirectURLs;
    */
    
    
    @Value("${check-user-scopes}")
	private Boolean checkUserScopes;
    
    
    
    public OAuth2AuthorizationServer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()")
			.allowFormAuthenticationForClients();
	}
	
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		//clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
		
		 clients.withClientDetails(authClientDetailsService);
	}
	
	
	// for grant_type=password
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		
		//endpoints.authenticationManager(authenticationManager);
		
		
		endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtAccessTokenConverter())
		.authenticationManager(authenticationManager).userDetailsService(userDetailsService);
		if (checkUserScopes) {
			endpoints.requestFactory(requestFactory());
		}
		
	}
	
	
	
	// problem section
	@Bean
	public OAuth2RequestFactory requestFactory() {
		CustomOauth2RequestFactory requestFactory = new CustomOauth2RequestFactory(authClientDetailsService);
		requestFactory.setCheckUserScopes(true);
		return requestFactory;
	}
	
	
	
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}
	
	
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new CustomTokenEnhancer();
		converter.setKeyPair(
				new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "password".toCharArray()).getKeyPair("jwt"));
		return converter;
	}

	
	class CustomTokenEnhancer extends JwtAccessTokenConverter {
		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
			User user = (User) authentication.getPrincipal();

			Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());

			info.put("email", user.getEmail());

			DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
			customAccessToken.setAdditionalInformation(info);

			return super.enhance(customAccessToken, authentication);
		}
	}

	class CustomOauth2RequestFactory extends DefaultOAuth2RequestFactory {
		@Autowired
		private TokenStore tokenStore;
		
		private SecurityContextAccessor securityContextAccessor = new DefaultSecurityContextAccessor();

		public CustomOauth2RequestFactory(ClientDetailsService clientDetailsService) {
			super(clientDetailsService);
		}

		@Override
		public TokenRequest createTokenRequest(Map<String, String> requestParameters,ClientDetails authenticatedClient) {
			
			
			
			
			if (requestParameters.get("grant_type").equals("refresh_token")) {
				OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(tokenStore.readRefreshToken(requestParameters.get("refresh_token")));
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getName(), null,
								userDetailsService.loadUserByUsername(authentication.getName()).getAuthorities()));
				
			}else if(requestParameters.get("grant_type").equals("password")){
				SecurityContextHolder.getContext() .setAuthentication(new UsernamePasswordAuthenticationToken(requestParameters.get("username"), null, userDetailsService.loadUserByUsername(requestParameters.get("username")).getAuthorities())); 
				
			}
			
			
			
			
			return super.createTokenRequest(requestParameters, authenticatedClient);
			
		}
		
		
		@Override
	    public AuthorizationRequest createAuthorizationRequest(Map<String, String> authorizationParameters) {
	        AuthorizationRequest request = super.createAuthorizationRequest(authorizationParameters);

	        if (securityContextAccessor.isUser()) {
	            request.setAuthorities(securityContextAccessor.getAuthorities());
	        }

	        return request;
	    }
		
		
		
		
		
	}
	
	
	
	
	@Bean
	public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter() {
		return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory());
	}

	
	/*
	@Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient(ClientID)
            .secret(passwordEncoder.encode(ClientSecret))
            .authorizedGrantTypes("password", "authorization_code", "refresh_token")
            .scopes("user_info")
            .autoApprove(true)
            .redirectUris(RedirectURLs);
    }
	
	*/
	
	/*
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
			.inMemory()
			.withClient("clientapp")
			.secret(passwordEncoder.encode("123456"))
			.authorizedGrantTypes("password", "authorization_code", "refresh_token")
			.authorities("READ_ONLY_CLIENT")
			.scopes("read_profile_info")
			.resourceIds("oauth2-resource")
			.redirectUris("http://127.0.0.1:8000/foos")
			.accessTokenValiditySeconds(5000)
			.refreshTokenValiditySeconds(50000);
	}
	
	*/
}
