package tn.ucar.enicar.info.projetspring.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET = "QJXCy0hs9+zVQZzoCAkIxQcdR7vqI1gFfcQylyp8haoHKgu7zrovKQtGEok5Obh+J4c44MWSZesi5x9wtjldJzHxUQ0Hzk0VcmMasG833Y6rp5OPLvohVvwCORgthA/Le7aX8tagtjRU8vQ5g9fBEi2bERVRCWZlHuVgHWOHpAg8A8yzfQ7QY7MyH/DUHRNd5JDSbHYfRGkZamcI33aSpH2BhBJC+ZqorjeCEwrdLgU0o4rseXburZudmZn2ageBTRlGGthNTIkR+d9m6EughuMM5nYqlUCw3YFtURn64KXjYn3m7OfXs43H6fyGuBIvOCOGkaqwGz+ToKG/e0svy+iEqPXVOLnndOTVIp3SMEc=";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver ) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims ,UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith( getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

  public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()))&& !isTokenExpired(token);
  }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
