package com.example.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.PasswordOtp;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;

@Service(value = "userService")
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	public OtpService otpService;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	
	public User getUserByUsernameOrEmail(String username) {
		User user = null;

		if (username.contains("@"))
			user = userRepository.findByEmail(username);
		else
			user = userRepository.findByUsername(username);

		return user;
	}
	
	public Boolean isEmailAreadyExist(String username) {
		User user = userRepository.findByUsername(username);
		
		if(user == null) {
			return false;
		}else {
			return true;
		}

		
	}


	public String sendEmail(String username) {
		//User user = null;

		/*if (username.contains("@"))
			user = userRepository.findByEmail(username);
		else
			user = userRepository.findByUsername(username);
		*/
		
		if(username == null || username.isEmpty()) {
			return null;
		}else {
			// send email
			
			int otp = otpService.generateOTP(username);
			
			emailSend(username,otp+"");
			
			
//			PasswordOtp passwordOtp = new PasswordOtp();
//			passwordOtp.setId(UUID.randomUUID().toString());
//			passwordOtp.setOtp(otp);
//			passwordOtp.setOtpOn(new Date());
//			passwordOtp.setEmail(user.getEmail());
//			
//			passwordOtpRepository.save(passwordOtp);
			return username;
		}
		
	}
	
	
	
	private void emailSend(String email,String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);

        msg.setSubject("Genuine Hire");
        msg.setText("OTP : " + otp);

        javaMailSender.send(msg);

    }

	@Transactional
	public void updatePassword(String password, String username) {
		User user = null;
		/*
		if (username.contains("@"))
			user = userRepository.findByEmail(username);
		else
			user = userRepository.findByUsername(username);
		*/
		
		user = userRepository.findByUsername(username);
		
		
		password = "{bcrypt}"+bCryptPasswordEncoder.encode(password);
		user.setPassword(password);
		
		userRepository.save(user);
		
	}

	@Transactional
	public void saveUser(String firstName, String lastName, String username, String password) {
		
		List<Role> roles = new ArrayList<Role>();
		roles.add(new Role(null, "role_user"));
		
		
		User user = new User();
		user.setAccountNonExpired(false);
		user.setAccountNonLocked(false);
		user.setCredentialsNonExpired(false);
		user.setEmail(username);
		user.setEnabled(true);
		user.setId(UUID.randomUUID().toString());
		user.setPassword(password);
		user.setRoles(roles);
		user.setUsername(username);
		
		
		userRepository.save(user);
		
	}

}
