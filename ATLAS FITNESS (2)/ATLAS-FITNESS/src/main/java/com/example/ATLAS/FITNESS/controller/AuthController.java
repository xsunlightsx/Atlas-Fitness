package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.dto.LoginDTO;
import com.example.ATLAS.FITNESS.dto.RegistroClienteDTO;
import com.example.ATLAS.FITNESS.model.Cliente;
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
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión exitosamente");
        }
        
        if (registered != null) {
            model.addAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión");
        }
        
        if (!model.containsAttribute("loginDTO")) {
            model.addAttribute("loginDTO", new LoginDTO());
        }
        
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute @Valid LoginDTO loginDTO,
                               BindingResult result,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/login";
        }
        
        try {
            Usuario usuario = authService.autenticar(loginDTO);
            
            // Guardar usuario en sesión
            session.setAttribute("usuario", usuario);
            
            // Buscar y guardar cliente asociado
            if (usuario.getCliente() != null) {
                session.setAttribute("cliente", usuario.getCliente());
            } else if (usuario.getClienteId() != null) {
                // Si no está cargado el cliente, buscar en la base de datos
                // Esto requiere que el AuthService tenga acceso al ClienteService
                // Por simplicidad, por ahora solo guardamos el ID
                session.setAttribute("clienteId", usuario.getClienteId());
            }
            
            // Redirigir según rol
            String redirectPath = determinarRedireccion(usuario);
            
            return redirectPath;
            
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginDTO", loginDTO);
            return "auth/login";
        }
    }
    
    private String determinarRedireccion(Usuario usuario) {
        if (usuario.esAdmin()) {
            return "redirect:/admin/dashboard";
        } else if (Usuario.Rol.RECEPCIONISTA.equals(usuario.getRol())) {
            return "redirect:/recepcion/dashboard";
        } else if (Usuario.Rol.ENTRENADOR.equals(usuario.getRol())) {
            return "redirect:/entrenador/dashboard";
        } else if (Usuario.Rol.CLIENTE.equals(usuario.getRol())) {
            return "redirect:/";
        }
        
        // Por defecto para clientes
        return "redirect:/";
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        if (!model.containsAttribute("registroDTO")) {
            model.addAttribute("registroDTO", new RegistroClienteDTO());
        }
        return "auth/registro";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute @Valid RegistroClienteDTO registroDTO,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("registroDTO", registroDTO);
            return "auth/registro";
        }
        
        // Verificar que las contraseñas coincidan
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("registroDTO", registroDTO);
            return "auth/registro";
        }
        
        try {
            authService.registrarCliente(registroDTO);
            redirectAttributes.addFlashAttribute("success", 
                "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/auth/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registroDTO", registroDTO);
            return "auth/registro";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Sesión cerrada exitosamente");
        return "redirect:/auth/login?logout=true";
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
            // Siempre mostrar éxito por seguridad
            redirectAttributes.addFlashAttribute("success", 
                "Si el email existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar la solicitud");
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
    
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "auth/acceso-denegado";
    }
}