package com.acme.checkout.config;

import com.acme.checkout.domain.converters.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;

import java.util.*;

@Configuration
@EnableMongoRepositories(basePackages = "com.acme.checkout.domain")
@Data
public class MongoConfig extends AbstractMongoConfiguration {

    private List<ServerAddress> hostname = new ArrayList<>();
    private Integer port;
    private String username;
    private String password;
    private static Logger LOG = LoggerFactory.getLogger(MongoConfig.class);

    public MongoConfig() {
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("mongo.hostname", "localhost");
        defaultMap.put("mongo.port", 27017);
        defaultMap.put("mongo.username", "");
        defaultMap.put("mongo.password", "");
        Config defaultConf = ConfigFactory.parseMap(defaultMap);

        Config config = ConfigFactory.load().withFallback(defaultConf);
        setPort(config.getInt("mongo.port"));
        setUsername(Optional.ofNullable(config.getString("mongo.username")).isPresent()
                ? config.getString("mongo.username") : null);
        setPassword(Optional.ofNullable(config.getString("mongo.password")).isPresent()
                ? config.getString("mongo.password") : null);

        List<String> seedList = Optional.ofNullable(config.getString("mongo.hostname")).isPresent() ?
                Arrays.asList(config.getString("mongo.hostname").split(",")) : null;

        for (String seed : seedList) {
            try {
                hostname.add(new ServerAddress(seed, port));
            } catch (Exception e) {
                LOG.error("Error constructing mongo factory", e);
            }
        }

    }

    @Override
    public MongoClient mongoClient() {
        if (!StringUtils.isEmpty(getUsername()) && !StringUtils.isEmpty(getPassword())) {
            try {
                MongoCredential credential = MongoCredential.createCredential(getUsername(), getDatabaseName(), getPassword().toCharArray());
                MongoClientOptions options = MongoClientOptions.builder().build();
                return new MongoClient(hostname, credential, options);
            } catch (Exception e) {
                return new MongoClient(hostname);
            }
        } else {
            return new MongoClient(hostname);
        }
    }

    public static final List<Converter<?, ?>> converters = Arrays.asList(
            new Converter[]{
                    new LocalDateReadConverter(),
                    new LocalDateWriteConverter()
            }
    );

    @Override
    protected String getDatabaseName() {
        return "acme";
    }

    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(converters);
    }

}