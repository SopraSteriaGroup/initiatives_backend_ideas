package com.soprasteria.initiatives.ideas.dto

import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import org.hibernate.validator.constraints.NotBlank

data class IdeaDTO(var id: String?,
                   @NotBlank val name: String,
                   @NotBlank val pitch: String,
                   @NotBlank val category: String,
                   val logo: String,
                   val contact: IdeaContactDTO,
                   val progress: IdeaProgress = IdeaProgress.NOT_STARTED,
                   val likes: Int = 0)