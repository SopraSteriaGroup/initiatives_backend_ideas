package com.soprasteria.initiatives.ideas.utils

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.domain.IdeaProgress.NOT_STARTED
import com.soprasteria.initiatives.ideas.dto.IdeaContactDTO
import com.soprasteria.initiatives.ideas.dto.IdeaDTO
import org.bson.types.ObjectId

fun createIdea(name: String) = Idea(ObjectId(), name, "$name pitch", "cat", "$name logo", NOT_STARTED, 0, createIdeaContact())

fun createIdeaContact(mail: String = "jntakpe@mail.com") = IdeaContact(mail, null, null, null, null)

fun createIdeaDTO(name: String) = IdeaDTO(null, name, "$name pitch", "cat", "$name logo", createIdeaContactDTO())

fun createIdeaContactDTO(mail: String = "jntakpe@mail.com") = IdeaContactDTO(mail, null, null, null, null)