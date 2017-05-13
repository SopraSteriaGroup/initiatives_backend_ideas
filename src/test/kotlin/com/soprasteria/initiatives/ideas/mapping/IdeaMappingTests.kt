package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.SimpleIdeaDTO
import org.assertj.core.api.Assertions.assertThat
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
        val idea = SimpleIdeaDTO(null, name, pitch, category, logo, contact).toIdea()
        assertThat(idea).isNotNull()
        assertThat(idea.id).isNotNull()
        assertThat(idea.name).isEqualTo(name)
        assertThat(idea.pitch).isEqualTo(pitch)
        assertThat(idea.category).isEqualTo(category)
        assertThat(idea.logo).isEqualTo(logo)
        assertThat(idea.contact).isNotNull()
        assertThat(idea.contact.mail).isEqualTo(mail)
    }

}