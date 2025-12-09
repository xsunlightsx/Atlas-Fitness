package com.example.ATLAS.FITNESS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            // Lógica personalizada después de login exitoso
            String username = authentication.getName();
            System.out.println("Login exitoso para: " + username);
            response.sendRedirect("/");
        };
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Temporal para pruebas
            .authorizeHttpRequests(auth -> auth
                // Páginas públicas
                .requestMatchers(
                    "/",
                    "/auth/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/error",
                    "/api/public/**"
                ).permitAll()
                
                // Rutas por roles
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/recepcionista/**").hasRole("RECEPCIONISTA")
                .requestMatchers("/entrenador/**").hasRole("ENTRENADOR")
                .requestMatchers("/cliente/**").hasAnyRole("CLIENTE", "ADMIN", "RECEPCIONISTA")
                
                // Por defecto, autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
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
                .accessDeniedPage("/auth/acceso-denegado")
            )
            .authenticationProvider(authenticationProvider());
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Fuerza 12 para coincidir con tu hash $2a$12$...
        return new BCryptPasswordEncoder(12);
    }
}