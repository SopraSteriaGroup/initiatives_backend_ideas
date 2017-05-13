package com.soprasteria.initiatives.ideas.repository

import com.soprasteria.initiatives.ideas.domain.Idea
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface IdeaRepository : ReactiveMongoRepository<Idea, ObjectId> {

    fun findByNameIgnoreCase(name: String): Mono<Idea>

}