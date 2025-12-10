package com.example.ATLAS.FITNESS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/home", "/inicio").permitAll()
                .requestMatchers("/auth/login", "/auth/registro", "/auth/olvido-password").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/error", "/public/**").permitAll()
                .requestMatchers("/productos/**").permitAll()
                .requestMatchers("/cliente/membresias").permitAll()
                .requestMatchers("/cliente/membresias/**").permitAll()
                
                .requestMatchers("/cliente/membresia").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/cliente/dashboard").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/cliente/perfil").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/cliente/rutinas").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/cliente/historial").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/cliente/mi-membresia").hasAnyRole("CLIENTE", "ADMIN")
                
                .requestMatchers("/carrito/**").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/auth/logout").authenticated()
                .requestMatchers("/cliente/**").hasAnyRole("CLIENTE", "ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/cliente/dashboard", true)
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/auth/access-denied")
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            String redirectUrl = (String) session.getAttribute("url_prior_login");
            
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("url_prior_login");
                response.sendRedirect(request.getContextPath() + redirectUrl);
                return;
            }
            
            boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/cliente/dashboard");
            }
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}