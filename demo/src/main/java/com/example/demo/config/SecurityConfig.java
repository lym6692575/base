package com.example.demo.config;

import com.example.demo.filters.JwtAuthenticationFilter;
import com.example.demo.utils.AESUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    @Bean
    public AESUtils encryptionUtils() {
        return new AESUtils();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/manager/file/**").permitAll()
                .antMatchers("/manager/flow/downloadFlowGeFile/**").permitAll()
                .antMatchers("/knowledge/case/downloadfile/**").permitAll()
                .antMatchers("/knowledge/downloadfile-zip/**").permitAll()
                .antMatchers("/materialSubmittal/submit/downloadSbtzFjfile/**").permitAll()
                .antMatchers("/materialSubmittal/downloadSbtzFjfile/**").permitAll()
                .antMatchers("/common/downloadSbtzFjfile/**").permitAll()
                .antMatchers("/common/export/download/**").permitAll()
                .antMatchers("/training/downloadfile/**").permitAll()
                .antMatchers("/api/secure/**").authenticated() // 其它安全接口需认证
                .anyRequest().authenticated()                // 其余接口也需认证
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .cors();
        http.headers().frameOptions().sameOrigin();
    }
}
