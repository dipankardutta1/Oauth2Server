package com.example.demo.controller;

import java.io.IOException;
import java.util.regex.Pattern;

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
import com.example.demo.ValidateEmailForRegDto;

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
	
	
	
	@GetMapping("/recruit/register")
	public String goToRecruitRegisterPage() {
		return "recruit/register";
	}
	
	
	@PostMapping("/recruit/register")
	public String registerRecruit(Model model,
			@RequestParam("firstName")String firstName,
    		@RequestParam("lastName")String lastName,
    		@RequestParam("username")String username
			) {
		
		
		if(firstName == null || firstName.isEmpty()) {
			model.addAttribute("msg","First Name is empty !");
			return "recruit/register";
		}
		
		if(lastName == null || lastName.isEmpty()) {
			model.addAttribute("msg","Last Name is empty !");
			return "recruit/register";
		}
		
		if(username == null || username.isEmpty()) {
			model.addAttribute("msg","Email is empty !");
			return "recruit/register";
		}
		
		
		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		
		if(!pattern.matcher(username).matches()) {
			model.addAttribute("msg","Email is not valid !");
			return "recruit/register";
		}
		
		
		if(userService.isEmailAreadyExist(username)) {
			model.addAttribute("msg","Email is already taken !");
			return "recruit/register";
		}
		
		
		
		ValidateEmailForRegDto validateEmailForRegDto = new ValidateEmailForRegDto();
		validateEmailForRegDto.setFirstName(firstName);
		validateEmailForRegDto.setLastName(lastName);
		validateEmailForRegDto.setUsername(username);
		
		userService.sendEmail(username);
		
		model.addAttribute("validateEmailForRegDto", validateEmailForRegDto);
		
		return "recruit/validateEmailForReg";
		
		
		
	}
	
	
	
	
	
	@PostMapping("/recruit/validateEmailForReg")
    public String validateEmailForRecruiterReg(Model model,@Valid ValidateEmailForRegDto validateEmailForRegDto,BindingResult errors){
		
		
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
			return "recruit/validateEmailForReg";
		  }
		
		
		try {
			Integer otpGiven = Integer.valueOf(validateEmailForRegDto.getOtp());
			
			
			
			

			if(otpGiven >= 0){
				Integer serverOtp = otpService.getOtp(validateEmailForRegDto.getUsername());

				if(serverOtp > 0){
					if(otpGiven.equals(serverOtp)){
						
						
						userService.saveRecruiter(validateEmailForRegDto.getFirstName(),validateEmailForRegDto.getLastName(),validateEmailForRegDto.getUsername(),validateEmailForRegDto.getPassword());
						otpService.clearOTP(validateEmailForRegDto.getUsername());
						return "recruit/regSuccess";
						
						
						
					}else{
						model.addAttribute("msg","Entered Otp is not valid");
						return "recruit/validateEmailForReg";
					}
				}else {
					model.addAttribute("msg","Entered Otp is not valid");
					return "recruit/validateEmailForReg";
				}
			}else {
				model.addAttribute("msg","Entered Otp is not valid");
				return "recruit/validateEmailForReg";
			}
				
		}catch(NumberFormatException e) {
			model.addAttribute("msg","Entered Otp is not valid");
			return "recruit/validateEmailForReg";
		}catch(Exception e) {
			model.addAttribute("msg","Error occurred");
			return "recruit/validateEmailForReg";
		}
		
		
		
		
		
		
		
    }
	
	
	
	
	
	
	
	
	@GetMapping("register")
	public String goToRegisterPage() {
		return "registerUser";
	}
	
	
	@PostMapping("/register")
    public String register(Model model,
    		@RequestParam("firstName")String firstName,
    		@RequestParam("lastName")String lastName,
    		@RequestParam("username")String username
    		){
		
		if(firstName == null || firstName.isEmpty()) {
			model.addAttribute("msg","First Name is empty !");
			return "registerUser";
		}
		
		if(lastName == null || lastName.isEmpty()) {
			model.addAttribute("msg","Last Name is empty !");
			return "registerUser";
		}
		
		if(username == null || username.isEmpty()) {
			model.addAttribute("msg","Email is empty !");
			return "registerUser";
		}
		
		
		
		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		
		if(!pattern.matcher(username).matches()) {
			model.addAttribute("msg","Email is not valid !");
			return "registerUser";
		}
		
		
		if(userService.isEmailAreadyExist(username)) {
			model.addAttribute("msg","Email is already taken !");
			return "registerUser";
		}
		
		
		
		ValidateEmailForRegDto validateEmailForRegDto = new ValidateEmailForRegDto();
		validateEmailForRegDto.setFirstName(firstName);
		validateEmailForRegDto.setLastName(lastName);
		validateEmailForRegDto.setUsername(username);
		
		userService.sendEmail(username);
		
		model.addAttribute("validateEmailForRegDto", validateEmailForRegDto);
		
		return "validateEmailForReg";
		
		
		
	}
		
	
	@PostMapping("/validateEmailForReg")
    public String validateEmailForReg(Model model,@Valid ValidateEmailForRegDto validateEmailForRegDto,BindingResult errors){
		
		
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
			return "validateEmailForReg";
		  }
		
		
		try {
			Integer otpGiven = Integer.valueOf(validateEmailForRegDto.getOtp());
			
			
			
			

			if(otpGiven >= 0){
				Integer serverOtp = otpService.getOtp(validateEmailForRegDto.getUsername());

				if(serverOtp > 0){
					if(otpGiven.equals(serverOtp)){
						
						
						userService.saveUser(validateEmailForRegDto.getFirstName(),validateEmailForRegDto.getLastName(),validateEmailForRegDto.getUsername(),validateEmailForRegDto.getPassword());
						otpService.clearOTP(validateEmailForRegDto.getUsername());
						return "regSuccess";
						
						
						
					}else{
						model.addAttribute("msg","Entered Otp is not valid");
						return "validateEmailForReg";
					}
				}else {
					model.addAttribute("msg","Entered Otp is not valid");
					return "validateEmailForReg";
				}
			}else {
				model.addAttribute("msg","Entered Otp is not valid");
				return "validateEmailForReg";
			}
				
		}catch(NumberFormatException e) {
			model.addAttribute("msg","Entered Otp is not valid");
			return "validateEmailForReg";
		}catch(Exception e) {
			model.addAttribute("msg","Error occurred");
			return "validateEmailForReg";
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
