package com.soprasteria.initiatives.ideas.dto

import org.hibernate.validator.constraints.Email

data class MemberDTO(val username: String, @field:Email val email: String, val firstName: String, val lastName: String, val avatar: String?)