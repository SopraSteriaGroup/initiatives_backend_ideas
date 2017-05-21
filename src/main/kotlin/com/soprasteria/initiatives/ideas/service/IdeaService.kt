package com.soprasteria.initiatives.ideas.service

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.Member
import com.soprasteria.initiatives.ideas.exceptions.ConflictKeyException
import com.soprasteria.initiatives.ideas.repository.IdeaRepository
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Service
class IdeaService(private val ideaRepository: IdeaRepository) {

    val logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun findAll(): Flux<Idea> {
        listOf("Toto", "titi").map { }
        return ideaRepository.findAll()
                .doOnNext { logger.debug("All ideas retrieved") }
    }

    fun create(idea: Idea): Mono<Idea> {
        logger.info("Creating idea {}")
        return verifyNameAvailable(idea.name)
                .flatMap { ideaRepository.insert(idea) }
                .doOnNext { logger.info("Idea {} successfully created", it.name) }
    }

    fun update(idea: Idea, id: ObjectId): Mono<Idea> {
        logger.info("Updating idea {}", idea)
        return verifyNameAvailable(idea.name, id)
                .flatMap { findLikes(id) }
                .map { idea.copy(likes = it) }
                .flatMap { ideaRepository.save(it) }
                .doOnNext { logger.info("Idea {} successfully updated", it) }
    }

    fun verifyNameAvailable(name: String, id: ObjectId? = null): Mono<Boolean> {
        logger.debug("Checking that name {} is available", name)
        return findByName(name)
                .filter { it.id != id }
                .flatMap { ConflictKeyException("Name $name is not available").toMono<Boolean>() }
                .defaultIfEmpty(true)
                .doOnNext { logger.debug("Name {} is available", name) }
    }

    fun join(id: ObjectId, member: Member): Mono<Idea> {
        logger.info("Adding member {} to idea with id {}", member, id)
        return ideaRepository.findById(id)
                .map { it.apply { members.add(member) } }
                .flatMap { update(it, it.id) }
                .doOnNext { logger.info("Member {} has joined team {}", member, it) }
    }

    fun like(id: ObjectId, username: String): Mono<Idea> {
        return findById(id)
                .doOnNext { logger.info("User {} liking idea {}", username, it) }
                .map { it.apply { likes.add(username) } }
                .flatMap { ideaRepository.save(it) }
                .doOnNext { logger.info("User {} like added to idea {}", username, it) }
    }

    private fun findLikes(id: ObjectId): Mono<MutableList<String>> {
        logger.debug("Searching members with id {}", id)
        return findById(id)
                .doOnNext { logger.debug("Found {} members liking idea {}", it.likes.size, it) }
                .map { it.likes }
    }

    private fun findById(id: ObjectId): Mono<Idea> {
        logger.debug("Searching idea with id {}", id)
        return ideaRepository.findById(id)
                .doOnNext { logger.debug("Idea {} retrieved with id {}", it, id) }
    }

    private fun findByName(name: String): Mono<Idea> {
        logger.debug("Searching idea with name {}", name)
        return ideaRepository.findByNameIgnoreCase(name)
                .doOnNext { logger.debug("Found {} matching name {}", it, name) }
    }

}