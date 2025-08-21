package com.ariforhanus.wordle.controller;

import com.ariforhanus.wordle.dto.JwtResponse;
import com.ariforhanus.wordle.dto.LoginRequest;
import com.ariforhanus.wordle.entity.User;
import com.ariforhanus.wordle.jwt.JwtUtil;
import com.ariforhanus.wordle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    private final AuthenticationManager authManager;
    private final JwtUtil jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtUtil jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    @Value("${jwt.expiration}")
    private long jwtExpMs;


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

        if(!password.equals(confirmPassword)){
            m.addAttribute("error", "Şifreler eşleşmiyor");
            return"auth/register";
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole("USER");
        users.save(u);


        try {
            users.save(u);
        } catch (DataIntegrityViolationException e) {
            m.addAttribute("error", "kullanıcı adı veya e-posta zaten kayıtlı.");
            return "auth/register";
        }

        return "redirect:/auth/login?registered";
    }

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JwtResponse> apiLogin(@RequestBody LoginRequest req){
        try{
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(req.getUsername(),req.getPassword());
            authManager.authenticate(authToken);

            String token = jwt.generateToken(req.getUsername());
            return ResponseEntity.ok(new JwtResponse(token, jwtExpMs));

        } catch (BadCredentialsException ex){
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/test2")
    public ResponseEntity<String> test2(){
        return ResponseEntity.ok("test2");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("AuthController çalışıyor.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader){
        try {
            String token = authHeader.replace("bearer ", "");

            String username = jwt.extractUsername(token);

            var user = users.findByUsername(username)
                    .orElseThrow(()-> new UsernameNotFoundException("Kullanıcı bulunamadı."));

            Map<String, Object> body = Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "role", user.getRole()
            );
            return ResponseEntity.ok(body);
        }catch(Exception e){
            return ResponseEntity.status(401).body(Map.of("error", "Geçersiz token"));
        }
    }



}
