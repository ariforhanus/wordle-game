package com.ariforhanus.wordle.controller;


import com.ariforhanus.wordle.domain.User;
import com.ariforhanus.wordle.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, @RequestParam(required = false) String registered, Model m) {
        if ( error != null) {
            m.addAttribute("error", "Kullanıcı adı veya şifre hatalı.");

        }
        if ( registered != null ) {
            m.addAttribute("msg", "Kayıt Başarılı! Giriş yapabilirsin");
        }
        return "auth/login";
    }


    @GetMapping("/register")
    public String registerForm(){
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword, Model m) {

        if(username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            m.addAttribute("error", "Kullanıcı adı, e-posta ve şifre zorunlu.");
            return "auth/register";
        }

        username = username.trim();

        if (users.existsByUsername(username)) {
            m.addAttribute("error", "Bu kullanıcı adı zaten alınmış.");
            return "auth/register";
        }
        if (users.existsByEmail(email)) {
            m.addAttribute("error", "Bu e‑posta zaten kayıtlı.");
            return "auth/register";
        }

        var u = User.builder()
                .username(username)
                .email(email)
                .passwordHash(encoder.encode(password))
                .role("USER")
                .build();

        try {
            users.save(u);
        } catch (DataIntegrityViolationException e) {
            m.addAttribute("error", "kullanıcı adı veya e-posta zaten kayıtlı.");
            return "auth/register";
        }

        return "redirect:/auth/login?registered";
    }

}
