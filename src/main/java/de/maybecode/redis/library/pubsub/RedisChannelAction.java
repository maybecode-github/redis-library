package de.maybecode.redis.library.pubsub;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisChannelAction {

    String name() default "no name set";

    String description() default "no description set";

}
