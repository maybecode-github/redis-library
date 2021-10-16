package de.maybecode.redis.library.pubsub.publish;

import de.maybecode.redis.library.pubsub.RedisChannelAction;

@RedisChannelAction
public class DefaultPublish implements PublishService {

    @Override
    public void onPublish(String transmitted) {
        System.out.println("transmitted default publish: " + transmitted);
    }
}
