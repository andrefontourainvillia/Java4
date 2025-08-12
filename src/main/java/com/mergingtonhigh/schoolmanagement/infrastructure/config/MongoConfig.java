package com.mergingtonhigh.schoolmanagement.infrastructure.config;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder
                .applyToConnectionPoolSettings(poolBuilder -> poolBuilder
                        .maxSize(50) 
                        .minSize(5) 
                        .maxWaitTime(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                        .maxConnectionIdleTime(60000, java.util.concurrent.TimeUnit.MILLISECONDS))

                .readPreference(ReadPreference.nearest())

                .writeConcern(WriteConcern.ACKNOWLEDGED)

                .applyToSocketSettings(socketBuilder -> socketBuilder
                        .connectTimeout(3000, java.util.concurrent.TimeUnit.MILLISECONDS)
                        .readTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS));
    }
}
