package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new CustomUserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider getDaoAuthenticationProvider(){
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.getUserDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		    http.csrf().disable()
		        .authorizeRequests()
		            .requestMatchers("/admin/**").hasRole("ADMIN")
		            .requestMatchers("/user/**").hasRole("USER")
		            .requestMatchers("/**").permitAll()
		            .and()
		        .formLogin()
		        .loginPage("/login")
		        .loginProcessingUrl("/dologin")
		        .defaultSuccessUrl("/user/index");
		         
		    return http.build();
		}
	
}


