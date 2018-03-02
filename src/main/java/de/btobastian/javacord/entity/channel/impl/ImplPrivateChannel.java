package de.btobastian.javacord.entity.channel.impl;

import com.fasterxml.jackson.databind.JsonNode;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.ImplDiscordApi;
import de.btobastian.javacord.entity.DiscordEntity;
import de.btobastian.javacord.entity.channel.PrivateChannel;
import de.btobastian.javacord.entity.message.MessageBuilder;
import de.btobastian.javacord.entity.message.impl.ImplMessageBuilder;
import de.btobastian.javacord.entity.user.User;
import de.btobastian.javacord.entity.user.impl.ImplUser;
import de.btobastian.javacord.util.Cleanupable;
import de.btobastian.javacord.util.cache.ImplMessageCache;
import de.btobastian.javacord.util.cache.MessageCache;

import java.util.Objects;

/**
 * The implementation of {@link PrivateChannel}.
 */
public class ImplPrivateChannel implements PrivateChannel, Cleanupable {

    /**
     * The discord api instance.
     */
    private final ImplDiscordApi api;

    /**
     * The id of the channel.
     */
    private final long id;

    /**
     * The recipient of the private channel.
     */
    private final ImplUser recipient;

    /**
     * The message cache of the private channel.
     */
    private final ImplMessageCache messageCache;

    /**
     * Creates a new private channel.
     *
     * @param api The discord api instance.
     * @param data The json data of the channel.
     */
    public ImplPrivateChannel(ImplDiscordApi api, JsonNode data) {
        this.api = api;
        this.recipient = (ImplUser) api.getOrCreateUser(data.get("recipients").get(0));
        this.messageCache = new ImplMessageCache(
                api, api.getDefaultMessageCacheCapacity(), api.getDefaultMessageCacheStorageTimeInSeconds());

        id = Long.parseLong(data.get("id").asText());
        recipient.setChannel(this);
    }

    @Override
    public DiscordApi getApi() {
        return api;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public MessageBuilder createMessageBuilder() {
        return new ImplMessageBuilder().setReceiver(this);
    }

    @Override
    public User getRecipient() {
        return recipient;
    }

    @Override
    public MessageCache getMessageCache() {
        return messageCache;
    }


    @Override
    public void cleanup() {
        messageCache.cleanup();
    }

    @Override
    public boolean equals(Object o) {
        return (this == o)
               || !((o == null)
                    || (getClass() != o.getClass())
                    || (getId() != ((DiscordEntity) o).getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("PrivateChannel (id: %s, recipient: %s)", getIdAsString(), getRecipient());
    }

}