package ca.sheridancollege.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import ca.sheridancollege.beans.Contact;
import ca.sheridancollege.beans.User;
import ca.sheridancollege.database.DatabaseAccess;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "index.html";
	}

	@GetMapping("/AddContact")
	public String AddContact(Model model) {
		model.addAttribute("c", new Contact());
		return "AddContact.html";
	}

	@GetMapping("/Member")
	public String goHello() {
		return "/Member/index.html";
	}

	@GetMapping("/login")
	public String login() {
		return "login.html";
	}

	@GetMapping("/Admin")
	public String goWelcome() {
		return "/Admin/index.html";
	}

	@GetMapping("/Guest")
	public String goHome() {
		return "/Guest/index.html";
	}

	@GetMapping("/Register")
	public String Register() {
		return "Registration.html";
	}

	@GetMapping("/Registration")
	public String Registration() {
		return "Registration.html";
	}

	@PostMapping("/Registration")
	public String CreateUser(@RequestParam String Username, @RequestParam String pass,
			@RequestParam(defaultValue = "0") int admin, @RequestParam(defaultValue = "0") int member,
			@RequestParam(defaultValue = "0") int guest) {
		User user = new User(Username, pass);
		// da.addUser(user, admin, member, guest);
		return "redirect:/";
	}

	@GetMapping("/access-denied")
	public String error() {
		return "/Error/access-denied.html";
	}

//	public String AddContact(@RequestParam String name, @RequestParam int Number, @RequestParam String address,
//			@RequestParam String email, @RequestParam String role, RestTemplate restTemplate) {
	@PostMapping("/Insert")
	public String AddContact(@ModelAttribute Contact c, RestTemplate restTemplate) {
//		Contact d = new Contact(name, Number, address, email, role);
//		ResponseEntity<String> entity = null;
		restTemplate.postForLocation("http://localhost:8080/ContactList/" + c.getName() + "/" + c.getNumber() + "/"
				+ c.getAddress() + "/" + c.getEmail() + "/" + c.getRole(), null);
//		model.addAttribute("Student", responseEntity.getBody());
		// da.addContact(d);
		return "redirect:/";
	}

	@GetMapping("/View")
	public String view(Authentication authentication, RestTemplate restTemplate, Model model) {
		ArrayList<Contact> list = new ArrayList<Contact>();
//		list = da.getContacts();

		ResponseEntity<ArrayList<Contact>> contacts = null;
		for (GrantedAuthority ga : authentication.getAuthorities()) {
			System.out.println(ga.getAuthority());
			if (ga.getAuthority().equalsIgnoreCase("ROLE_GUEST")) {
				contacts = (ResponseEntity<ArrayList<Contact>>) restTemplate
						.getForEntity("http://localhost:8080/ContactList?role=Guest", list.getClass());
				list.addAll(contacts.getBody());
//				ResponseEntity<Contact[]> contactsq = restTemplate
//						.getForEntity("http://localhost:8080/ContactList?role=GUEST", Contact[].class);
//				list.addAll(contactsq.getBody());
			}
			if (ga.getAuthority().equalsIgnoreCase("ROLE_MEMBER")) {
				contacts = (ResponseEntity<ArrayList<Contact>>) restTemplate
						.getForEntity("http://localhost:8080/ContactList?role=MEMBER", list.getClass());
				list.addAll(contacts.getBody());
			}
			if (ga.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
				System.out.println("admin");
				contacts = (ResponseEntity<ArrayList<Contact>>) restTemplate
						.getForEntity("http://localhost:8080/ContactList?role=ADMIN", list.getClass());
				list.addAll(contacts.getBody());
//				System.out.println(contacts.getBody());
			}
		}

		System.out.println(contacts.getBody());
		model.addAttribute("list", list);
		return "/ViewContact/ViewContact.html";
	}

	@GetMapping("/ViewAll")
	public String viewall(Model model, RestTemplate restTemplate) {
//		ArrayList<Contact> list = new ArrayList<Contact>();
//		list = da.getContacts();

		ResponseEntity<Contact[]> responseEntity = restTemplate.getForEntity("http://localhost:8080/ContactList",
				Contact[].class);

//		ArrayList<Contact> c = new ArrayList<Contact>();
//		ResponseEntity<ArrayList<Contact>> responseEntity = (ResponseEntity<ArrayList<Contact>>) restTemplate
//				.getForEntity("http://localhost:8080/ContactList", c.getClass());
		System.out.println(responseEntity.getBody());
		model.addAttribute("list", responseEntity.getBody());
//		model.addAttribute("list", list);
		return "ViewContact.html";
	}

	@GetMapping("/edit/{id}")
	public String Edit(@PathVariable int id, Model model, RestTemplate restTemplate) {
//		Contact c = da.getContactsById(id);
//		model.addAttribute("list", c);
		ResponseEntity<Contact> responseEntity = restTemplate.postForEntity("http://localhost:8080/ContactList/" + id,
				null, Contact.class);
		System.out.println(responseEntity.getBody());
		model.addAttribute("list", responseEntity.getBody());
		return "edit.html";
	}

//	public String Edit(@RequestParam int id, @RequestParam String name, @RequestParam int Number,
//			@RequestParam String address, @RequestParam String email, @RequestParam String role, Model model,
//			RestTemplate restTemplate) {
	@GetMapping("/editRecord")
	public String Edit(@ModelAttribute Contact list, RestTemplate restTemplate) {
		restTemplate.put("http://localhost:8080/ContactList/" + list.getId() + "/" + list.getName() + "/"
				+ list.getNumber() + "/" + list.getAddress() + "/" + list.getEmail() + "/" + list.getRole(), null);
		// da.setContactsById(id, name, Number, address, email);
		// model.addAttribute("list", da.getContacts());
		return "redirect:/View";
	}

	@GetMapping("/Delete/{id}")
	public String Delete(@PathVariable int id, Model model, RestTemplate restTemplate) {
//		da.deleteContactsById(id);
		restTemplate.delete("http://localhost:8080/ContactList/" + id);
//		model.addAttribute("list", da.getContacts());
		return "redirect:/View";
	}
}
