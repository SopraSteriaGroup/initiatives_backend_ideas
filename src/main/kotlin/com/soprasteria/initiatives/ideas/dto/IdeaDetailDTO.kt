package com.soprasteria.initiatives.ideas.dto

import com.soprasteria.initiatives.ideas.domain.IdeaProgress
import org.hibernate.validator.constraints.NotBlank

data class IdeaDetailDTO(var id: String,
                         @field:NotBlank val name: String,
                         @field:NotBlank val pitch: String,
                         @field:NotBlank val category: String,
                         val logo: String,
                         val progress: IdeaProgress,
                         val likes: Int,
                         val founder: MemberDTO,
                         val members: List<MemberDTO>,
                         val contact: IdeaContactDTO)
