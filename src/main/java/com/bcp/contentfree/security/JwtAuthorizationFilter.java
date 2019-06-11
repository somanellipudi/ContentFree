package com.bcp.contentfree.security;

import com.bcp.contentfree.service.JwtService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bcp.contentfree.constant.Constant.AUTHORIZATION_HEADER;
import static com.bcp.contentfree.constant.Constant.AUTHORIZATION_PREFIX;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtService jwtService;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    public JwtAuthorizationFilter(AuthenticationManager authManager, JwtService jwtService) {
        super(authManager);
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith(AUTHORIZATION_PREFIX)) {
            xLogger.error("ErrorMessage : Authorization Header was null or invalid");
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                getAuthentication(header.replace(AUTHORIZATION_PREFIX, ""));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
        xLogger.debug("Message : Exiting authorization filter");

        // Return updated jwt
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String jwt) {
        return (UsernamePasswordAuthenticationToken) jwtService.parseJwt(jwt);
    }


}
