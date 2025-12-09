package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.dto.LoginDTO;
import com.example.ATLAS.FITNESS.dto.RegistroClienteDTO;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute @Valid LoginDTO loginDTO,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/login";
        }
        
        try {
            Usuario usuario = authService.autenticar(loginDTO);
            session.setAttribute("usuario", usuario);
            session.setAttribute("cliente", usuario.getCliente());
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroClienteDTO());
        return "auth/registro";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute @Valid RegistroClienteDTO registroDTO,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/registro";
        }
        
        if (!registroDTO.passwordsCoinciden()) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "auth/registro";
        }
        
        try {
            authService.registrarCliente(registroDTO);
            redirectAttributes.addFlashAttribute("success", 
                "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/registro";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    @GetMapping("/olvido-password")
    public String mostrarOlvidoPassword() {
        return "auth/olvido-password";
    }
    
    @PostMapping("/olvido-password")
    public String procesarOlvidoPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            authService.solicitarResetPassword(email);
            redirectAttributes.addFlashAttribute("success", 
                "Se ha enviado un email con las instrucciones para restablecer tu contraseña.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar la solicitud: " + e.getMessage());
        }
        return "redirect:/auth/olvido-password";
    }
    
    @GetMapping("/reset-password")
    public String mostrarResetPassword(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }
    
    @PostMapping("/reset-password")
    public String procesarResetPassword(@RequestParam String token,
                                       @RequestParam String nuevaPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!nuevaPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/auth/reset-password?token=" + token;
        }
        
        try {
            authService.resetPassword(token, nuevaPassword);
            redirectAttributes.addFlashAttribute("success", 
                "Contraseña restablecida exitosamente. Ahora puedes iniciar sesión.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/reset-password?token=" + token;
        }
    }
}