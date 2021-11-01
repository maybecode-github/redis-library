package de.maybecode.redis.library;

import de.maybecode.redis.library.api.RedisAPI;
import de.maybecode.redis.library.channel.ChannelManager;
import de.maybecode.redis.library.pubsub.IRedisChannelAction;
import de.maybecode.redis.library.pubsub.publish.PublishService;
import de.maybecode.redis.library.pubsub.subscribe.RedisSubscription;
import de.maybecode.redis.library.scanner.AnnotationScanner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class RedisBuilder implements RedisAPI {

    private int redisPort;
    private String redisHost;
    private Jedis publisher, subscriber, storage;
    private ChannelManager channelManager;
    private JedisPool jedisPool = new JedisPool();

    private final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    private final AnnotationScanner annotationScanner = new AnnotationScanner();

    @Override
    public RedisAPI setHost(String host) {
        this.redisHost = host;
        return this;
    }

    @Override
    public RedisAPI setPort(int port) {
        this.redisPort = port;
        return this;
    }

    @Override
    public RedisAPI setMaxIdle(int maxIdle) {
        this.jedisPoolConfig.setMaxIdle(maxIdle);
        return this;
    }

    @Override
    public RedisAPI setMaxTotal(int maxTotal) {
        this.jedisPoolConfig.setMaxTotal(maxTotal);
        return this;
    }

    @Override
    public RedisAPI injectPool() {
        this.jedisPool = new JedisPool(this.jedisPoolConfig, this.redisHost, this.redisPort);
        return this;
    }

    @Override
    public RedisAPI authPublisher(String password) {
        if (this.publisher == null)
            this.publisher = this.jedisPool.getResource();
        this.publisher.auth(password);
        return this;
    }

    @Override
    public RedisAPI authSubscriber(String password) {
        if (this.subscriber == null)
            this.subscriber = this.jedisPool.getResource();
        this.subscriber.auth(password);
        return this;
    }

    @Override
    public RedisAPI authDatabase(String password) {
        if (this.storage == null)
            this.storage = this.jedisPool.getResource();
        this.storage.auth(password);
        return this;
    }

    @Override
    public RedisAPI create(Class<?> mainClass) {
        this.annotationScanner.findChannelActions(mainClass);
        this.channelManager = new ChannelManager();
        subscribe(AnnotationScanner.getChannelActionClasses());
        return this;
    }

    @Override
    public Jedis newJedisInstance(String password) {
        Jedis newJedis = this.jedisPool.getResource();
        newJedis.auth(password);
        return newJedis;
    }

    @Override
    public void publish(Class<? extends PublishService> clazz, String message) {
        try {
            Method method = clazz.getMethod("onPublish", String.class);
            RedisSubscription redisSubscription = method.getAnnotation(RedisSubscription.class);
            try {
                method.invoke(clazz.newInstance(), message);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
            this.publisher.publish(redisSubscription.channel(), message);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(String channel, String message) {
        this.publisher.publish(channel, message);
    }

    @Override
    public void setKey(String key, String value) {
        if (value == null) {
            this.storage.set(key, null);
            return;
        }
        this.storage.set(key, value);
    }

    @Override
    public String getFromKey(String key) {
        return this.storage.get(key);
    }

    @Override
    public boolean hasKey(String key) {
        return this.storage.get(key) != null;
    }

    private void subscribe(List<Class<? extends IRedisChannelAction>> classes) {
        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                classes.forEach(clazz -> {
                    try {
                        RedisSubscription redisSubscription = clazz.getMethod("onExecute", String.class).getAnnotation(RedisSubscription.class);
                        if (redisSubscription.channel().equals(channel)) {
                            try {
                                clazz.newInstance().onExecute(message);
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        new Thread(() -> this.subscriber.subscribe(jedisPubSub, this.channelManager.listChannels())).start();
    }

}
