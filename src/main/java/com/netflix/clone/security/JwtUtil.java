package com.netflix.clone.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY= 30L * 24 * 60 * 1000;

    @Value("${jwt.secret:defaultSecretKeyForNetflixClonedefaultSecretKeyForNetflixClone}")
    private String secret;

    private SecretKey getSigninKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getUserNameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);

    }

    public String getRoleFromToken(String token){
        return getClaimFromToken(token,claims -> claims.get("role",String.class));
    }

    public Date getExpirationDateFromToken(String token){
          return getClaimFromToken(token,Claims::getExpiration);
    }



    private <T> T getClaimFromToken(String token, Function<Claims,T> claimsResolver){
        final Claims claims= getAllClaimsFromTokens(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromTokens(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token){
        final Date expiration  = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String username , String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        return doGenerateToken(claims,username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigninKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getAllClaimsFromTokens(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

}
