package com.soprasteria.initiatives.ideas.domain

import org.hibernate.validator.constraints.Email

data class Member(val username: String, @field:Email val email: String, val firstName: String, val lastName: String, val avatar: String?)