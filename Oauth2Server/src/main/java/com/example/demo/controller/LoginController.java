package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.OtpService;
import com.example.demo.ResetPasswordDto;
import com.example.demo.UserService;

@Controller
public class LoginController {
	
	 @Autowired
	 private TokenStore tokenStore;
	 
	 @Autowired
	 private UserService userService;
	 @Autowired
	 private OtpService otpService;
	
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
	
	
	
	
	
	@GetMapping("validateEmail")
	public String validateEmail() {
		return "validateEmail";
	}
	
	
	@RequestMapping(value = "validateEmail", method = RequestMethod.POST)
    public String validateEmail(Model model,@RequestParam("username") String username){
		
		String email = userService.sendEmail(username);
       
		if(email != null) {
			
			ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
			resetPasswordDto.setUsername(email);
			
			model.addAttribute("resetPasswordDto", resetPasswordDto);
			
			return "resetPassword";
		}else {
			return "redirect:validateEmail?error";
		}
		
		
		
		
		
		
    }
	
	
	
	@PostMapping("/resetPassword")
    public String resetPassword(Model model,@Valid ResetPasswordDto resetPasswordDto,BindingResult errors){
		
		
		
		
		
		//model.addAttribute("username", resetPasswordDto.getUsername());
		
		
		if (errors.hasErrors()) {
			
			String error = "";
			
			for (Object object : errors.getAllErrors()) {
			    if(object instanceof FieldError) {
			        FieldError fieldError = (FieldError) object;
			        error =  fieldError.getField() + " "+ fieldError.getDefaultMessage();
			       
			    }

			   /* if(object instanceof ObjectError) {
			        ObjectError objectError = (ObjectError) object;
			        error = error + objectError.getCode()+" ";
			        
			    }*/
			}
			
			
			model.addAttribute("msg",error);
			return "resetPassword";
		  }
		
		
		try {
			Integer otpGiven = Integer.valueOf(resetPasswordDto.getOtp());
			
			
			
			

			if(otpGiven >= 0){
				Integer serverOtp = otpService.getOtp(resetPasswordDto.getUsername());

				if(serverOtp > 0){
					if(otpGiven.equals(serverOtp)){
						
						
						
						if(resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirm())) {
							userService.updatePassword(resetPasswordDto.getPassword(),resetPasswordDto.getUsername());
							otpService.clearOTP(resetPasswordDto.getUsername());
							return "pwdChngdSuccess";
						}else {
							model.addAttribute("msg","Passwords didn't match");
							return "resetPassword";
						}
						
						
						
					}else{
						model.addAttribute("msg","Entered Otp is not valid");
						return "resetPassword";
					}
				}else {
					model.addAttribute("msg","Entered Otp is not valid");
					return "resetPassword";
				}
			}else {
				model.addAttribute("msg","Entered Otp is not valid");
				return "resetPassword";
			}
				
		}catch(NumberFormatException e) {
			model.addAttribute("msg","Entered Otp is not valid");
			return "resetPassword";
		}catch(Exception e) {
			model.addAttribute("msg","Error occurred");
			return "resetPassword";
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
