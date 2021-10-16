package de.maybecode.redis.library.pubsub.publish;

public interface PublishService {

    /**
     * on push message in channel
     *
     * @param transmitted published message
     */
    void onPublish(String transmitted);

}
