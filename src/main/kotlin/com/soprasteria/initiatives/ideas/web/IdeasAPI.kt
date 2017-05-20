package com.soprasteria.initiatives.ideas.web

import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDetailDTO
import com.soprasteria.initiatives.ideas.dto.MemberDTO
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

    fun create(req: ServerRequest) = req.bodyToMono<IdeaDTO>()
            .flatMap { validator.toMono(it) }
            .map { it.toIdea(MemberDTO("default", "default@mail.com", "first name", "last name", "avatar")) }
            .flatMap { ideaService.create(it) }
            .map { it.toDTO() }
            .flatMap { created(req.uri().resolve("${req.path()}/${it.id}")).syncBody(it) }
            .onErrorResume { it.toResponse() }

    fun update(req: ServerRequest) = req.bodyToMono<IdeaDetailDTO>()
            .flatMap { validator.toMono(it) }
            .map { it.toIdea(MemberDTO("default", "default@mail.com", "first name", "last name", "avatar")) }
            .flatMap { ideaService.update(it, req.pathVariable("id").toId()) }
            .map { it.toDetailDTO() }
            .flatMap { ok().syncBody(it) }
            .onErrorResume { it.toResponse() }

}