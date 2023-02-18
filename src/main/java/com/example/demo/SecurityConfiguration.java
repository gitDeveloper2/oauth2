package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomOauthService customOauthService;
    @Autowired
    private UserRepo userRepo;



    public CustomOauthService getCustomOauthService() {
        return customOauthService;
    }

    public void setCustomOauthService(CustomOauthService customOauthService) {
        this.customOauthService = customOauthService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a->
                        a.antMatchers("/","/error","/login","/oauth/**","/webjars/**").permitAll()
                                .anyRequest().authenticated()
                        )
                .exceptionHandling(e->e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .logout(l->l.logoutSuccessUrl("/").permitAll())
                .csrf(c->c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .oauth2Login((f)->{
                    f.userInfoEndpoint().userService(customOauthService);
//
                    f.successHandler(new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            CustomOauthUser customOauthUser= (CustomOauthUser) authentication.getPrincipal();
                            userRepo.save(new User(customOauthUser.getAttribute("name"),"password","github"));
                            System.out.println(customOauthUser.getAttributes());
                            System.out.println("sun");
                            response.sendRedirect("/");
                        }
                    });
                });
    }
}
