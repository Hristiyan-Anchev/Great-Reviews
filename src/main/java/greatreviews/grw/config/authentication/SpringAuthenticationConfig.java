package greatreviews.grw.config.authentication;

import greatreviews.grw.services.interfaces.UserService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableWebSecurity
@Component
public class SpringAuthenticationConfig extends WebSecurityConfigurerAdapter {

    PasswordEncoder encoder;
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                    .csrf().disable()
//                    .cors().disable()
                .authorizeRequests()
                    .antMatchers("/users/login").anonymous()
                .antMatchers("/users/logout").permitAll()
                .antMatchers(
                        "/",
                        "/bootstrap/**",
                        "/images/**",
                        "/images/*",
                        "/categories/**",
                        "/home",
                        "/users/register",
                        "/users/login",
                        "users/claim",
                        "/blog",
                        "/companies/show",
                        "/reviews/company",
                        "/reviews/company/evaluate"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/users/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/users/login?error=true")
                .loginProcessingUrl("/users/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/users/logout")
                .logoutSuccessUrl("/users/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .httpBasic()
        ;
    }

//
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor(onConstructor = @__(@Autowired))
//    @FieldDefaults(level = AccessLevel.PRIVATE)
//    @Configuration
//    @Order(1)
//    public static class UserConfigureAdapter extends WebSecurityConfigurerAdapter{
//
//        PasswordEncoder encoder;
//        UserService userService;
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth
//                    .userDetailsService(userService)
//                    .passwordEncoder(encoder);
//        }
//
//
//        protected void configure(HttpSecurity http) throws Exception {
//            http
////                    .csrf().disable()
////                    .cors().disable()
//                    .authorizeRequests()
////                    .antMatchers("/users/login").anonymous()
//                    .antMatchers(
//                            "/",
//                            "/bootstrap/**",
//                            "/images/**",
//                            "/categories/**",
//                            "/home",
//                            "/users/register",
//                            "/users/login",
//                            "/blog",
//                            "/enterprise/home",
//                            "/enterprise/login",
//                            "/enterprise/register"
//
//                    )
//                    .permitAll()
//                    .anyRequest()
//                    .authenticated()
//                    .and()
//                    .formLogin()
//                    .loginPage("/users/login")
//                    .defaultSuccessUrl("/", true)
//                    .failureUrl("/users/login?error=true")
//                    .loginProcessingUrl("/users/login")
//                    .usernameParameter("email")
//                    .permitAll()
//                    .and().logout().permitAll()
//                    .and()
//                    .httpBasic()
//            ;
//        }
//
//    }



   }
