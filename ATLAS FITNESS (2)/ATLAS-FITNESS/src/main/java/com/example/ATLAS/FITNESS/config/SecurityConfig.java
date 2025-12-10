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
import jakarta.servlet.http.HttpSession;

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
                .requestMatchers("/", "/index", "/home", "/inicio", "/auth/**", 
                                "/css/**", "/js/**", "/images/**", "/webjars/**", 
                                "/error", "/public/**", "/registro").permitAll()
                .requestMatchers("/cliente/**").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("atlasFitnessKey")
                .tokenValiditySeconds(604800) // 7 días
                .rememberMeParameter("remember-me")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/auth/login?accessDenied=true")
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            String redirectUrl = (String) session.getAttribute("url_prior_login");
            
            if (redirectUrl == null || redirectUrl.isEmpty()) {
                // Redirigir según el rol del usuario
                boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
                
                boolean isCliente = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_CLIENTE"));
                
                if (isAdmin) {
                    redirectUrl = "/admin/dashboard"; // Panel de administrador
                } else if (isCliente) {
                    redirectUrl = "/cliente/dashboard"; // Panel del cliente
                } else {
                    redirectUrl = "/"; // Página principal por defecto
                }
            } else {
                session.removeAttribute("url_prior_login");
            }
            
            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}