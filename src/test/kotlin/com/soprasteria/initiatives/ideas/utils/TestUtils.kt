package com.soprasteria.initiatives.ideas.utils

import com.soprasteria.initiatives.ideas.domain.Idea
import com.soprasteria.initiatives.ideas.domain.IdeaProgress.NOT_STARTED
import com.soprasteria.initiatives.ideas.domain.Member
import org.bson.types.ObjectId

fun createIdea(name: String) = Idea(ObjectId(), name, "$name pitch", "cat", "$name logo", NOT_STARTED, createMember("toto"))

fun createMember(name: String) = Member(name, "$name@mail.com", "$name firstName", "$name lastName", "$name avatar")