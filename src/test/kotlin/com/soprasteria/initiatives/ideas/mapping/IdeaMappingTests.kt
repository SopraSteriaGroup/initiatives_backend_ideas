package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.Test

class IdeaMappingTests {

    @Test
    fun `should map dto to contact`() {
        val mail = "mail"
        val website = "website"
        val slack = "slack"
        val github = "github"
        val trello = "trello"
        val contact = IdeaContactDTO(mail, website, slack, github, trello).toContact()
        assertThat(contact).isNotNull()
        assertThat(contact.mail).isEqualTo(mail)
        assertThat(contact.website).isEqualTo(website)
        assertThat(contact.slack).isEqualTo(slack)
        assertThat(contact.github).isEqualTo(github)
        assertThat(contact.trello).isEqualTo(trello)
    }

    @Test
    fun `should map dto to new idea`() {
        val name = "name"
        val pitch = "pitch"
        val category = "category"
        val logo = "logo"
        val mail = "mail"
        val contact = IdeaContactDTO(mail, null, null, null, null)
        val idea = IdeaDTO(null, name, pitch, category, logo, contact).toIdea()
        assertThat(idea).isNotNull()
        assertThat(idea.id).isNotNull()
        assertThat(idea.name).isEqualTo(name)
        assertThat(idea.pitch).isEqualTo(pitch)
        assertThat(idea.category).isEqualTo(category)
        assertThat(idea.logo).isEqualTo(logo)
        assertThat(idea.contact).isNotNull()
        assertThat(idea.contact.mail).isEqualTo(mail)
        assertThat(idea.progress).isEqualTo(IdeaProgress.NOT_STARTED)
        assertThat(idea.likes).isEqualTo(0)
    }

    @Test
    fun `should map contact to dto`() {
        val mail = "mail"
        val website = "website"
        val slack = "slack"
        val github = "github"
        val trello = "trello"
        val dto = IdeaContact(mail, website, slack, github, trello).toDTO()
        assertThat(dto).isNotNull()
        assertThat(dto.mail).isEqualTo(mail)
        assertThat(dto.website).isEqualTo(website)
        assertThat(dto.slack).isEqualTo(slack)
        assertThat(dto.github).isEqualTo(github)
        assertThat(dto.trello).isEqualTo(trello)
    }

    @Test
    fun `should map idea to dto`() {
        val name = "name"
        val pitch = "pitch"
        val category = "category"
        val logo = "logo"
        val progress = IdeaProgress.STARTED
        val likes = 5
        val mail = "mail"
        val contact = IdeaContact(mail, null, null, null, null)
        val dto = Idea(ObjectId(), name, pitch, category, logo, progress, likes, contact).toDTO()
        assertThat(dto).isNotNull()
        assertThat(dto.id).isNotNull()
        assertThat(dto.name).isEqualTo(name)
        assertThat(dto.pitch).isEqualTo(pitch)
        assertThat(dto.category).isEqualTo(category)
        assertThat(dto.logo).isEqualTo(logo)
        assertThat(dto.progress).isEqualTo(progress)
        assertThat(dto.likes).isEqualTo(likes)
        assertThat(dto.contact).isNotNull()
        assertThat(dto.contact.mail).isEqualTo(mail)
    }

}