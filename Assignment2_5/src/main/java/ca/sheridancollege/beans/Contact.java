package ca.sheridancollege.beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int Number;
	private int number;

	private String address;
	private String email;
	private String Role;
	private String role;

	public Contact(String name, int number, String address, String email, String role) {
		this.name = name;
		Number = number;
		this.address = address;
		this.email = email;
		Role = role;
	}

	public Contact(int id, String name, int number, String address, String email, String role) {
		this.id = id;
		this.name = name;
		Number = number;
		this.address = address;
		this.email = email;
		Role = role;
	}

}