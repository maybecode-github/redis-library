package de.maybecode.redis.library.channel;

import de.maybecode.redis.library.pubsub.IRedisChannelAction;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {

    private final static Map<String, Class<? extends IRedisChannelAction>> CHANNELS = new HashMap<>();

    public void addChannel(String channel, Class<? extends IRedisChannelAction> clazz) {
        CHANNELS.put(channel, clazz);
    }

    public String[] listChannels() {
        return CHANNELS.keySet().toArray(new String[CHANNELS.size()]);
    }

    public static Map<String, Class<? extends IRedisChannelAction>> getChannels() {
        return CHANNELS;
    }
}
