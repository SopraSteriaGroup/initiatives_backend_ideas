package com.soprasteria.initiatives.ideas.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import com.soprasteria.initiatives.ideas.repository.IdeaRepository
import com.soprasteria.initiatives.ideas.utils.createIdea
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.restdocs.restassured3.operation.preprocess.RestAssuredPreprocessors.modifyUris
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class IdeasAPITests {

    val defaultName = "setup"
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
                                .andWithPrefix("[].", ideaDTOFieldsWithId().apply {
                                    addAll(applyPathPrefix("contact.", contactDTOFields()))
                                })))
                .`when`().get("/api/ideas")
                .then().statusCode(HttpStatus.OK.value()).apply(validateMultipleIdeas())
    }

    @Test
    fun `should create new idea`() {
        val name = "create"
        given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(createIdeaRequestBody(name))
                .filter(document("createIdea", preprocessRequest(modifyUris().port(8080), prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(minIdeaDTOFields().apply {
                            addAll(applyPathPrefix("contact.", listOf(mailField())))
                        }),
                        responseFields(ideaDTOFieldsWithId().apply {
                            addAll(applyPathPrefix("contact.", contactDTOFields()))
                        })))
                .`when`().post("/api/ideas")
                .then().statusCode(HttpStatus.OK.value()).apply(validateSimpleIdea(name))
    }

    @Test
    fun `should not create new idea because conflicting keys`() {

    }

    private fun validateSimpleIdea(name: String): ValidatableResponse.() -> Unit {
        return {
            body("id", notNullValue())
            body("name", equalTo(name))
            body("pitch", equalTo("$name pitch"))
            body("category", equalTo("cat"))
            body("logo", equalTo("$name logo"))
            body("progress", equalTo(IdeaProgress.NOT_STARTED.name))
            body("likes", equalTo(0))
            body("contact.mail", equalTo("jntakpe@mail.com"))
            body("contact.slack", nullValue())
            body("contact.github", nullValue())
            body("contact.trello", nullValue())
            body("contact.website", nullValue())
        }
    }

    private fun validateMultipleIdeas(): ValidatableResponse.() -> Unit {
        return {
            body("id", hasItems(notNullValue(), notNullValue()))
            body("name", hasItems(defaultName, "$defaultName 2"))
            body("pitch", hasItems("$defaultName pitch", "$defaultName 2 pitch"))
            body("category", hasItems("cat", "cat"))
            body("logo", hasItems("$defaultName logo", "$defaultName 2 logo"))
            body("progress", hasItems(IdeaProgress.NOT_STARTED.name, IdeaProgress.NOT_STARTED.name))
            body("likes", hasItems(0, 0))
            body("contact.mail", hasItems("jntakpe@mail.com", "jntakpe@mail.com"))
            body("contact.slack", hasItems(nullValue(), nullValue()))
            body("contact.github", hasItems(nullValue(), nullValue()))
            body("contact.trello", hasItems(nullValue(), nullValue()))
            body("contact.website", hasItems(nullValue(), nullValue()))
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
            fieldWithPath("likes").type(Int::class.java).description("The idea's numbers of likes"),
            fieldWithPath("contact").description("The idea's contact")
    ).apply { addAll(minIdeaDTOFields()) }

    private fun ideaDTOFieldsWithId() = mutableListOf(
            fieldWithPath("id").type(String::class.java).description("The idea's technical id generated by MongoDB"))
            .apply { addAll(ideaDTOFields()) }

    private fun contactDTOFields() = mutableListOf(
            mailField(),
            fieldWithPath("website").type(String::class.java).description("The idea's website URL"),
            fieldWithPath("slack").type(String::class.java).description("The idea's slack URL"),
            fieldWithPath("github").type(String::class.java).description("The idea's github URL"),
            fieldWithPath("trello").type(String::class.java).description("The idea's trello URL")
    )

    private fun mailField() = fieldWithPath("mail").type(String::class.java).description("The contact's mail address")

    private fun createIdeaRequestBody(name: String) = ObjectMapper().writeValueAsString(
            IdeaDTOTest(name, "$name pitch", "cat", "$name logo", contact = ContactDTOTest("jntakpe@mail.com")))

    data class IdeaDTOTest(val name: String, val pitch: String, val category: String, val logo: String, val contact: ContactDTOTest)

    data class ContactDTOTest(val mail: String)

}