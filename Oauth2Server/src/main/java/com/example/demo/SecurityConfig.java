package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@EnableWebSecurity
@Configuration
@Order(-20)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	/*
	 @Value("${user.oauth.user.username}")
	 private String username;
	 @Value("${user.oauth.user.password}")
	 private String password;
	 */
	 @Autowired
	 private UserDetailsService userDetailsService;
	 
	 
	 @Override
	  public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/css/**", "/images/**", "/js/**");
	  }
 
	 
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
		 
		// http.authorizeRequests().antMatchers("/auth/login**","/login", "/oauth/authorize").permitAll();
		 
		 
		 //http.authorizeRequests().antMatchers("/userInfo").access("hasRole('" + AppRole.ROLE_USER + "')");
		 
	        // For ADMIN only.
	     //   http.authorizeRequests().antMatchers("/admin").access("hasRole('" + AppRole.ROLE_ADMIN + "')");
	 
	        // When the user has logged in as XX.
	        // But access a page that requires role YY,
	        // AccessDeniedException will be thrown.
	       // http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
	 
	        // Form Login config
	       // http.authorizeRequests().anyRequest().authenticated()
	       // .and().formLogin();//
	                // Submit URL of login page.
            //.loginProcessingUrl("/login") // Submit URL
            //.loginPage("/login")//
            //.defaultSuccessUrl("/userInfo")//
            //.failureUrl("/login?error=true")//
            //.usernameParameter("username")//
            //.passwordParameter("password");
	 
	        // Logout Config
	       // http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
		 /*
        http.requestMatchers().antMatchers("/auth/login**","/login", "/oauth/authorize")
            .and()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin();
          */
		 
		 
		 http.csrf().disable().formLogin().loginPage("/login").permitAll().and().requestMatchers()
	        .antMatchers("/auth/login**","/login", "/oauth/authorize","/auth/oauth/authorize","/revoke-token").and().authorizeRequests()
	        .anyRequest().authenticated()
		  .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		  //.and()
		  //.logout()
	       // .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout");
	       
	 }
	 
	 
	 @Bean
	 @Override
	 public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	 }
	 
	
	 @Override
	 public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//auth.parentAuthenticationManager(authenticationManagerBean()).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		 auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	 }
	 
 /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	 auth.inMemoryAuthentication()
         .withUser(username)
         .password(passwordEncoder().encode(password))
         .roles("USER");
    }
    
    */
     
    @Bean
	public PasswordEncoder passwordEncoder() { // changed
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
