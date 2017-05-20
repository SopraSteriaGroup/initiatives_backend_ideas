package com.soprasteria.initiatives.ideas.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.soprasteria.initiatives.ideas.domain.IdeaProgress.NOT_STARTED
import com.soprasteria.initiatives.ideas.domain.IdeaProgress.STARTED
import com.soprasteria.initiatives.ideas.dto.IdeaDetailDTO
import com.soprasteria.initiatives.ideas.dto.MemberDTO
import com.soprasteria.initiatives.ideas.mapping.toDetailDTO
import com.soprasteria.initiatives.ideas.repository.IdeaRepository
import com.soprasteria.initiatives.ideas.utils.createIdea
import com.soprasteria.initiatives.ideas.utils.createMember
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.restdocs.restassured3.operation.preprocess.RestAssuredPreprocessors.modifyUris
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class IdeasAPITests {

    val defaultName = "setup"
    val baseUrl = "/api/ideas"
    lateinit var spec: RequestSpecification
    @Rule @JvmField val restDocumentation = JUnitRestDocumentation()
    @LocalServerPort var port: Int = 0
    @Autowired lateinit var ideaRepository: IdeaRepository

    @Before
    fun setUp() {
        ideaRepository.deleteAll().block()
        ideaRepository.insert(createIdea(defaultName)).block()
        ideaRepository.insert(createIdea("$defaultName 2")).block()
        RestAssured.port = port
        spec = RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation)).build()
    }

    @Test
    fun `should publish all ideas`() {
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("findAllIdeas", preprocessRequest(modifyUris().port(8080)), preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("[]").description("An array of ideas"))
                                .andWithPrefix("[].", ideaDTOFieldsWithId())))
                .`when`().get(baseUrl)
                .then().statusCode(OK.value()).apply(validateMultipleIdeas())
    }

    @Test
    fun `should create new idea`() {
        val name = "create"
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(createIdeaRequestBody(name))
                .filter(document("createIdea", preprocessRequest(modifyUris().port(8080), prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(minIdeaDTOFields()),
                        responseFields(ideaDTOFieldsWithId())))
                .`when`().post(baseUrl)
                .then().statusCode(CREATED.value()).apply(validateSimpleIdea(name))
    }

    @Test
    fun `should not create new idea because conflicting names`() {
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(createIdeaRequestBody(defaultName))
                .filter(document("conflictCreateIdea", preprocessRequest(modifyUris().port(8080), prettyPrint()),
                        preprocessResponse(prettyPrint()), responseFields(errorDTOFields())))
                .`when`().post(baseUrl)
                .then().statusCode(CONFLICT.value()).apply { validateError(CONFLICT) }
    }

    @Test
    fun `should not create new idea because name empty`() {
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(createIdeaRequestBody(""))
                .`when`().post(baseUrl)
                .then().statusCode(BAD_REQUEST.value()).apply { validateError(BAD_REQUEST) }
    }

    @Test
    fun `should update idea`() {
        val updatedName = "updated name"
        val idea = ideaRepository.findAll().blockFirst()
        val updatedContact = idea.contact.copy(website = "updated.site.com", github = "http://github.com/jntakpe")
        val members = mutableListOf(createMember("titi"), createMember("tata"))
        val updatedIdea = idea.copy(name = updatedName, likes = 10, progress = STARTED, contact = updatedContact, members = members)
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(updatedIdea.toDetailDTO())
                .filter(document("updateIdea", preprocessRequest(modifyUris().port(8080), prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(ideaDetailDTO()),
                        responseFields(ideaDetailDTO())))
                .`when`().put("$baseUrl/{id}", idea.id.toString())
                .then().statusCode(OK.value()).apply(validateUpdatedIdea(idea.name))
    }

    @Test
    fun `should not update idea because conflicting names`() {
        val ideas = ideaRepository.findAll().collectList().block()
        assertThat(ideas.size).isGreaterThanOrEqualTo(2)
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(ideas[0].toDetailDTO())
                .`when`().put("$baseUrl/{id}", ideas[1].id.toString())
                .then().statusCode(CONFLICT.value())
    }

    @Test
    fun `should not update idea because name is empty`() {
        val idea = ideaRepository.findAll().blockFirst()
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(idea.copy(name = "").toDetailDTO())
                .`when`().put("$baseUrl/{id}", idea.id.toString())
                .then().statusCode(BAD_REQUEST.value())
    }

    @Test
    fun `should join team`() {
        val idea = ideaRepository.findAll().blockFirst()
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .filter(document("joinIdea", preprocessRequest(modifyUris().port(8080), prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(ideaDetailDTO())))
                .`when`().post("$baseUrl/{id}/join", idea.id.toString())
                .then().statusCode(OK.value()).apply(validateDetailedIdea(idea.toDetailDTO()))
    }

    private fun validateSimpleIdea(name: String): ValidatableResponse.() -> Unit {
        return {
            body("id", notNullValue())
            body("name", equalTo(name))
            body("pitch", equalTo("$name pitch"))
            body("category", equalTo("cat"))
            body("logo", equalTo("$name logo"))
            body("progress", equalTo(NOT_STARTED.name))
            body("likes", equalTo(0))
        }
    }

    private fun validateUpdatedIdea(oldName: String): ValidatableResponse.() -> Unit {
        return {
            body("id", notNullValue())
            body("name", equalTo("updated name"))
            body("pitch", equalTo("$oldName pitch"))
            body("category", equalTo("cat"))
            body("logo", equalTo("$oldName logo"))
            body("progress", equalTo(STARTED.name))
            body("founder", notNullValue(MemberDTO::class.java))
            body("founder.username", equalTo("toto"))
            body("founder.email", equalTo("toto@mail.com"))
            body("founder.firstName", equalTo("toto firstName"))
            body("founder.lastName", equalTo("toto lastName"))
            body("founder.avatar", equalTo("toto avatar"))
            body("members.username", hasItems("titi", "tata"))
            body("likes", equalTo(10))
            body("contact.website", equalTo("updated.site.com"))
            body("contact.github", equalTo("http://github.com/jntakpe"))
            body("contact.slack", nullValue())
            body("contact.trello", nullValue())
        }
    }

    private fun validateDetailedIdea(idea: IdeaDetailDTO): ValidatableResponse.() -> Unit {
        return {
            body("id", notNullValue())
            body("name", equalTo(idea.name))
            body("pitch", equalTo(idea.pitch))
            body("category", equalTo(idea.category))
            body("logo", equalTo(idea.logo))
            body("progress", equalTo(idea.progress.name))
            body("founder", notNullValue(MemberDTO::class.java))
            body("founder.username", equalTo(idea.founder.username))
            body("founder.email", equalTo(idea.founder.email))
            body("founder.firstName", equalTo(idea.founder.firstName))
            body("founder.lastName", equalTo(idea.founder.lastName))
            body("founder.avatar", equalTo(idea.founder.avatar))
            body("likes", equalTo(idea.likes))
            body("contact.website", equalTo(idea.contact.website))
            body("contact.github", equalTo(idea.contact.github))
            body("contact.slack", equalTo(idea.contact.slack))
            body("contact.trello", equalTo(idea.contact.trello))
        }
    }

    private fun validateMultipleIdeas(): ValidatableResponse.() -> Unit {
        return {
            body("id", hasItems(notNullValue(), notNullValue()))
            body("name", hasItems(defaultName, "$defaultName 2"))
            body("pitch", hasItems("$defaultName pitch", "$defaultName 2 pitch"))
            body("category", hasItems("cat", "cat"))
            body("logo", hasItems("$defaultName logo", "$defaultName 2 logo"))
            body("progress", hasItems(NOT_STARTED.name, NOT_STARTED.name))
            body("likes", hasItems(0, 0))
        }
    }

    private fun validateError(status: HttpStatus): ValidatableResponse.() -> Unit {
        return {
            body("message", notNullValue())
            body("status", equalTo(status.value()))
            body("reason", equalTo(status.reasonPhrase))
        }
    }

    private fun minIdeaDTOFields() = mutableListOf(
            fieldWithPath("name").type(String::class.java).description("The idea's name"),
            fieldWithPath("pitch").type(String::class.java).description("The idea's pitch explaining what this idea is about"),
            fieldWithPath("category").type(String::class.java).description("The idea's category TODO ..."),
            fieldWithPath("logo").optional().type(String::class.java).description("The idea's logo URL")
    )

    private fun ideaDTOFields() = mutableListOf(
            fieldWithPath("progress").type(String::class.java).description("The idea's progress could be any value of type IdeaProgress"),
            fieldWithPath("likes").type(Int::class.java).description("The idea's numbers of likes")
    ).apply { addAll(minIdeaDTOFields()) }

    private fun ideaDTOFieldsWithId() = mutableListOf(
            fieldWithPath("id").type(String::class.java).description("The idea's technical id generated by MongoDB"))
            .apply { addAll(ideaDTOFields()) }

    private fun ideaDetailDTO(): MutableList<FieldDescriptor> {
        return ideaDTOFieldsWithId()
                .apply {
                    addAll(applyPathPrefix("founder.", founderDTOFields()))
                    addAll(applyPathPrefix("contact.", contactDTOFields()))
                    add(fieldWithPath("members").description("An array of team members"))
                    addAll(applyPathPrefix("members[].", membersDTOFields()))
                }
    }

    private fun founderDTOFields() = mutableListOf(
            fieldWithPath("username").type(String::class.java).description("The idea's founder username"),
            fieldWithPath("email").type(String::class.java).description("The idea's founder email"),
            fieldWithPath("firstName").type(String::class.java).description("The idea's founder first name"),
            fieldWithPath("lastName").type(String::class.java).description("The idea's founder last name"),
            fieldWithPath("avatar").type(String::class.java).optional().description("The idea's founder avatar")
    )

    private fun membersDTOFields() = mutableListOf(
            fieldWithPath("username").type(String::class.java).description("An idea's team member username"),
            fieldWithPath("email").type(String::class.java).description("An idea's team member email"),
            fieldWithPath("firstName").type(String::class.java).description("An idea's team member first name"),
            fieldWithPath("lastName").type(String::class.java).description("An idea's team member last name"),
            fieldWithPath("avatar").type(String::class.java).optional().description("An idea's team member avatar")
    )

    private fun contactDTOFields() = mutableListOf(
            fieldWithPath("website").type(String::class.java).description("The idea's website URL"),
            fieldWithPath("slack").type(String::class.java).description("The idea's slack URL"),
            fieldWithPath("github").type(String::class.java).description("The idea's github URL"),
            fieldWithPath("trello").type(String::class.java).description("The idea's trello URL")
    )

    private fun errorDTOFields() = mutableListOf(
            fieldWithPath("message").type(String::class.java).description("The error message"),
            fieldWithPath("status").type(Int::class.java).description("The HTTP status code"),
            fieldWithPath("reason").type(String::class.java).description("The HTTP status reason")
    )

    private fun createIdeaRequestBody(name: String) = ObjectMapper()
            .writeValueAsString(IdeaDTOTest(name, "$name pitch", "cat", "$name logo"))

    data class IdeaDTOTest(val name: String, val pitch: String, val category: String, val logo: String)

    data class ContactDTOTest(val mail: String)

}