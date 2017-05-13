package com.soprasteria.initiatives.ideas

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration

@SpringBootApplication
@EnableAutoConfiguration(exclude = arrayOf(MongoAutoConfiguration::class, MongoDataAutoConfiguration::class))
class InitiativesIdeasApplication

fun main(args: Array<String>) {
    SpringApplication.run(InitiativesIdeasApplication::class.java, *args)
}
