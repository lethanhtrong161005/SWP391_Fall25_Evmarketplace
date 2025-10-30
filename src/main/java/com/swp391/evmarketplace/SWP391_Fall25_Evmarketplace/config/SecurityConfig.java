package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security.JwtAuthenticationFilter;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security.handlers.CustomSecurityHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private CustomSecurityHandlers customSecurityHandlers;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    //Public Access

                    auth.requestMatchers(
                            "/api/auth/login-with-phone-number",
                            "/api/auth/google",
                            "/api/auth/google/callback",
                            "/api/accounts/request-otp",
                            "/api/accounts/verify-otp",
                            "/api/accounts/register",
                            "/api/accounts/reset-password",
                            "/api/listing/all",
                            "/api/listing/**",
                            "/api/accounts/avatar",
                            "/ws/**"
                    ).permitAll();
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    auth.requestMatchers(HttpMethod.GET, "/api/files/contract/**").authenticated();

                    auth.requestMatchers(HttpMethod.GET,
                            "/api/files/**",
                            "/api/category/**",
                            "/api/brand/**",
                            "/api/model/**",
                            "/api/product/vehicle/**",
                            "/api/product/battery/**",
                            "/api/listing/**"
                    ).permitAll();
                    auth.requestMatchers(
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html"

                    ).permitAll();

                    //auth
                    auth.requestMatchers("/api/accounts/change-password",
                            "/api/accounts/update-profile",
                            "/api/accounts/update-avatar",
                            "/api/accounts/current",
                            "/api/accounts/listing",
                            "/api/config/boosted",
                            "/api/inspection_schedule/**",
                            "/api/consignments_request/**",
                            "/api/branchs/**",
                            "/api/inspections/request/{requestId}"
                    ).authenticated();


                    //staff
                    auth.requestMatchers("/api/staff/**").hasRole(AccountRole.STAFF.name())
                            .requestMatchers("/api/inspections/**").hasRole(AccountRole.STAFF.name());
//                    auth.requestMatchers(HttpMethod.POST, "/api/inspections/add").hasRole(AccountRole.STAFF.name());
//                    auth.requestMatchers(HttpMethod.GET, "/api/inspections/**").hasRole(AccountRole.STAFF.name());
//                    auth.requestMatchers(HttpMethod.PUT, "/api/inspections/{inspectionId}/inactive").hasRole(AccountRole.STAFF.name());


                    //moderator
                    auth.requestMatchers("/api/moderator/listing/**").hasAnyRole("MODERATOR", "ADMIN", "MANAGER");

                    //manager
//                    auth.requestMatchers(
//                            "/api/shifts/templates/**",
//                            "api/manager/**"
//                            ).hasRole(AccountRole.MANAGER.name());

                    auth.requestMatchers(HttpMethod.GET,
//                            "/api/shifts/templates/**",
                                    "/api/manager/consignment-request",
                                    "/api/manager/branches/{branchId}/accounts/staff",
                                    "/api/manager/consignment-request",
                                    "/api/manager/branches/{branchId}/consignment-request/ignore-submitted",
                                    "/api/manager/branches/{branchId}/consignment-requests/assign")
                            .hasRole("MANAGER");
                    auth.requestMatchers(HttpMethod.PUT, "/api/manager/consignment-requests/{requestId}/assign/{staffId}")
                            .hasRole(AccountRole.MANAGER.name());

                    //admin
                    auth.requestMatchers("/api/admin/accounts/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.POST,
                                    "/api/category/add",
                                    "/api/brand/add",
                                    "/api/model/add",
                                    "/api/product/vehicle/add",
                                    "/api/product/battery/add")
                            .hasAnyRole("ADMIN", "STAFF");

                    auth.requestMatchers(HttpMethod.DELETE,
                                    "/api/category/delete/**",
                                    "/api/brand/delete/**",
                                    "/api/model/delete/**")
                            .hasAnyRole("ADMIN", "STAFF");

                    auth.requestMatchers(HttpMethod.PUT,
                                    "/api/category/update/**",
                                    "/api/brand/update/**",
                                    "/api/model/update/**",
                                    "/api/product/vehicle/update/**",
                                    "/api/product/battery/update/**")
                            .hasAnyRole("ADMIN", "STAFF");

                    auth.requestMatchers("api/admin/**").hasRole("ADMIN");

                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customSecurityHandlers)
                        .accessDeniedHandler(customSecurityHandlers)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
