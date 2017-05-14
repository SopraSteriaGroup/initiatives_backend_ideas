package com.soprasteria.initiatives.ideas.dto

import org.hibernate.validator.constraints.Email

data class IdeaContactDTO(@field:Email val mail: String, val website: String?, val slack: String?, val github: String?, val trello: String?)