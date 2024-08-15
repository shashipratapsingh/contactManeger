package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String showHome(Model model) {
		model.addAttribute("title","Home-Smart Contact Manager");
		return "home";	
	}
	
	@RequestMapping("/about")
	public String showAbout(Model model) {
		model.addAttribute("title","About-Smart Contact Manager");
		return "about";	
	}
	
	@RequestMapping("/signup")
	public String showSignup(Model model) {
		model.addAttribute("title","Register-Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";	
	}
	
	//handler for registering user
	@RequestMapping(value="/do_register",method = RequestMethod.POST)
	public String registerUser(@ModelAttribute("user") User user,@RequestParam(value="agreement",
			defaultValue="false")boolean agreement,Model model,HttpSession session) {
		try {
			
			if(!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			user.setRole("Role_User");
			user.setEnabled(true);
			user.setImageUrl("notebook.jpg");
			
			System.out.println("Agreement " +agreement);
			System.out.println("User "+user);
			
			User result=this.userRepository.save(user);
			model.addAttribute("user",new User());
			model.addAttribute("message",new Message("Successfully Registered !!","alert-success"));
			return "signup";
			
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("message",new Message("Something Went Wrong !!" + e.getMessage(),"alert-danger"));
			return "signup";
		}
	}
}
