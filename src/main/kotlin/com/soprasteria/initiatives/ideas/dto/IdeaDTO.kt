package com.soprasteria.initiatives.ideas.dto

import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import org.hibernate.validator.constraints.NotBlank
import javax.validation.Valid

data class IdeaDTO(var id: String?,
                   @field:NotBlank val name: String,
                   @field:NotBlank val pitch: String,
                   @field:NotBlank val category: String,
                   val logo: String,
                   @field:Valid val contact: IdeaContactDTO,
                   val progress: IdeaProgress = IdeaProgress.NOT_STARTED,
                   val likes: Int = 0)