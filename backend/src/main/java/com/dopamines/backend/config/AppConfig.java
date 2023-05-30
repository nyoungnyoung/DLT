package com.dopamines.backend.config;

import com.dopamines.backend.security.CustomAuthenticationFilter;
import com.dopamines.backend.security.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @RequiredArgsConstructor
    @Configuration
    @EnableWebSecurity
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        private final AuthenticationProvider authenticationProvider;
        private final AuthenticationFailureHandler authenticationFailureHandler;
        private final AuthenticationSuccessHandler authenticationSuccessHandler;
        private final CustomAuthorizationFilter customAuthorizationFilter;
        private final AccessDeniedHandler accessDeniedHandler;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            CustomAuthenticationFilter customAuthenticationFilter =
                    new CustomAuthenticationFilter(authenticationManagerBean());
            customAuthenticationFilter.setFilterProcessesUrl("/account/login");
            customAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
            customAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);

            http.csrf().disable();

            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 사용 X
            http.authorizeRequests().antMatchers("/account/signup/**", "/account/login/**", "/account/refresh/**", "/account/oauth/**", "/v3/api-docs/**", "/swagger*/**", "/test/**", "/ws/**").permitAll();
            http.authorizeRequests().antMatchers("/account/my/**").hasAnyAuthority("ROLE_USER");
            http.authorizeRequests().antMatchers("/account/admin/**").hasAnyAuthority("ROLE_ADMIN");
            http.authorizeRequests().anyRequest().authenticated();
            http.addFilter(customAuthenticationFilter);
            http.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

            http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        }


        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }
}
