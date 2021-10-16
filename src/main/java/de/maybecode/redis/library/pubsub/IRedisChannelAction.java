package de.maybecode.redis.library.pubsub;

public interface IRedisChannelAction {

    /**
     * on execute
     *
     * @param transmitted transmitted object(s)
     */
    void onExecute(String transmitted);

}
