package org.example.outsourcing.common.config;

import java.util.List;

import org.example.outsourcing.common.filter.AccessJwtFilter;
import org.example.outsourcing.common.filter.ExceptionJwtFilter;
import org.example.outsourcing.common.filter.RefreshJwtFilter;
import org.example.outsourcing.common.filter.handler.OauthFailureHandler;
import org.example.outsourcing.common.filter.handler.OauthSuccessHandler;
import org.example.outsourcing.jwt.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

	private final RefreshJwtFilter refreshJwtFilter;
	private final AccessJwtFilter accessJwtFilter;
	private final ExceptionJwtFilter exceptionJwtFilter;
	private final OauthSuccessHandler successHandler;
	private final OauthFailureHandler failureHandler;

	public SecurityConfig(JwtService jwtService, OauthSuccessHandler successHandler, OauthFailureHandler failureHandler) {
		this.refreshJwtFilter = new RefreshJwtFilter(jwtService);
		this.accessJwtFilter = new AccessJwtFilter(jwtService);
		this.exceptionJwtFilter = new ExceptionJwtFilter();
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain oauth2Chain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/api/auth/oauth2/**")
			.authorizeHttpRequests(a -> a
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(ep ->
					ep.baseUri("/api/auth/oauth2/signin")
				)
				.redirectionEndpoint(re ->
					re.baseUri("/api/auth/oauth2/callback/google")
				)
				.userInfoEndpoint(ui -> ui
					.userAuthoritiesMapper(grantedAuthoritiesMapper()))
				.successHandler(successHandler)
				.failureHandler(failureHandler)
			);
		return http.build();
	}

	@Bean
	public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
		return authorities -> List.of(
			(new SimpleGrantedAuthority("ROLE_social"))
		);
	}

	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sm ->
				sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/auth/signin",
					"/api/users",
					"/swagger-ui/**",
					"/swagger-resources/**",
					"/v2/**",
					"/v3/**",
					"/webjars/**",
					"/api/auth/social/login"
				).permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(refreshJwtFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(accessJwtFilter, RefreshJwtFilter.class)
			.addFilterBefore(exceptionJwtFilter, AccessJwtFilter.class)
			.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
