package com.soprasteria.initiatives.ideas.web

import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.mapping.toDTO
import com.soprasteria.initiatives.ideas.mapping.toIdea
import com.soprasteria.initiatives.ideas.service.IdeaService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono

@Component
class IdeasAPI(private val ideaService: IdeaService) {

    fun findAll(req: ServerRequest) = ok().body(ideaService.findAll().map { it.toDTO() })

    fun create(req: ServerRequest) = ideaFromReq(req).flatMap { ok().body(ideaService.create(it).map { it.toDTO() }) }

    private fun ideaFromReq(req: ServerRequest) = req.bodyToMono<IdeaDTO>().map { it.toIdea() }

}