package de.btobastian.javacord.util.handler.user;

import com.fasterxml.jackson.databind.JsonNode;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entity.channel.ServerTextChannel;
import de.btobastian.javacord.event.user.UserStartTypingEvent;
import de.btobastian.javacord.listener.user.UserStartTypingListener;
import de.btobastian.javacord.util.gateway.PacketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the typing start packet.
 */
public class TypingStartHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public TypingStartHandler(DiscordApi api) {
        super(api, true, "TYPING_START");
    }

    @Override
    public void handle(JsonNode packet) {
        long userId = packet.get("user_id").asLong();
        long channelId = packet.get("channel_id").asLong();
        api.getTextChannelById(channelId).ifPresent(channel -> api.getCachedUserById(userId).ifPresent(user -> {
            UserStartTypingEvent event = new UserStartTypingEvent(api, user, channel);

            List<UserStartTypingListener> listeners = new ArrayList<>();
            listeners.addAll(channel.getUserStartTypingListeners());
            if (channel instanceof ServerTextChannel) {
                listeners.addAll(((ServerTextChannel) channel).getServer().getUserStartTypingListeners());
            }
            listeners.addAll(user.getUserStartTypingListeners());
            listeners.addAll(api.getUserStartTypingListeners());

            if (channel instanceof ServerTextChannel) {
                api.getEventDispatcher().dispatchEvent(((ServerTextChannel) channel).getServer(),
                        listeners, listener -> listener.onUserStartTyping(event));
            } else {
                api.getEventDispatcher().dispatchEvent(api, listeners, listener -> listener.onUserStartTyping(event));
            }
        }));
    }

}