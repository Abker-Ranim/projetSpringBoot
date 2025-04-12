package tn.ucar.enicar.info.projetspring.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tn.ucar.enicar.info.projetspring.entities.Token;
import tn.ucar.enicar.info.projetspring.entities.TokenType;
import tn.ucar.enicar.info.projetspring.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.config.JwtService;
import tn.ucar.enicar.info.projetspring.entities.Role;
import tn.ucar.enicar.info.projetspring.repositories.TokenRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final userRepo repository ;
    private final TokenRepository tokenRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtService jwtService ;
    private final AuthenticationManager authenticationManager ;


public AuthenticationResponse register (RegisterRequest request){
    System.out.println("Tentative d'inscription: " + request.getEmail());

   var user = User.builder()
           .firstname(request.getFirstname())
           .lastname(request.getLastname())
           .email(request.getEmail())
           .password(passwordEncoder.encode(request.getPassword()))
           .role(request.getRole() != null ? request.getRole() : Role.USER)
           .build();
   var savedUser =repository.save(user);

   var jwtToken = jwtService.generateToken(user);

    revokeAllUserTokens(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .role(user.getRole().name())
            .build();
}

private void revokeAllUserTokens(User user){
    var validUserTokens= tokenRepository.findAllValidTokensByUser(user.getId());
            if(validUserTokens.isEmpty())
                return;
            validUserTokens.forEach(t ->{
                t.setExpired(true);
                t.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
}

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }


    public AuthenticationResponse authenticate (AuthenticationRequest request){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()));

    var user = repository.findByEmail(request.getEmail())
            .orElseThrow() ;
    var jwtToken = jwtService.generateToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .role(user.getRole().name())
            .build();
}
}
