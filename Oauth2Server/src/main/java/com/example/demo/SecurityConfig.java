package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(1)
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
	 protected void configure(HttpSecurity http) throws Exception {
		 
		 http.authorizeRequests().antMatchers("/auth/login**","/login", "/oauth/authorize").permitAll();
		 
		 
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
		 
        http.requestMatchers().antMatchers("/auth/login**","/login", "/oauth/authorize")
            .and()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin();//.loginPage("/login").loginProcessingUrl("/j_spring_security_check");
            
	 }
	 
	 
	 @Bean
	 @Override
	 public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	 }
	 
	
	 @Override
	 public void configure(AuthenticationManagerBuilder auth) throws Exception {
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
