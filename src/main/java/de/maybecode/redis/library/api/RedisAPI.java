package de.maybecode.redis.library.api;

import de.maybecode.redis.library.pubsub.publish.PublishService;
import redis.clients.jedis.Jedis;

public interface RedisAPI {

    RedisAPI setHost(String host);

    RedisAPI setPort(int port);

    /**
     * Set the value for the maxIdle configuration attribute for pools created with this configuration instance.
     *
     * @param maxIdle max idle amount
     * @return redis api
     */
    RedisAPI setMaxIdle(int maxIdle);

    /**
     * Set the value for the maxTotal configuration attribute for pools created with this configuration instance
     *
     * @param maxTotal max total amount
     * @return redis api
     */
    RedisAPI setMaxTotal(int maxTotal);

    /**
     * injects jedis pool, has to be executed before authentication
     *
     * @return redis api
     */
    RedisAPI injectPool();

    /**
     * set password for publisher instance
     *
     * @param password publisher password
     * @return redis api
     */
    RedisAPI authPublisher(String password);

    /**
     * set subscriber password
     *
     * @param password subscriber password
     * @return redis api
     */
    RedisAPI authSubscriber(String password);

    /**
     * set database password
     *
     * @param password database password
     * @return redis api
     */
    RedisAPI authDatabase(String password);

    RedisAPI create(Class<?> mainClass);

    Jedis newJedisInstance(String password);

    void publish(Class<? extends PublishService> clazz, String message);

    void publish(String channel, String message);

    void setKey(String key, String value);

    String getFromKey(String key);

    boolean hasKey(String key);

}
