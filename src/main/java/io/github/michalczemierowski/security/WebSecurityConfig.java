package io.github.michalczemierowski.security;

import io.github.michalczemierowski.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] safeUrls = {
                "/",
                "/sign-in",
                "/error",
                "/webjars/**",
                "/js/**",
                "/css/**"
        };

        http
                .authorizeRequests()
                .antMatchers(safeUrls).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/sign-in")
                .permitAll()
                .and()
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                .oauth2Login(o -> o
                        .failureHandler((request, response, exception) ->
                        {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                        })
                        .successHandler((request, response, exception) ->
                        {
                            boolean isNewUser = userService.addUserToDatabaseIfNotExists();
                            if (isNewUser) {
                                // TODO: redirect to page where user can change 'name'
                                response.sendRedirect("/dashboard");
                            } else
                                response.sendRedirect("/dashboard");
                        })
                );
    }
}
