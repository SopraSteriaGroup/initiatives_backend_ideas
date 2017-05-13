package com.soprasteria.initiatives.ideas.mapping

import org.bson.types.ObjectId

fun String?.toId() = this?.let { ObjectId(it) } ?: ObjectId()