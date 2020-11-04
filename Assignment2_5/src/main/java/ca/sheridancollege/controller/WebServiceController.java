package ca.sheridancollege.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.sheridancollege.beans.Contact;
import ca.sheridancollege.database.DatabaseAccess;

@RestController
public class WebServiceController {
	@Autowired
	private DatabaseAccess da;

	@PostMapping(value = "/ContactList/{name}/{number}/{address}/{email}/{role}")
	public int AddContact(@PathVariable String name, @PathVariable int number, @PathVariable String address,
			@PathVariable String email, @PathVariable String role) {
		System.out.println("sd" + name);
		return da.addContact(new Contact(name, number, address, email, role));
	}

	@GetMapping("/ContactList")
	public ArrayList<Contact> View(@RequestParam String role) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();

		if (role.equalsIgnoreCase("GUEST")) {
			contacts.addAll(da.getGuestContacts());
		}
		if (role.equalsIgnoreCase("MEMBER")) {
			contacts.addAll(da.getMembersContacts());
		}
		if (role.equalsIgnoreCase("ADMIN")) {
			contacts.addAll(da.getAdminContacts());
		}
		return contacts;

//		return da.getContacts();
	}

	@PutMapping(value = "/ContactList", headers = { "Content-type=application/json" })
	public void replaceList(@RequestBody List<Contact> contacts) {
		da.replaceContacts(contacts);
	}

	@PostMapping("/ContactList/{id}")
	public Contact Single(@PathVariable int id) {
//		System.out.println(id+"asd");
		return da.getContactsById(id);
	}

	@PutMapping(value = "/ContactList/{id}/{name}/{number}/{address}/{email}/{role}")
	public String Edit(@PathVariable int id, @PathVariable String name, @PathVariable int number,
			@PathVariable String address, @PathVariable String email, @PathVariable String role) {
		da.setContactsById(new Contact(id, name, number, address, email, role));
		return "Updated Successfully";
	}

	@DeleteMapping("/ContactList")
	public int Delete() {
		return da.deleteAll();
	}

	@DeleteMapping("/ContactList/{id}")
	public int Delete(@PathVariable int id) {
		return da.deleteContactsById(id);
	}

}
