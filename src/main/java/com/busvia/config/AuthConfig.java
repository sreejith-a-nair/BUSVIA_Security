package com.busvia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthConfig {

    @Autowired
    JwtRequestFilter jwtRequestFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws  Exception{
        return http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**","/user/signup").permitAll()
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    private static final String[]  AUTH_WHITELIST={
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/signup","/login","/api/").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin((form) -> form
//                        .loginPage("/login")
//                        .permitAll()
//                )
//                .logout((logout) -> logout.permitAll())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//   public UserDetailsService userDetailsService(){
//     return  new CustomUserDetailsService();
//   }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return daoAuthenticationProvider;
//    }
    @Bean
    public  PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{
        return  configuration.getAuthenticationManager();
    }

}
