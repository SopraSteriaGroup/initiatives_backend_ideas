package com.soprasteria.initiatives.ideas.dto

import org.hibernate.validator.constraints.NotBlank

data class SimpleIdeaDTO(var id: String?,
                         @NotBlank val name: String,
                         @NotBlank val pitch: String,
                         @NotBlank val category: String,
                         val logo: String,
                         val contact: IdeaContactDTO)