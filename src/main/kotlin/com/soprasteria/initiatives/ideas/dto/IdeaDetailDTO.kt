package com.soprasteria.initiatives.ideas.dto

import com.soprasteria.initiatives.ideas.domain.IdeaProgress

data class IdeaDetailDTO(var id: String,
                         val name: String,
                         val pitch: String,
                         val category: String,
                         val logo: String,
                         val progress: IdeaProgress,
                         val likes: Int,
                         val founder: MemberDTO,
                         val members: List<MemberDTO>,
                         val contact: IdeaContactDTO)
