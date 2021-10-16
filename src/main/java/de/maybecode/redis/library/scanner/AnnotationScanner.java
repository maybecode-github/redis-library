package de.maybecode.redis.library.scanner;

import de.maybecode.redis.library.channel.ChannelManager;
import de.maybecode.redis.library.pubsub.IRedisChannelAction;
import de.maybecode.redis.library.pubsub.RedisChannelAction;
import de.maybecode.redis.library.pubsub.publish.PublishService;
import de.maybecode.redis.library.pubsub.subscribe.RedisSubscription;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.ArrayList;
import java.util.List;

public class AnnotationScanner {

    private final ChannelManager channelManager = new ChannelManager();

    private final static List<Class<? extends IRedisChannelAction>> CHANNEL_ACTION_CLASSES = new ArrayList<>();
    private final static List<Class<? extends RedisSubscription>> CHANNEL_SUBSCRIPTIONS = new ArrayList<>();
    private final static List<Class<? extends PublishService>> CHANNEL_PUBLISHES = new ArrayList<>();

    /**
     * scan project for redis annotations
     *
     * @param mainClass main class of the project
     */
    public void findChannelActions(Class<?> mainClass) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());
        ClasspathHelper.forPackage(mainClass.getPackage().getName(), mainClass.getClassLoader()).forEach(reflections::scan);
        provideAnnotations(reflections);
    }

    private void provideAnnotations(Reflections reflections) {
        reflections.getTypesAnnotatedWith(RedisChannelAction.class).forEach(channel -> {
            handleChannelActionAnnotations((Class<? extends IRedisChannelAction>) channel);
            handleRedisSubscriptionAnnotations(channel);
        });
    }

    private void handleChannelActionAnnotations(Class<? extends IRedisChannelAction> clazz) {
        try {
            CHANNEL_ACTION_CLASSES.add(clazz);
            CHANNEL_PUBLISHES.add((Class<? extends PublishService>) clazz);
            try {
                channelManager.addChannel(clazz.getMethod("onExecute", String.class).getAnnotation(RedisSubscription.class).channel(), clazz);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException ignored) {
        }
    }

    private void handleRedisSubscriptionAnnotations(Class<?> clazz) {
        try {
            CHANNEL_SUBSCRIPTIONS.add((Class<? extends RedisSubscription>) clazz);
        } catch (NullPointerException ignored) {
        }
    }

    public static List<Class<? extends IRedisChannelAction>> getChannelActionClasses() {
        return CHANNEL_ACTION_CLASSES;
    }

    public static List<Class<? extends RedisSubscription>> getChannelSubscriptions() {
        return CHANNEL_SUBSCRIPTIONS;
    }

    public static List<Class<? extends PublishService>> getChannelPublishes() {
        return CHANNEL_PUBLISHES;
    }
}
