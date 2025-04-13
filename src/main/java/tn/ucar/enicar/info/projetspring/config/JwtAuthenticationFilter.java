package tn.ucar.enicar.info.projetspring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tn.ucar.enicar.info.projetspring.repositories.TokenRepository;

import java.io.IOException;
@Component
@RequiredArgsConstructor

@Slf4j
public class JwtAuthenticationFilter  extends OncePerRequestFilter {
    private final JwtService jwtService ;
    private final UserDetailsService userDetailsService ;
    private final TokenRepository tokenRepository ;

    @Override
    protected void doFilterInternal(
         @NonNull HttpServletRequest request,
         @NonNull HttpServletResponse response,
         @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt ;
    final String userEmail ;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        logger.debug("No Bearer token found");
        filterChain.doFilter(request, response);
        return;
    }
    jwt = authHeader.substring(7);
        userEmail =jwtService.extractUsername(jwt);
            logger.debug("Extracted email: " + userEmail);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null )  {
            UserDetails userDetails=this.userDetailsService.loadUserByUsername(userEmail);
            logger.debug("User authorities: " + userDetails.getAuthorities());
            var isTokenValid =tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.validateToken(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("Authentication set for: " + userEmail);
            }
        }
        filterChain.doFilter(request, response);
        }
    }
