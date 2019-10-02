package com.nhattien;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nhattien.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	MyUserDetailsService userDetailsService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Sét đặt dịch vụ để tìm kiếm User trong Database.
		// Và sét đặt PasswordEncoder.
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	/*
	 * // Create 2 users for demo
	 * 
	 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
	 * Exception {
	 * 
	 * auth.inMemoryAuthentication().withUser("user").password("{noop}user").roles(
	 * "USER").and().withUser("admin") .password("{noop}admin").roles("USER",
	 * "ADMIN");
	 * 
	 * }
	 */

	// Secure the endpoins with HTTP Basic authentication
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Chỉ cho phép user có quyền ADMIN or USER truy cập đường dẫn
		http.authorizeRequests().antMatchers("/product/**").access("hasRole('ADMIN') or hasRole('USER')");
		
		//remember
		http.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(1296000);
		
		// Khi người dùng đã login, với vai trò USER, Nhưng truy cập vào trang yêu cầu vai trò ADMIN, sẽ chuyển hướng tới trang /403
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
		http.authorizeRequests().antMatchers("/register").permitAll();
		http.authorizeRequests().and().formLogin().loginProcessingUrl("/j_spring_security_login").loginPage("/login")
				.defaultSuccessUrl("/product").failureUrl("/login?message=error").usernameParameter("username").passwordParameter("password")
				.and().logout()
				.logoutUrl("/j_spring_security_logout").logoutSuccessUrl("/login?message=logout")// Cấu hình cho Logout Page
				.and().csrf().disable();
	}

}