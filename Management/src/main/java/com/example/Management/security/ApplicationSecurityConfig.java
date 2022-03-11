package com.example.Management.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter{
    
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENT.name())
                .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name())
                .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ApplicationUserRole.ADMINTRAINEE.name())//this line of code was added by me because the admintrainee is a static add should be accessed in a static way.
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService(){
         UserDetails princeUser = User.builder()
                .username("prince")
                .password(passwordEncoder.encode("okafor"))
                //.roles(ApplicationUserRole.STUDENT.name())//ROLE_STUDENT, take note
                .authorities(ApplicationUserRole.STUDENT.getGrantedAuthorities())
                .build();
        UserDetails handelUser = User.builder()
                .username("Handel")
                .password(passwordEncoder.encode("okafor1"))
                //.roles(ApplicationUserRole.ADMIN.name())//ROLE_ADMIN, take note
                .authorities(ApplicationUserRole.ADMIN.getGrantedAuthorities())
                .build();

        UserDetails tomUser = User.builder()
                .username("Tom")
                .password(passwordEncoder.encode("okafor1"))
                //.roles(ApplicationUserRole.ADMINTRAINEE.name())//ROLE_ADMINTRAINEE, take note
                .authorities(ApplicationUserRole.ADMINTRAINEE.getGrantedAuthorities())
                .build();             

        return new InMemoryUserDetailsManager(
            princeUser,
            handelUser,
            tomUser
        );
        
    }
}
