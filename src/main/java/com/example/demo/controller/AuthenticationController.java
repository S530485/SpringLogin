package com.example.demo.controller;

import java.util.Base64;
import java.util.Random;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Login;
import com.example.demo.model.UserInformation;
import com.example.demo.model.UserSecureInfo;
import com.example.demo.repository.AuthRepository;
import com.example.demo.repository.ConfRepository;

@RestController
public class AuthenticationController {
	
	@Autowired
	private AuthRepository repository;
	@Autowired
	private SmtpMailSender smtpMailSender;
	@Autowired
	private ConfRepository cRepository;
	
	String random = "";
	
	@GetMapping("/")
	public String welcome() {
		return "redirect:/userInformation";
	}
	
	@PostMapping("/userInformation")
	public String userInformationSubmit(@RequestBody UserInformation userInformation) throws MessagingException {
		
		String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        random = salt.toString();
        smtpMailSender.send(userInformation.getEmailId(),"Email Verification",random);
        repository.save(userInformation);
		return "User SignUp Information Saved";
	}
	
	@PostMapping("/confirmation")
	public String confirmation(@RequestBody UserSecureInfo userSecureInfo ) {
		
		if(userSecureInfo.getConfirmationCode().equals(random)) {
			String encodePass = Base64.getEncoder().encodeToString(userSecureInfo.getPassword().getBytes());
			userSecureInfo.setPassword(encodePass);
			cRepository.save(userSecureInfo);
		}
		return "Confirmation Successful";
	}
	
	@PostMapping("/login")
	public String login(@RequestBody Login login) {
		if(repository.findByEmailId(login.getUserName()).getEmailId().equals(login.getUserName())) {
			byte[] decodeArg = Base64.getDecoder().decode(login.getPassword());
			String dArg = new String(decodeArg);
			String decodePwd = cRepository.findByPassword(login.getPassword()).getPassword();
			byte[] decPwd = Base64.getDecoder().decode(decodePwd);
			String dPwd = new String(decPwd);
			if(dArg.equals(dPwd)) {
				return "Login Successful";
			}
			return "Login Failed. Invalid Pasword";
		}
		
		return "Login Failed";
	}

}
