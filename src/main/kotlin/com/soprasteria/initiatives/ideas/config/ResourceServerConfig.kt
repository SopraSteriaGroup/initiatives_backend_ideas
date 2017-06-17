package com.soprasteria.initiatives.ideas.config

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore


@Configuration
@EnableResourceServer
@EnableWebSecurity
internal class ResourceServerConfig(private val resourceServerProperties: ResourceServerProperties) : ResourceServerConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable().headers().frameOptions().disable().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/html5/**").permitAll()
    }

    @Bean
    fun jwtAccessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setVerifierKey(resourceServerProperties.jwt.keyValue)
        val defaultAccessTokenConverter = DefaultAccessTokenConverter()
        defaultAccessTokenConverter.setUserTokenConverter(CustomUserAuthenticationConverter())
        converter.accessTokenConverter = defaultAccessTokenConverter
        return converter
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(jwtAccessTokenConverter())
    }

    @Bean
    fun jwtTokenServices(): DefaultTokenServices {
        val services = DefaultTokenServices()
        services.setTokenStore(tokenStore())
        return services
    }
}
