package com.example.demo;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestResource 
{
	
	@Autowired
	 private UserService userService;
	
	@GetMapping("/user/me")
    public Principal user(Principal principal) {
        return principal;
    }
	
	
	@GetMapping("/user/getDetails")
    public ResponseDto userDetail(Principal principal,@RequestParam String username) {
        return userService.getNameByEmail(username);
    }
	
}