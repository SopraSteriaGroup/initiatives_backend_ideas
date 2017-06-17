package com.soprasteria.initiatives.ideas.config

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter


class CustomUserAuthenticationConverter : DefaultUserAuthenticationConverter() {

    override fun extractAuthentication(map: Map<String, *>): Authentication {
        val authentication = super.extractAuthentication(map)
        val authorities = authentication.authorities
        val user = AuthenticatedUser(getUsername(map), getFistName(map), getLastName(map), authorities)
        return UsernamePasswordAuthenticationToken(user, authentication.credentials, authorities)
    }

    private fun getUsername(map: Map<String, *>): String {
        return map[UserAuthenticationConverter.USERNAME] as String
    }

    private fun getFistName(map: Map<String, *>): String {
        return map["firstName"] as String
    }

    private fun getLastName(map: Map<String, *>): String {
        return map["lastName"] as String
    }

}

