package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.domain.IdeaProgress.NOT_STARTED
import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.SimpleIdeaDTO

fun IdeaContactDTO.toContact() = IdeaContact(this.mail, this.website, this.slack, this.github, this.trello)

fun SimpleIdeaDTO.toIdea() = Idea(this.id.toId(), this.name, this.pitch, this.category, this.logo, NOT_STARTED, 0, this.contact.toContact())

