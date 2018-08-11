package com.acme.checkout.test.config;

import com.acme.checkout.config.MongoConfig;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.fakemongo.Fongo;

@Configuration
public class MongoTestConfig extends MongoConfig {

    public static final String DATABASE_TEST_NAME = "acme-test";

	@Override
    protected String getDatabaseName() {
        return DATABASE_TEST_NAME;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        return new Fongo(DATABASE_TEST_NAME).getMongo();
    }

}
