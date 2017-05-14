package com.soprasteria.initiatives.ideas.domain

import org.hibernate.validator.constraints.Email

data class IdeaContact(@field:Email val mail: String, val website: String?, val slack: String?, val github: String?, val trello: String?)