package com.soprasteria.initiatives.ideas.mapping

import com.soprasteria.initiatives.ideas.dto.ErrorDTO
import com.soprasteria.initiatives.ideas.exceptions.ConflictKeyException
import com.soprasteria.initiatives.ideas.exceptions.IdNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import javax.validation.ValidationException

fun Throwable.toResponse() = when (this) {
    is IdNotFoundException -> NOT_FOUND.toErrorDTO(this.message)
    is ConflictKeyException -> CONFLICT.toErrorDTO(this.message)
    is ValidationException -> BAD_REQUEST.toErrorDTO(this.message)
    else -> INTERNAL_SERVER_ERROR.toErrorDTO(this.message)
}

fun HttpStatus.toErrorDTO(errMsg: String?) = ResponseEntity.status(this).body(ErrorDTO(errMsg, this.value(), reasonPhrase))

