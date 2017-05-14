package com.soprasteria.initiatives.ideas.web

import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.mapping.toDTO
import com.soprasteria.initiatives.ideas.mapping.toIdea
import com.soprasteria.initiatives.ideas.mapping.toResponse
import com.soprasteria.initiatives.ideas.service.IdeaService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono

@Component
class IdeasAPI(private val ideaService: IdeaService) {

    fun findAll(req: ServerRequest) = ok().body(ideaService.findAll().map { it.toDTO() })

    fun create(req: ServerRequest) = ideaFromReq(req)
            .flatMap { ideaService.create(it) }
            .map { it.toDTO() }
            .flatMap { ok().syncBody(it) }
            .onErrorResume { it.toResponse() }

    private fun ideaFromReq(req: ServerRequest) = req.bodyToMono<IdeaDTO>().map { it.toIdea() }

}