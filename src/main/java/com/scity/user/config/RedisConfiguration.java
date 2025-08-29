package com.scity.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfiguration {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.username}")
  private String username;

  @Value("${spring.data.redis.password}")
  private String password;

  @Value("${spring.data.redis.database}")
  private int database;

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
    configuration.setUsername(username);
    configuration.setPassword(password);
    configuration.setDatabase(database);
    return new LettuceConnectionFactory(configuration);
  }

  @Bean
  public RedisTemplate<String, Serializable> redisTemplate(
      LettuceConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Serializable> template = new RedisTemplate<>();
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    RedisCacheConfiguration redisCacheConfiguration = config
        .entryTtl(Duration.ofSeconds(30))
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));

    Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();
    cacheNamesConfigurationMap.put(
        "SECONDS60",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(60)));
    cacheNamesConfigurationMap.put(
        "SECONDS90",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(90)));
    cacheNamesConfigurationMap.put(
        "SECONDS120",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(120)));
    cacheNamesConfigurationMap.put(
        "HOURS1",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(3600)));
    cacheNamesConfigurationMap.put(
        "phoneOrEmail",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(190)));
    cacheNamesConfigurationMap.put(
        "usersignup",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(300)));
    cacheNamesConfigurationMap.put(
        "userinfo",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(300)));
    cacheNamesConfigurationMap.put(
        "passwordold",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(300)));
    cacheNamesConfigurationMap.put(
        "phoneOrEmailUpdateErrorCount",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(3600)));
    cacheNamesConfigurationMap.put(
        "UserLoginUpdateErrorCount",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(86400)));
    cacheNamesConfigurationMap.put(
        "BlockPhoneOrEmailWithExcessiveErrors",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(3600)));
    cacheNamesConfigurationMap.put(
        "BlockUserLoginWithExcessiveErrors",
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(86400)));
    RedisCacheManager redisCacheManager = RedisCacheManager.builder(factory)
        .cacheDefaults(redisCacheConfiguration)
        .withInitialCacheConfigurations(cacheNamesConfigurationMap)
        .build();
    return redisCacheManager;
  }

  @Bean
  RedisCacheConfiguration defaultRedisCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30));
  }
}
