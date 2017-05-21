package com.soprasteria.initiatives.ideas.service

import com.soprasteria.initiatives.ideas.exceptions.ConflictKeyException
import com.soprasteria.initiatives.ideas.repository.IdeaRepository
import com.soprasteria.initiatives.ideas.utils.createIdea
import com.soprasteria.initiatives.ideas.utils.createMember
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.test
import javax.validation.ValidationException

@SpringBootTest
@RunWith(SpringRunner::class)
class IdeaServiceTest {

    val defaultName = "setup"
    @Autowired lateinit var ideaRepository: IdeaRepository
    @Autowired lateinit var ideaService: IdeaService

    @Before
    fun setUp() {
        ideaRepository.deleteAll().block()
        ideaRepository.insert(createIdea(defaultName)).block()
        ideaRepository.insert(createIdea("$defaultName 2")).block()
    }

    @Test
    fun `should find all ideas`() {
        ideaService.findAll().test()
                .expectNextCount(2)
                .verifyComplete()
    }

    @Test
    fun `should create new idea`() {
        val initCount = ideaRepository.count().block()
        ideaService.create(createIdea("first idea")).test()
                .expectSubscription()
                .consumeNextWith { assertThat(it).isNotNull() }
                .then { assertThat(ideaRepository.count().block()).isEqualTo(initCount + 1) }
                .verifyComplete()
    }

    @Test
    fun `should not create idea with duplicate names`() {
        ideaService.create(createIdea(defaultName)).test()
                .expectSubscription()
                .verifyError(ConflictKeyException::class.java)
    }

    @Test
    fun `should not create idea cuz invalid bean`() {
        ideaService.create(createIdea("")).test()
                .expectSubscription()
                .verifyError(ValidationException::class.java)
    }

    @Test
    fun `should update idea`() {
        val initCount = ideaRepository.count().block()
        val first = ideaRepository.findAll().blockFirst()
        val updatedName = "updated name"
        ideaService.update(first.copy(name = updatedName), first.id).test()
                .expectSubscription()
                .consumeNextWith {
                    assertThat(it).isNotNull()
                    assertThat(it.name).isEqualTo(updatedName)
                }
                .then { assertThat(ideaRepository.count().block()).isEqualTo(initCount) }
                .verifyComplete()
    }

    @Test
    fun `should update preserving likes`() {
        val oldIdea = ideaRepository.findAll().take(1)
                .map { it.copy(likes = mutableListOf("toto", "tata", "titi")) }
                .flatMap { ideaRepository.save(it) }
                .blockFirst()
        val updatedName = "updated name"
        ideaService.update(oldIdea.copy(name = updatedName), oldIdea.id).test()
                .expectSubscription()
                .consumeNextWith {
                    assertThat(it.name).isEqualTo(updatedName)
                    assertThat(it.likes).hasSize(3).contains("toto", "titi", "tata")
                }
                .verifyComplete()
    }

    @Test
    fun `should update idea when duplicate name but same id`() {
        val first = ideaRepository.findAll().blockFirst()
        val updatedPitch = "updated pitch"
        ideaService.update(first.copy(pitch = updatedPitch), first.id).test()
                .expectSubscription()
                .consumeNextWith { assertThat(it.pitch).isEqualTo(updatedPitch) }
                .verifyComplete()
    }

    @Test
    fun `should update idea when duplicate name when id differs`() {
        val ideas = ideaRepository.findAll().collectList().block()
        assertThat(ideas.size).isGreaterThanOrEqualTo(2)
        ideaService.update(ideas[0].copy(name = ideas[1].name), ideas[0].id).test()
                .expectSubscription()
                .verifyError(ConflictKeyException::class.java)
    }

    @Test
    fun `should accept name because no duplicate found`() {
        ideaService.verifyNameAvailable("no duplicate").test()
                .expectSubscription()
                .expectNext(true)
                .verifyComplete()
    }

    @Test
    fun `should not accept name duplicate found`() {
        ideaService.verifyNameAvailable(defaultName).test()
                .expectSubscription()
                .verifyError(ConflictKeyException::class.java)
    }

    @Test
    fun `should accept name with id because no duplicate found`() {
        ideaService.verifyNameAvailable("no duplicate", ObjectId()).test()
                .expectSubscription()
                .expectNext(true)
                .verifyComplete()
    }

    @Test
    fun `should accept name with id because same ids`() {
        val idea = ideaRepository.findAll().blockFirst()
        ideaService.verifyNameAvailable(idea.name, idea.id).test()
                .expectSubscription()
                .expectNext(true)
                .verifyComplete()
    }

    @Test
    fun `should not accept name with id because ids differs`() {
        val idea = ideaRepository.findAll().blockFirst()
        ideaService.verifyNameAvailable(idea.name, ObjectId()).test()
                .expectSubscription()
                .verifyError(ConflictKeyException::class.java)
    }

    @Test
    fun `should add member to empty team`() {
        val idea = ideaRepository.findAll().blockFirst()
        assertThat(idea.members).isEmpty()
        val username = "titi"
        ideaService.join(idea.id, createMember(username)).test()
                .expectSubscription()
                .consumeNextWith {
                    assertThat(it).isNotNull()
                    assertThat(it.members).isNotEmpty
                    assertThat(it.members[0].username).isEqualTo(username)
                }
                .verifyComplete()
    }

    @Test
    fun `should add member to existing team`() {
        val ideaWithMembers = createIdea("with members").apply { members.addAll(listOf(createMember("tata"), createMember("titi"))) }
        val ideaId = ideaRepository.insert(ideaWithMembers).block().id
        val username = "new member"
        ideaService.join(ideaId, createMember(username)).test()
                .expectSubscription()
                .consumeNextWith {
                    assertThat(it).isNotNull()
                    assertThat(it.members).isNotEmpty
                    assertThat(it.members).hasSize(3)
                    assertThat(it.members.map { it.username }).contains("new member")
                }
                .verifyComplete()
    }

    @Test
    fun `should add like to idea`() {
        val idea = ideaRepository.findAll().blockFirst()
        val likerUsername = "cool guy"
        ideaService.like(idea.id, likerUsername).test()
                .expectSubscription()
                .consumeNextWith {
                    assertThat(it.likes).contains(likerUsername)
                    assertThat(it.likes.size).isEqualTo(idea.likes.size + 1)
                }
                .verifyComplete()
    }

}