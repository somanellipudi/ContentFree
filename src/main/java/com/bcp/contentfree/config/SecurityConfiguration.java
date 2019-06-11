package com.bcp.contentfree.config;


import com.bcp.contentfree.security.JwtAuthenticationFilter;
import com.bcp.contentfree.security.JwtAuthorizationFilter;
import com.bcp.contentfree.service.JwtService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.Setter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.validation.annotation.Validated;

@Validated
@EnableWebSecurity
@ConfigurationProperties(prefix = "security")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableEncryptableProperties
@Setter
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    @Autowired
    JwtService jwtService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.headers()
                .disable()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/favicon.ico")
                .permitAll()
                .antMatchers("/resources/**")
                .permitAll()

                .antMatchers( "/swagger-ui.html#/*")
                .permitAll()


                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(http401EntryPoint())
                .and()
                .addFilter(jwtAuthenticationFilter())
                .addFilter(jwtAuthorizationFilter())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers()
                .frameOptions()
                .deny()
                .and()
                .logout()
                .and()
                .cors();


    }



    public Http403ForbiddenEntryPoint http401EntryPoint() {
        return new Http403ForbiddenEntryPoint();
    }


    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jwtAuthenticationFilter.setFilterProcessesUrl("/authentication/login");
        return jwtAuthenticationFilter;
    }


    @Bean
    JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManager(), jwtService);
    }


}
