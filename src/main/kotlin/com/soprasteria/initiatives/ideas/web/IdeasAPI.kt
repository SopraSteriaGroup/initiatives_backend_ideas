package com.soprasteria.initiatives.ideas.web

import com.soprasteria.initiatives.ideas.config.AuthenticatedUser
import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDetailDTO
import com.soprasteria.initiatives.ideas.mapping.*
import com.soprasteria.initiatives.ideas.service.IdeaService
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("api/ideas")
class IdeasAPI(private val ideaService: IdeaService) {

    @GetMapping
    fun findAll() = ResponseEntity.ok().body(ideaService.findAll().map { it.toDTO() })

    @PostMapping
    fun create(req: HttpServletRequest, @Valid @RequestBody ideaDTO: IdeaDTO) =
            Mono.just(ideaDTO.toIdea(connectedUser().toMemberDTO()))
                    .flatMap { ideaService.create(it) }
                    .map { it.toDTO() }
                    .map { ResponseEntity.created(URI.create(req.requestURI.toString()).resolve("${req.requestURI}/${it.id}")).body(it) }
                    .map { it as ResponseEntity<Any?> }
                    .onErrorResume { Mono.just(it.toResponse() as ResponseEntity<Any?>) }

    @PutMapping("/{id}")
    fun update(@PathVariable id: ObjectId, req: HttpServletRequest, @Valid @RequestBody ideaDetailDTO: IdeaDetailDTO) =
            Mono.just(ideaDetailDTO.toIdea())
                    .flatMap { ideaService.update(it, id) }
                    .map { it.toDetailDTO() }
                    .map { ResponseEntity.ok().body(it) }
                    .map { it as ResponseEntity<Any?> }
                    .onErrorResume { Mono.just(it.toResponse() as ResponseEntity<Any?>) }

    @PostMapping("/{id}/join")
    fun join(@PathVariable id: ObjectId, req: HttpServletRequest) = ideaService.join(id, connectedUser().toMember())
            .map { it.toDetailDTO() }
            .map { ResponseEntity.ok().body(it) }
            .map { it as ResponseEntity<Any?> }
            .onErrorResume { Mono.just(it.toResponse() as ResponseEntity<Any?>) }

    @PostMapping("/{id}/like")
    fun like(@PathVariable id: ObjectId, req: HttpServletRequest) = ideaService.like(id, connectedUser().username)
            .map { it.toDetailDTO() }
            .map { ResponseEntity.ok().body(it) }
            .map { it as ResponseEntity<Any?> }
            .onErrorResume { Mono.just(it.toResponse() as ResponseEntity<Any?>) }

    private fun connectedUser() = SecurityContextHolder.getContext().authentication.principal as AuthenticatedUser


}

