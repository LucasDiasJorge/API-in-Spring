package com.project.core.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project.core.exception.handler.AppAccessDeniedHandler;
import com.project.core.exception.handler.CustomAuthenticationEntryPointHandler;
import com.project.core.repository.UserRepository;
import com.project.core.security.auth.UserDetailsSeviceImpl;

// @EnableWebSecurity not necessary, spring auto configuration takes care of it
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class JWTConfig {

    private final UserDetailsSeviceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final CustomAuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final AppAccessDeniedHandler appAccessDeniedHandler;

    private final Environment environment;

    @Autowired
    @Lazy(false)
    public JWTConfig(UserDetailsSeviceImpl userService, PasswordEncoder passwordEncoder, UserRepository userRepository,
                     CustomAuthenticationEntryPointHandler authenticationEntryPointHandler,
                     AppAccessDeniedHandler appAuthenticationFailedHandler, Environment environment) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.appAccessDeniedHandler = appAuthenticationFailedHandler;
        this.authenticationEntryPointHandler = authenticationEntryPointHandler;
        this.environment = environment;
    }

    @Bean
    @Primary
    @Lazy(false)
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder).and().getOrBuild();
    }

    @Bean
    @Primary
    @Lazy(false)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] AUTH_WHITELIST = {
                "/v3/**",
                "/swagger-ui/**",
                "/docs/**",
                "/swagger/**",
                "/swagger-ui/**",
                "/docs",
        };

        http.exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPointHandler).accessDeniedHandler(appAccessDeniedHandler));

        http.addFilter(new JWTAuthFilter(authenticationManager(http), userRepository, userService, environment));
        http.addFilter(new JWTFilterValidator(authenticationManager(http), userRepository, environment));
        http.cors(c -> c.configurationSource(r -> new CorsConfiguration().applyPermitDefaultValues()
                .combine(corsConfigurationSource().getCorsConfiguration(r)))).csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                .requestMatchers("/core/**").permitAll()
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated());

        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.getOrBuild();

    }

    @Bean
    @Primary
    @Lazy(false)
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
