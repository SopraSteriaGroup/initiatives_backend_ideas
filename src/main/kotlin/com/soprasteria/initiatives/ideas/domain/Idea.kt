package com.soprasteria.initiatives.ideas.domain

import org.bson.types.ObjectId
import org.hibernate.validator.constraints.NotBlank
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Idea(val id: ObjectId,
                @field:NotBlank val name: String,
                @field:NotBlank val pitch: String,
                @field:NotBlank val category: String, //TODO to become DBRef or embedded object
                val logo: String,
                val progress: IdeaProgress,
                val founder: Member,
                val members: MutableList<Member> = mutableListOf(),
                val contact: IdeaContact = IdeaContact(null, null, null, null),
                val likes: MutableList<String> = mutableListOf()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Idea) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Idea(id=$id, name=$name, founder=$founder)"
    }
}