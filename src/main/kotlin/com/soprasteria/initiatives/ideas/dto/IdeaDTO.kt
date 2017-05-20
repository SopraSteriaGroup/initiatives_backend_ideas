package com.soprasteria.initiatives.ideas.dto

import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import org.hibernate.validator.constraints.NotBlank

data class IdeaDTO(var id: String?,
                   @field:NotBlank val name: String,
                   @field:NotBlank val pitch: String,
                   @field:NotBlank val category: String,
                   val logo: String,
                   val progress: IdeaProgress = IdeaProgress.NOT_STARTED,
                   val likes: Int = 0)