package com.busvia.service;
import io.jsonwebtoken.*;
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

    public static  final String SECRET="fgBDT07Pnjm3QmX/2ndPpiqNc+fZqBAFeXIz3Tbq0BPrONjSLp3nZPjkiyLyAsMp";
//    public static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000;

     public String extractUsername(String token) {
         if (token == null || token.isEmpty()) {
             return "Token is null or empty";
         }

        return extractClaim(token, Claims::getSubject);
    }


    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }



    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }



    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSignKey())
                   .build().parseClaimsJws(token)
                   .getBody();
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public String generateToken(String userName,String role){
                Map<String, Object> claim= new HashMap<>();
                claim.put("userName",userName );
                claim.put("role",role);
                return  createToken(claim,userName);
            }
    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date((System.currentTimeMillis())))
                .setExpiration(new Date(System.currentTimeMillis() + 19 * 60 * 60 * 1000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }


    private Key getSignKey() {
                byte[]keyBytes= Decoders.BASE64.decode(SECRET);
                return Keys.hmacShaKeyFor(keyBytes);
    }

}
