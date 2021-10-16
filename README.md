# redis-library
# this is the example of the usage of my redis-library

> **Redis-API-Example:**

```
RedisAPI redisAPI = new RedisBuilder().setHost("localhost").setPort(6379).setMaxIdle(2000).setMaxTotal(2000).injectPool().authPublisher("publisherPassword").authSubscriber("subscriberPassword").create(Main.class);
```

> **Redis-Push-Example**

``` 
redisAPI.publish(TestAction.class, "test message lol");
```

> **Action-Class-Example**

```
package de.maybecode.redis.test.action;

import de.maybecode.redis.library.pubsub.IRedisChannelAction;
import de.maybecode.redis.library.pubsub.RedisChannelAction;
import de.maybecode.redis.library.pubsub.publish.PublishService;
import de.maybecode.redis.library.pubsub.subscribe.RedisSubscription;

@RedisChannelAction
public class TestAction implements IRedisChannelAction, PublishService {

    @Override
    @RedisSubscription(channel = "testChannel")
    public void onExecute(String transmitted) {
        System.out.println("EXECUTE " + transmitted);
    }
    
    @Override
    @RedisSubscription(channel = "testChannel")
    public void onPublish(String transmitted) {
        System.out.println("PUBLISHING " + transmitted);
    }
}
```
