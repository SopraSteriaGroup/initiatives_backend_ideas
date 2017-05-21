package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.domain.Member
import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDetailDTO
import com.soprasteria.initiatives.ideas.dto.MemberDTO

fun IdeaContactDTO.toContact() = IdeaContact(this.website, this.slack, this.github, this.trello)

fun IdeaDTO.toIdea(founder: MemberDTO) = Idea(this.id.toId(), this.name, this.pitch, this.category, this.logo, this.progress,
        founder.toMember())

fun IdeaDetailDTO.toIdea() = Idea(this.id.toId(), this.name, this.pitch, this.category, this.logo, this.progress, founder.toMember(),
        this.members.map { it.toMember() }.toMutableList(), this.contact.toContact())

fun MemberDTO.toMember() = Member(this.username, this.email, this.firstName, this.lastName, this.avatar)

fun IdeaContact.toDTO() = IdeaContactDTO(this.website, this.slack, this.github, this.trello)

fun Idea.toDTO() = IdeaDTO(this.id.toString(), this.name, this.pitch, this.category, this.logo, this.progress, this.likes.size)

fun Idea.toDetailDTO() = IdeaDetailDTO(this.id.toString(), this.name, this.pitch, this.category, this.logo, this.progress, this.likes.size,
        founder.toDTO(), members.map(Member::toDTO).toMutableList(), contact.toDTO())

fun Member.toDTO() = MemberDTO(this.username, this.email, this.firstName, this.lastName, this.avatar)