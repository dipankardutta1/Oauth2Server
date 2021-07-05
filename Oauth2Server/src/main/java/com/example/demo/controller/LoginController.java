package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	
	 @Autowired
	 private TokenStore tokenStore;
	
	@GetMapping("login")
	public String goToLoginPage() {
		return "customLogin";
	}
	
	@RequestMapping(value = "revoke-token", method = RequestMethod.GET)
    public void logout(HttpServletRequest request,HttpServletResponse response){
		/*
		 * String authHeader = request.getHeader("Authorization"); if (authHeader !=
		 * null) { String tokenValue = authHeader.replace("Bearer", "").trim();
		 * OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
		 * tokenStore.removeAccessToken(accessToken); }
		 * 
		 * return "redirect:http://localhost:8000";
		 */
		
		
		 // token can be revoked here if needed
        new SecurityContextLogoutHandler().logout(request, null, null);
        try {
            //sending back to client app
            response.sendRedirect(request.getHeader("referer"));
        } catch (IOException e) {
            e.printStackTrace();
        }
       
		
		
		
		
		
    }
	
	/*
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	  public ModelAndView logoutPage (HttpServletRequest request, HttpServletResponse response) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){
	      new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	    return new ModelAndView ("redirect:/login?logout");
	  }
	  
	  */

}
