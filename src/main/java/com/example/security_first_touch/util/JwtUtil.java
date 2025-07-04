package com.example.security_first_touch.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    private Key key;

    @PostConstruct
    public void init() throws IOException {
        this.key = Jwts.SIG.HS256.key().build();
        String strKey = Base64.getEncoder().encodeToString(key.getEncoded());
        try(FileWriter fileWriter = new FileWriter("src//main//resources//secret.txt")){
            log.debug("Записываю ключ в файл");
            fileWriter.write(strKey);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }


    /**
     * Создает jwt токен.
     * Токен формируется когда был получен логин и пароль и этот токен возвращается клиенту
     */
    public String generateToken(UserDetails userDetails) {
        //time handling
        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + lifetime.toMillis());

        // get user data
        String username = userDetails.getUsername();

        // get roles
        List<String> roles = getRoles(userDetails);

        //claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(issuedDate)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public List<String> getUserRoles(String token) {
        return getClaimsFromToken(token).get("roles", List.class);
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    //TODO: Узнать что делает метод
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private List<String> getRoles(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
    }
}
