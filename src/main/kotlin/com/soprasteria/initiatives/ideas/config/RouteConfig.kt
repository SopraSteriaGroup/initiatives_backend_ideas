package com.soprasteria.initiatives.ideas.config

import com.soprasteria.initiatives.ideas.web.IdeasAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class RouteConfig {

    @Bean
    fun apiRouter(ideasAPI: IdeasAPI) = router {
        "/api".and(accept(MediaType.APPLICATION_JSON)).nest {
            "/ideas".nest {
                GET("/", ideasAPI::findAll)
            }
        }
    }

}