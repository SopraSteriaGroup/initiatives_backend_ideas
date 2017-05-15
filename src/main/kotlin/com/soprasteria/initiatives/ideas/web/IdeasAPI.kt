package com.soprasteria.initiatives.ideas.web

import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.mapping.*
import com.soprasteria.initiatives.ideas.service.IdeaService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import javax.validation.Validator

@Component
class IdeasAPI(private val ideaService: IdeaService, private val validator: Validator) {

    fun findAll(req: ServerRequest) = ok().body(ideaService.findAll().map { it.toDTO() })

    fun create(req: ServerRequest) = validateIdea(req)
            .flatMap { ideaService.create(it) }
            .map { it.toDTO() }
            .flatMap { created(req.uri().resolve("/${it.id}")).syncBody(it) }
            .onErrorResume { it.toResponse() }

    fun update(req: ServerRequest) = validateIdea(req)
            .flatMap { ideaService.update(it, req.pathVariable("id").toId()) }
            .map { it.toDTO() }
            .flatMap { ok().syncBody(it) }
            .onErrorResume { it.toResponse() }

    private fun validateIdea(req: ServerRequest) = req.bodyToMono<IdeaDTO>()
            .flatMap { validator.toMono(it).checkpoint() }
            .map { it.toIdea() }

}