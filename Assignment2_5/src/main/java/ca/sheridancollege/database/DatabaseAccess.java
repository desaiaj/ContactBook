package ca.sheridancollege.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Contact;
import ca.sheridancollege.beans.User;

@Repository
public class DatabaseAccess {
	@Autowired
	private NamedParameterJdbcTemplate jdbc;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public int addContact(Contact contact) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "INSERT INTO contacts(name,Number,address,email,Role) VALUES (:name,:number,:address,:email,:role)";
		parameters.addValue("name", contact.getName());
		parameters.addValue("number", contact.getNumber());
		parameters.addValue("address", contact.getAddress());
		parameters.addValue("email", contact.getEmail());
		parameters.addValue("role", contact.getRole());
		return jdbc.update(query, parameters);
	}

	public ArrayList<Contact> getContacts() {
		String q = "Select * from contacts";
		ArrayList<Contact> contacts = (ArrayList<Contact>) jdbc.query(q,
				new BeanPropertyRowMapper<Contact>(Contact.class));
		return contacts;
	}

	public Contact getContactsById(int id) {
		MapSqlParameterSource param = new MapSqlParameterSource();
		String q = "select * from contacts where id=:id";
		param.addValue("id", id);
		return jdbc.query(q, param, new BeanPropertyRowMapper<Contact>(Contact.class)).get(0);
	}

	public void setContactsById(Contact contact) {
		deleteContactsById(contact.getId());
		addContact(contact);
//		MapSqlParameterSource param = new MapSqlParameterSource();
		// String q = "update contacts set name=:name, Number=:number, address=:address,
		// email=:email where id=:id";
//		param.addValue("id", id);
//		param.addValue("name", name);
//		param.addValue("number", number);
//		param.addValue("address", Add);
//		param.addValue("email", email);
//		jdbc.update(q, param);
	}

	public User findUserAccount(String userName) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "SELECT * FROM sec_user WHERE userName=:userName";
		parameters.addValue("userName", userName);
		ArrayList<User> user = (ArrayList<User>) jdbc.query(query, parameters,
				new BeanPropertyRowMapper<User>(User.class));
		if (user.size() > 0) {
			return user.get(0);
		} else {
			return null;
		}
	}

	public List<String> getRolesById(Long id) {
		ArrayList<String> roles = new ArrayList<String>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "SELECT user_role.userId, sec_role.roleName FROM user_role ,sec_role WHERE user_role.roleId=sec_role.roleId and userId=:userId";
		parameters.addValue("userId", id);
		List<Map<String, Object>> rows = jdbc.queryForList(query, parameters);

		for (Map<String, Object> row : rows) {
			roles.add((String) row.get("roleName"));
		}
		return roles;
	}

	public void addUser(User user, int admin, int member, int guest) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "INSERT INTO sec_user(userName,encrytedPassword,ENABLED) VALUES (:name,:Pass,1)";
		parameters.addValue("name", user.getUserName());
		parameters.addValue("Pass", passwordEncoder.encode(user.getEncrytedPassword()));
		jdbc.update(query, parameters);
		user = findUserAccount(user.getUserName());
		query = "INSERT INTO user_role(userId,roleId) VALUES (:id,:role)";
		if (admin == 1) {
			parameters = new MapSqlParameterSource();
			parameters.addValue("id", user.getUserId());
			parameters.addValue("role", admin);
			jdbc.update(query, parameters);
		}
		if (member == 3) {
			parameters = new MapSqlParameterSource();
			parameters.addValue("id", user.getUserId());
			parameters.addValue("role", member);
			jdbc.update(query, parameters);
		}
		if (guest == 2) {
			parameters = new MapSqlParameterSource();
			parameters.addValue("id", user.getUserId());
			parameters.addValue("role", guest);
			jdbc.update(query, parameters);
		}
	}

	public ArrayList<Contact> getGuestContacts() {
		String Query = "SELECT * FROM contacts where Role = 'Guest'";
		return (ArrayList<Contact>) jdbc.query(Query, new BeanPropertyRowMapper<Contact>(Contact.class));
	}

	public ArrayList<Contact> getMembersContacts() {
		String Query = "SELECT * FROM contacts where Role = 'Member'";
		return (ArrayList<Contact>) jdbc.query(Query, new BeanPropertyRowMapper<Contact>(Contact.class));
	}

	public ArrayList<Contact> getAdminContacts() {
		String Query = "SELECT * FROM contacts where Role = 'Admin'";
		return (ArrayList<Contact>) jdbc.query(Query, new BeanPropertyRowMapper<Contact>(Contact.class));
	}

	public int deleteContactsById(int id) {
		MapSqlParameterSource param = new MapSqlParameterSource();
		String q = "delete from contacts where id=:id";
		param.addValue("id", id);
		return jdbc.update(q, param);
	}

	public int deleteAll() {
		String q = "delete from contacts where id=:id";
		return jdbc.update(q, new HashMap());
	}

	public void replaceContacts(List<Contact> contacts) {
		deleteAll();
		for (Contact c : contacts) {
			addContact(c);
		}
	}
}
