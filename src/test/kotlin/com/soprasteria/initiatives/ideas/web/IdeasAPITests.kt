package com.soprasteria.initiatives.ideas.web

import com.soprasteria.initiatives.ideas.repository.IdeaRepository
import com.soprasteria.initiatives.ideas.utils.createIdea
import io.restassured.RestAssured
import io.restassured.RestAssured.`when`
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IdeasAPITests {

    val defaultName = "setup"
    @LocalServerPort var port: Int = 0
    @Autowired lateinit var ideaRepository: IdeaRepository

    @Before
    fun setUp() {
        ideaRepository.deleteAll().block()
        ideaRepository.insert(createIdea(defaultName)).block()
        ideaRepository.insert(createIdea("$defaultName 2")).block()
        RestAssured.port = port
    }

    @Test
    fun `should publish all ideas`() {
        print(port)
        `when`()
                .get("/api/ideas")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", `is`(2))
                .body("id", hasItems(notNullValue(), notNullValue()))
                .body("name", hasItems(defaultName, "$defaultName 2"))
    }

}