package ca.sheridancollege.beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private Long userId;
	private String encrytedPassword;

	public User(String userName, String encrytedPassword) {
		this.userName = userName;
		this.encrytedPassword = encrytedPassword;
	}

}