package tn.ucar.enicar.info.projetspring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/api/v1/auth/**")
                                .permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/event/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/task/create").hasRole("RESPONSIBLE")
                                .requestMatchers("/api/v1/task/{taskId}/update-status").hasRole("VOLUNTARY")
                                .requestMatchers("/api/v1/task/{taskId}/assign-note").hasRole("RESPONSIBLE")
                                .requestMatchers("/api/v1/role-request/apply").hasAnyRole("USER")
                                .requestMatchers("/api/v1/role-request/apply-volunteer").hasAnyRole("USER")
                                .requestMatchers("/api/v1/role-request/{requestId}/review").hasRole("ADMIN")
                                .requestMatchers("/api/v1/role-request/{requestId}/review-volunteer").hasRole("RESPONSIBLE")
                                .requestMatchers("/api/v1/role-request/pending").hasAnyRole("ADMIN", "RESPONSIBLE")
                                .requestMatchers("/api/v1/role-request/pending-volunteer").hasRole("RESPONSIBLE")
                                .requestMatchers("/api/v1/team/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/comment/**").permitAll()
                                .requestMatchers("/api/v1/user/**").permitAll()
                                .requestMatchers("/api/v1/leaderboard/**").permitAll()




                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(
                                        (request, response, authentication) ->
                                                SecurityContextHolder.clearContext())
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}