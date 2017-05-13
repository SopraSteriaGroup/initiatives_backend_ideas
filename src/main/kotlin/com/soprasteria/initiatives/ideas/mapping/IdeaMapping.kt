package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDTO

fun IdeaContactDTO.toContact() = IdeaContact(this.mail, this.website, this.slack, this.github, this.trello)

fun IdeaDTO.toIdea() = Idea(this.id.toId(), this.name, this.pitch, this.category, this.logo, this.progress, this.likes,
        this.contact.toContact())

fun IdeaContact.toDTO() = IdeaContactDTO(this.mail, this.website, this.slack, this.github, this.trello)

fun Idea.toDTO() = IdeaDTO(this.id.toString(), this.name, this.pitch, this.category, this.logo, this.contact.toDTO(), this.progress,
        this.likes)
