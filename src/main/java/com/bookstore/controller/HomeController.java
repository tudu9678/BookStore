package com.bookstore.controller;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.bookstore.domain.User;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.SecurityUtility;
import com.bookstore.utility.MailContructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {


	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailContructor mailContructor;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

	@RequestMapping("/")
	public String index() {
		return "index";
	}


	@RequestMapping("/login")
	public String login(Model model){
		
		model.addAttribute("classActiveLogin",true);
		return "myAccount";
	}

	@RequestMapping("/newUser")
	public String logout(
		Locale locale,
		@RequestParam("token") String  token,
		Model model){
		
		PasswordResetToken passToken = userService.getPasswordResetToken(token);
		
		if ( passToken == null) {
			String message = "Invalid Token.";
			model.addAttribute("message", message);
		}

		User user = passToken.getUser();
		String userName = user.getUsername();

		UserDetails userDetails = userSecurityService.loadUserByUsername(userName);
	
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);	

		model.addAttribute("classActiveNewUser",true);
		return "myAccount";
	}

	@RequestMapping(value = "/newUser",method = RequestMethod.POST)
	public String newUserPost(
		HttpServletRequest request,
		@ModelAttribute("email") String userEmail,
		@ModelAttribute("userName") String userName,
		Model model ) throws Exception {
		
			
			model.addAttribute("email", userEmail);
			model.addAttribute("userName", userName);

			if (userService.findByUsername(userName) != null) {
				model.addAttribute("userNameExists",true);
				return "myAccount";
			}

			if (userService.findByEmail(userEmail) != null) { 
				model.addAttribute("email",true);
				return "myAccount";
			}

			User user = new User();
			user.setUserName(userName);
			user.setEmail(userEmail);

			String password = SecurityUtility.randomPassword();

			String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
			user.setPassword(encryptedPassword);

			Role role = new Role();
			role.setRoleId(1);
			role.setName("ROLE_USER");
			Set<UserRole> userRoles = new HashSet<>();
			userRoles.add(new UserRole(user,role));
			userService.createUser(user,userRoles);
			
			String token = UUID.randomUUID().toString();
			userService.createPasswordResetTokenForUser(user, token);

			String appUrl = "http://"+request.getServerName() + ":"
									 +request.getServerPort()
									 +request.getContextPath();
			SimpleMailMessage email = mailContructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
					password);

			mailSender.send(email);
			model.addAttribute("emailSent","true");
			model.addAttribute("classActiveNewUser",true);
			return "myAccount";





		
		}
	@RequestMapping("/forgotPassword")
	public String forgotPassword(Model model){
		model.addAttribute("classActiveEdit",true);
		return "myProfile";
	}

}
