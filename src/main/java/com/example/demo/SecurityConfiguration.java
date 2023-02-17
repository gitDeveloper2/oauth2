package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a->
                        a.antMatchers("/","/error","/webjars/**").permitAll()
                                .anyRequest().authenticated()
                        )
                .exceptionHandling(e->e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .logout(l->l.logoutSuccessUrl("/").permitAll())
                .csrf(c->c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .oauth2Login((f)->{
                    f.failureHandler(((request, response, exception) -> {
                        request.getSession().setAttribute("error.message",exception.getMessage());
                                System.out.println("failed omce");

                    })
                    );
                });
    }
}
