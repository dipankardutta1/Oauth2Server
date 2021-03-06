package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter 
{
	
	@Autowired
	private TokenStore tokenStore;
	
	private static final String RESOURCE_ID = "mw/adminapp";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false).tokenStore(tokenStore);
    }
	
    
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		 http .authorizeRequests() .antMatchers("/auth/**").permitAll().anyRequest().authenticated();
		
		/*
		   http.csrf().disable().formLogin().loginPage("/login").permitAll().and().requestMatchers()
	        .antMatchers("/auth/login**","/login", "/oauth/authorize","/auth/oauth/authorize").and().authorizeRequests()
	        .anyRequest().authenticated()
		  .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		  
		  */
		/*
		 http.
	        anonymous().disable()
	        .requestMatchers().antMatchers("/**")
	        .and().authorizeRequests()
	        .antMatchers("/**").access("hasRole('USER')")
	        .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	        
	        */
	}


}
