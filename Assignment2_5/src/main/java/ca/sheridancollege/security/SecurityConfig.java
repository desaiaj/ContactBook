package ca.sheridancollege.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	LoginAccessDeniedHandler accessDeniedHandler;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
	}

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance()).withUser("ajay")
//				.password("123").roles("USER").and().withUser("xyz").password("Abc1").roles("Hacker");
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// most secure to least secure // antMatchers(httpMethod.POST,"path")s
		http.csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/Registration", "/ContactList", "ContactList/**").permitAll()
				.antMatchers("/Admin/**").hasRole("ADMIN").antMatchers("/Member/**").hasRole("MEMBER")
				.antMatchers("/Guest/**").hasRole("GUEST").antMatchers("/View").hasAnyRole("ADMIN", "GUEST", "MEMBER")
//				.antMatchers(HttpMethod.POST, "/Insert", "/Error/**", "/img/**", "/js/**", "/css/**", "/**").permitAll()
				.antMatchers("/", "/Error/**", "/img/**", "/js/**", "/css/**", "/**").permitAll().anyRequest()
				.authenticated().and().formLogin().loginPage("/login").permitAll().and().logout()
				.invalidateHttpSession(true).clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout")
				.permitAll().and().exceptionHandling().accessDeniedHandler(accessDeniedHandler);
	}
}
