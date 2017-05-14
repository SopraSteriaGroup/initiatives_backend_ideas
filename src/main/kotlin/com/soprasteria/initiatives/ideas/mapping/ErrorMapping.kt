package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.dto.ErrorDTO
import com.soprasteria.initiatives.ideas.exceptions.ConflictKeyException
import com.soprasteria.initiatives.ideas.exceptions.IdNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono

fun Throwable.toResponse(): Mono<ServerResponse> = when (this) {
    is IdNotFoundException -> NOT_FOUND.toErrorDTO(this.message)
    is ConflictKeyException -> CONFLICT.toErrorDTO(this.message)
    else -> INTERNAL_SERVER_ERROR.toErrorDTO(this.message)
}

fun HttpStatus.toErrorDTO(errMsg: String?) = status(this).syncBody(ErrorDTO(errMsg, this.value(), this.reasonPhrase))