package uk.co.n3fs.mc.gcvbridge.velocity;

import me.lucko.gchat.api.events.GChatMessageFormedEvent;
import net.kyori.text.TextComponent;
import org.javacord.api.DiscordApi;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;
import uk.co.n3fs.mc.gcvbridge.util.TextUtil;

public class GChatListener {

    private final GCVBridge plugin;
    private final DiscordApi dApi;

    public GChatListener(GCVBridge plugin) {
        this.plugin = plugin;
        dApi = plugin.getDApi();
    }

    public void onGChatMessage(GChatMessageFormedEvent event) {
        plugin.getConfig().getOutChannels(dApi)
            .forEach(textChannel -> textChannel.sendMessage(TextUtil.toMarkdown((TextComponent) event.getMessage())));
    }

}
