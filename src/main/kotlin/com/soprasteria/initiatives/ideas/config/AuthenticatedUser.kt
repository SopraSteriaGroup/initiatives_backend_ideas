package com.soprasteria.initiatives.ideas.config

import org.springframework.security.core.GrantedAuthority
import java.security.Principal
import java.util.*


class AuthenticatedUser(val username: String, val firstName: String, val lastName: String, val authorities: Collection<GrantedAuthority>) : Principal {


    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is AuthenticatedUser) {
            return false
        }
        return username == o.username
    }

    override fun hashCode(): Int {
        return Objects.hash(username)
    }

    /**
     * Returns the name of this principal.

     * @return the name of this principal.
     */
    override fun getName(): String {
        return username
    }

    override fun toString(): String {
        return "AuthenticatedUser(username='$username', firstName='$firstName', lastName='$lastName', authorities=$authorities)"
    }


}
