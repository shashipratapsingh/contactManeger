package com.smart.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.service.ContactService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
    @Autowired 
    private ContactService contactService;

	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String email=principal.getName();
		//System.out.println("EMAIL " + email);
		
		//get the user using userName(Email)
		
		User user= userRepository.findByEmail(email);
	//	System.out.println("USER " + user);
		model.addAttribute("user",user);
	}
	
	//dashboard home
	
	@RequestMapping("/index")
	public String dashboard(Model m) {
		m.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactFrom(Model m) {
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,HttpSession session) {
		try {
			
		String name=principal.getName();
		User user=this.userRepository.findByEmail(name);
		
		//processing and uploading file..
		
		if(file.isEmpty()) {
			
			//if the file is empty than try our message
			System.out.println("File is empty");
			contact.setImage("contact.jpg");
			
		}else {
			
			//file the file to folder and update the name to contact
			contact.setImage(file.getOriginalFilename());
			
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		
		contact.setUser(user);
		user.getContacts().add(contact)	;
		this.userRepository.save(user);
		System.out.println("DATA "+contact);
		
		//message success
		 session.setAttribute("message", new Message("Your contact is added !! Add more..","success"));
		}catch(Exception e) {
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			//message error
			session.setAttribute("message", new Message("Something went wrong !! Try again..","danger"));
		}
		return "normal/add_contact_form";
	}
	
	//show contacts handler
	//per page =5[n]
	//current page = 0 [page]
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		String name = principal.getName();
		User user = this.userRepository.findByEmail(name);
		
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageRequest);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model m,Principal principal) {
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact= contactOptional.get();
		
		String name=principal.getName();
		User user=this.userRepository.findByEmail(name);
		if(user.getId()==contact.getUser().getId()) {
			m.addAttribute("contact",contact);
			m.addAttribute("title",contact.getName());
		}
		return "normal/contact_detail";
	}

	//delete handler
	
	@GetMapping("/delete/{cid}")
	public String  deleteContact(@PathVariable("cid") Integer cId,Model m,HttpSession session,Principal principal) {
		
		Contact contact = this.contactRepository.findById(cId).get();
		
		//check...Assignment..
		System.out.println("Contact "+contact.getcId());
		User user=this.userRepository.findByEmail(principal.getName());
		
		user.getContacts().remove(contact);
		
		//contact.setUser(null);
		this.userRepository.save(user);
		session.setAttribute("message", new Message("Contact Delete Succesfully...","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title","Update Contact");
		Contact contact=this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update_form";
	}
	
	//update contact handler
	@RequestMapping(value="/process-update",method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file ,
			       Model m ,HttpSession session,Principal principal) {
		
		try {
			
			//old contact details
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
			
			//image..
			if(!file.isEmpty()) {
				//file work
				//rewrite
				
				//delete old photo
				
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldcontactDetail.getImage());
				file1.delete();
				
				//update new photo
				File saveFile=new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}else {
				contact.setImage(oldcontactDetail.getImage());
			}
				User user=this.userRepository.findByEmail(principal.getName());
				contact.setUser(user);
				this.contactRepository.save(contact);
				
				session.setAttribute("message", new Message("Your contact is update..","success"));
	
		}catch(Exception e) {
			e.printStackTrace();
	   }
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	//import process
	
	@GetMapping("/show-import")
	public String showImportPage(Model m) {
		m.addAttribute("title","Import Page");
		return "normal/import";
	}
	
	
	// excel file upload
   @PostMapping("/upload-contact-data")
		  public String handleFileUpload(@RequestParam("file") MultipartFile file,
		          @RequestParam(value = "numberOfSheet", required = false) Integer numberOfSheet,
		          RedirectAttributes redirectAttributes,Principal principal) throws EncryptedDocumentException, InvalidFormatException {
		try {
			
			String name=principal.getName();
			
		String result = contactService.upload(file, numberOfSheet,name);
		redirectAttributes.addFlashAttribute("message", result);
		
		} catch (IOException e) {
		e.printStackTrace(); // Log the error or handle it appropriately
		redirectAttributes.addFlashAttribute("error", "Error occurred during file upload.");
		
		}
		return "redirect:/user/show-import"; // Redirect to the upload form page after processing
		
		}
    
	   
	   //your profile handler
	   
	   @GetMapping("/profile")
	   public String yourProfile(Model m) {
		   m.addAttribute("tilte","Profile Page");
		   return "normal/profile";
	   }
}
