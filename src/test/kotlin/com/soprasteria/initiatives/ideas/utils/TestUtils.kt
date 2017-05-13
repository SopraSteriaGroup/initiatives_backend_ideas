package com.soprasteria.initiatives.ideas.utils

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaContact
import com.soprasteria.initiatives.ideas.domain.IdeaProgress.NOT_STARTED
import org.bson.types.ObjectId

fun createIdea(name: String) = Idea(ObjectId(), name, "$name pitch", "cat", "$name logo", NOT_STARTED, 0, createIdeaContact(""))

fun createIdeaContact(mail: String?) = IdeaContact(mail ?: "jntakpe@mail.com", null, null, null, null)