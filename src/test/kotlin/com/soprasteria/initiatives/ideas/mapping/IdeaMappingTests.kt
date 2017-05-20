package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import com.soprasteria.initiatives.ideas.domain.Member
import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.dto.MemberDTO
import com.soprasteria.initiatives.ideas.utils.createMember
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.Test

class IdeaMappingTests {

    @Test
    fun `should map dto to contact`() {
        val website = "website"
        val slack = "slack"
        val github = "github"
        val trello = "trello"
        val contact = IdeaContactDTO(website, slack, github, trello).toContact()
        assertThat(contact).isNotNull()
        assertThat(contact.website).isEqualTo(website)
        assertThat(contact.slack).isEqualTo(slack)
        assertThat(contact.github).isEqualTo(github)
        assertThat(contact.trello).isEqualTo(trello)
    }

    @Test
    fun `should map null dto to empty comtact`() {
        val contact: IdeaContactDTO? = null
        val empty = contact.toContact()
        assertThat(empty).isNotNull()
        assertThat(empty.github).isNull()
        assertThat(empty.slack).isNull()
        assertThat(empty.trello).isNull()
        assertThat(empty.website).isNull()
    }

    @Test
    fun `should map dto to new idea`() {
        val name = "name"
        val pitch = "pitch"
        val category = "category"
        val logo = "logo"
        val idea = IdeaDTO(null, name, pitch, category, logo).toIdea(createMember("default").toDTO())
        assertThat(idea).isNotNull()
        assertThat(idea.id).isNotNull()
        assertThat(idea.name).isEqualTo(name)
        assertThat(idea.pitch).isEqualTo(pitch)
        assertThat(idea.category).isEqualTo(category)
        assertThat(idea.logo).isEqualTo(logo)
        assertThat(idea.progress).isEqualTo(IdeaProgress.NOT_STARTED)
        assertThat(idea.likes).isEqualTo(0)
        assertThat(idea.founder).isNotNull()
        assertThat(idea.contact).isNull()
    }

    @Test
    fun `should map dto to founder`() {
        val username = "username"
        val email = "email"
        val firstName = "firstname"
        val lastName = "lastname"
        val avatar = "avatar"
        val member = MemberDTO(username, email, firstName, lastName, avatar)
        assertThat(member.username).isEqualTo(username)
        assertThat(member.email).isEqualTo(email)
        assertThat(member.firstName).isEqualTo(firstName)
        assertThat(member.lastName).isEqualTo(lastName)
        assertThat(member.avatar).isEqualTo(avatar)
    }

    @Test
    fun `should map contact to dto`() {
        val website = "website"
        val slack = "slack"
        val github = "github"
        val trello = "trello"
        val dto = IdeaContact(website, slack, github, trello).toDTO()
        assertThat(dto).isNotNull()
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
        val contact = IdeaContact(null, null, null, null)
        val dto = Idea(ObjectId(), name, pitch, category, logo, progress, likes, createMember("founder"), mutableListOf(), contact).toDTO()
        assertThat(dto).isNotNull()
        assertThat(dto.id).isNotNull()
        assertThat(dto.name).isEqualTo(name)
        assertThat(dto.pitch).isEqualTo(pitch)
        assertThat(dto.category).isEqualTo(category)
        assertThat(dto.logo).isEqualTo(logo)
        assertThat(dto.progress).isEqualTo(progress)
        assertThat(dto.likes).isEqualTo(likes)
    }

    @Test
    fun `should map member to dto`() {
        val username = "username"
        val email = "email"
        val firstName = "firstname"
        val lastName = "lastname"
        val avatar = "avatar"
        val dtç = Member(username, email, firstName, lastName, avatar)
        assertThat(dtç.username).isEqualTo(username)
        assertThat(dtç.email).isEqualTo(email)
        assertThat(dtç.firstName).isEqualTo(firstName)
        assertThat(dtç.lastName).isEqualTo(lastName)
        assertThat(dtç.avatar).isEqualTo(avatar)
    }


}