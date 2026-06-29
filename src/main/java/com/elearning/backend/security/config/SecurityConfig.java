package com.elearning.backend.security.config;

import com.elearning.backend.security.filter.JwtAuthFilter;
import com.elearning.backend.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter authFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            return http
                    .csrf(csrf -> csrf.disable())
                    .cors(Customizer.withDefaults())

                    .authorizeHttpRequests(auth -> auth

                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            /* =====================================================
                               FRONTEND (React SPA) ROUTES – MUST BE PERMITTED
                               ===================================================== */
                            .requestMatchers(
                                    "/",
                                    "/index.html",
                                    "/login",
                                    "/demo",
                                    "/student/**",
                                    "/teacher/**",
                                    "/admin/**",
                                    "/quiz/**"
                            ).permitAll()

                            /* =====================================================
                               STATIC ASSETS (Vite / React build)
                               ===================================================== */
                            .requestMatchers(
                                    "/assets/**",
                                    "/static/**",
                                    "/js/**",
                                    "/css/**",
                                    "/images/**",
                                    "/favicon.ico",
                                    "/vite.svg"
                            ).permitAll()

                            /* =====================================================
                               PUBLIC FILE ACCESS (profile photos, materials preview)
                               ===================================================== */
                            .requestMatchers("/uploads/**").permitAll()

                            /* =====================================================
                               PUBLIC APIs
                               ===================================================== */
                            .requestMatchers(
                                    "/api/auth/**",
                                    "/api/test/**",
                                    "/ping",
                                    "/attendance_report.html"
                            ).permitAll()

                            /* =====================================================
                               TEACHER APIs
                               ===================================================== */
                            .requestMatchers("/api/teacher/**").hasAuthority("ROLE_TEACHER")
                            .requestMatchers(HttpMethod.POST, "/api/attendance/bulk").hasAuthority("ROLE_TEACHER")
                            .requestMatchers(HttpMethod.POST, "/api/attendance/teacher/report").hasAuthority("ROLE_TEACHER")
                            .requestMatchers(HttpMethod.POST, "/api/attendance/warn").hasAuthority("ROLE_TEACHER")

                            /* =====================================================
                               STUDENT APIs
                               ===================================================== */
                            .requestMatchers("/api/student/**").hasAuthority("ROLE_STUDENT")
                            .requestMatchers(HttpMethod.POST, "/api/results").hasAuthority("ROLE_STUDENT")

                            /* =====================================================
                            VIDEO ROOM APIs
                            ===================================================== */
                            .requestMatchers(HttpMethod.GET,  "/api/video/rooms/active").hasAuthority("ROLE_STUDENT")
                            .requestMatchers(HttpMethod.GET,  "/api/video/rooms/mine").hasAuthority("ROLE_TEACHER")
                            .requestMatchers(HttpMethod.POST, "/api/video/rooms").hasAuthority("ROLE_TEACHER")
                            .requestMatchers(HttpMethod.POST, "/api/video/rooms/*/end").hasAuthority("ROLE_TEACHER")

                            /* =====================================================
                            CHAT APIs
                            ===================================================== */
                            .requestMatchers("/api/ai/**").permitAll()
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/chat/**").authenticated()


                            /* =====================================================
                               ALL OTHER APIs REQUIRE JWT
                               ===================================================== */

                            .requestMatchers("/api/**").authenticated()
                            /* =====================================================
                               ANYTHING ELSE (fallback)
                               ===================================================== */
                            .anyRequest().authenticated()
                    )

                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )

                    .authenticationProvider(authenticationProvider())

                    .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)

                    .build();   
        }


    // Authentication provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsServiceImpl);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Allow exact role names without ROLE_ prefix
    @Bean
    public static MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setDefaultRolePrefix("");
        return handler;
    }
}
