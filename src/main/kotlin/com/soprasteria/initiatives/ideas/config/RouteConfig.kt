package com.soprasteria.initiatives.ideas.config

import com.soprasteria.initiatives.ideas.web.IdeasAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse.temporaryRedirect
import org.springframework.web.reactive.function.server.router
import java.net.URI

@Configuration
class RouteConfig {

    @Bean
    fun apiRouter(ideasAPI: IdeasAPI) = router {
        "/api".and(accept(MediaType.APPLICATION_JSON)).nest {
            "/ideas".nest {
                GET("/", ideasAPI::findAll)
                POST("/", ideasAPI::create)
                PUT("/{id}", ideasAPI::update)
                POST("/{id}/join", ideasAPI::join)
                POST("/{id}/like", ideasAPI::like)
            }
        }
    }

    @Bean
    fun staticRouter() = router {
        accept(MediaType.TEXT_HTML).nest { GET("/", { temporaryRedirect(URI.create("html5/index.html")).build() }) }
        resources("/**", ClassPathResource("static/"))
    }


}