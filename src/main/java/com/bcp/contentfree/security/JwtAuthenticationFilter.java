package com.bcp.contentfree.security;


import com.bcp.contentfree.request.LoginRequest;
import com.bcp.contentfree.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.bcp.contentfree.constant.Constant.AUTHORIZATION_HEADER;
import static com.bcp.contentfree.constant.Constant.AUTHORIZATION_PREFIX;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    private JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {

        this.jwtService = jwtService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        xLogger.info("Message : Attempting Authentication");
        try {
            LoginRequest login = new ObjectMapper().readValue(req.getInputStream(), LoginRequest.class);

            return this.getAuthenticationManager()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    login.getUserName(), login.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            xLogger.error("ErrorMessage : Authentication Failed and the cause is " + e.getCause());
            throw new AuthenticationServiceException(e.getMessage());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
        xLogger.info("Message : Authentication was successful: Generating Jwt Response Header");
        String token = jwtService.generateJwt(auth);
        res.addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + token);
        res.setStatus(204);
    }


}
