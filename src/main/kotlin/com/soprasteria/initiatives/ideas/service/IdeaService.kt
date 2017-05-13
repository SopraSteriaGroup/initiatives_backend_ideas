package com.soprasteria.initiatives.ideas.service

import com.soprasteria.initiatives.ideas.domain.Idea
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
        logger.debug("Searching all ideas")
        return ideaRepository.findAll()
                .doOnNext { logger.debug("All ideas retrieved") }
    }

    fun create(idea: Idea): Mono<Idea> {
        logger.info("Creating idea {}")
        return verifyNameAvailable(idea.name)
                .flatMap { ideaRepository.insert(idea) }
                .doOnNext { logger.info("Idea {} successfully created", it.name) }
    }

    fun verifyNameAvailable(name: String, id: ObjectId? = null): Mono<Boolean> {
        logger.debug("Checking that name {} is available", name)
        return findByName(name)
                .filter { it.id != id }
                .flatMap { ConflictKeyException("Name $name is not available").toMono<Boolean>() }
                .defaultIfEmpty(true)
                .doOnNext { logger.debug("Name {} is available", name) }
    }

    private fun findByName(name: String): Mono<Idea> {
        logger.debug("Searching idea with name {}", name)
        return ideaRepository.findByNameIgnoreCase(name)
                .doOnNext { logger.debug("Found {} matching name {}", it, name) }
    }

}