package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.config.AuthenticatedUser
import com.soprasteria.initiatives.ideas.domain.Member
import com.soprasteria.initiatives.ideas.dto.MemberDTO

fun AuthenticatedUser.toMember() = Member(username, username, firstName, lastName, "") //TODO real avatar URL

fun AuthenticatedUser.toMemberDTO() = MemberDTO(username, username, firstName, lastName, "") //TODO real avatar URL
