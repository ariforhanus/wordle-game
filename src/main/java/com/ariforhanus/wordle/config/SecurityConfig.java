package com.ariforhanus.wordle.config;


import com.ariforhanus.wordle.service.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final AppUserDetailsService uds;

    public SecurityConfig(AppUserDetailsService uds) {
        this.uds = uds;
    }

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/word").permitAll()
                        .requestMatchers("/api/**", "/api/leaderboard/**").permitAll()
                        .requestMatchers("/api/submit").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/auth/login").permitAll()
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/"))
                .userDetailsService(uds);
        return http.build();
    }
}
