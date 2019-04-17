package uk.co.n3fs.mc.gcvbridge.velocity;

import com.velocitypowered.api.event.Subscribe;
import me.lucko.gchat.api.events.GChatMessageFormedEvent;
import net.kyori.text.TextComponent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;
import uk.co.n3fs.mc.gcvbridge.util.TextUtil;

public class GChatListener {

    private final GCVBridge plugin;

    public GChatListener(GCVBridge plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onGChatMessage(GChatMessageFormedEvent event) {
        if (plugin.getConfig().isRequireSendPerm() && !event.getSender().hasPermission("gcvb.send")) return;

        plugin.getConfig().getOutChannels(plugin.getDApi())
            .forEach(textChannel -> textChannel.sendMessage(TextUtil.toMarkdown((TextComponent) event.getMessage())));
    }

}
