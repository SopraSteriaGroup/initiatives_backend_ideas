package com.soprasteria.initiatives.ideas.config

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.async.client.MongoClientSettings
import com.mongodb.connection.ClusterSettings
import com.mongodb.connection.SslSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories
@EnableConfigurationProperties(MongoProperties::class)
class MongoDBConfig(val mongoProperties: MongoProperties, val profileConfig: ProfileConfig) : AbstractReactiveMongoConfiguration() {

    override fun mongoClient() = if (profileConfig.isActive(Profiles.CLOUD)) MongoClients.create(settings()) else MongoClients.create()

    override fun getDatabaseName() = mongoProperties.database

    private fun settings() = MongoClientSettings.builder()
            .sslSettings(SslSettings.builder()
                    .enabled(true)
                    .build())
            .clusterSettings(clusterSettings())
            .credentialList(listOf(mongoCredentials()))
            .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
            .build()

    private fun clusterSettings() = ClusterSettings.builder()
            .hosts(listOf(ServerAddress(mongoProperties.host, mongoProperties.port)))
            .build()

    private fun mongoCredentials() = MongoCredential.createMongoCRCredential(
            mongoProperties.username,
            mongoProperties.database,
            mongoProperties.password
    )

}