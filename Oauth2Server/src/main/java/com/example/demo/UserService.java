package com.example.demo;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.PasswordOtp;
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


	public String sendEmail(String username) {
		User user = null;

		if (username.contains("@"))
			user = userRepository.findByEmail(username);
		else
			user = userRepository.findByUsername(username);
		
		
		if(user == null) {
			return null;
		}else {
			// send email
			
			int otp = otpService.generateOTP(user.getEmail());
			
			emailSend(user.getEmail(),otp+"");
			
			
//			PasswordOtp passwordOtp = new PasswordOtp();
//			passwordOtp.setId(UUID.randomUUID().toString());
//			passwordOtp.setOtp(otp);
//			passwordOtp.setOtpOn(new Date());
//			passwordOtp.setEmail(user.getEmail());
//			
//			passwordOtpRepository.save(passwordOtp);
			return user.getEmail();
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

		if (username.contains("@"))
			user = userRepository.findByEmail(username);
		else
			user = userRepository.findByUsername(username);
		
		password = "{bcrypt}"+bCryptPasswordEncoder.encode(password);
		user.setPassword(password);
		
		userRepository.save(user);
		
	}

}
