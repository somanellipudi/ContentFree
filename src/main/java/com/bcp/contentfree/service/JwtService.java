package com.bcp.contentfree.service;


import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Setter
@Getter
@Service
@ConfigurationProperties(prefix = "jwt")
public class JwtService {

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    private long expirationTime;
    private String issuer;
    private String key;


    public String generateJwt(Authentication authentication) {

        String username = ((LoginUser) (authentication.getPrincipal())).getUsername();
        xLogger.debug("Message : Creating JWT, for the userId {}", username);

        String token =
                Jwts.builder()
                        .setSubject(((LoginUser) authentication.getPrincipal()).getUsername())
                        .setIssuer(issuer)
                        .setIssuedAt(Date.from(Instant.now()))
                        .setExpiration(Date.from(Instant.now().plusSeconds(getExpirationTime())))
                        .claim("userName", username)
                        .signWith(SignatureAlgorithm.HS512, key.getBytes())
                        .compact();
        xLogger.debug("Message : Generated JWT -  {}", token);
        return token;
    }


    public Authentication parseJwt(String token) {
        if (token != null) {
            // parse the token.
            Claims claims = Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(token).getBody();

            User user = User.builder().userName(claims.getSubject()).build();
            String userName = (String) claims.get("userName");
            user.setUserName(userName);
            LoginUser userDetails = new LoginUser(user);
            return new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());

        }
        throw new JwtException("Invalid jwt");
    }


}
