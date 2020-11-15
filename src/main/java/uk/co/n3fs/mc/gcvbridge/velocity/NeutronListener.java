package uk.co.n3fs.mc.gcvbridge.velocity;

import com.velocitypowered.api.event.Subscribe;
import me.crypnotic.neutron.api.event.AlertBroadcastEvent;
import me.crypnotic.neutron.api.user.User;
import net.kyori.adventure.text.Component;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;
import uk.co.n3fs.mc.gcvbridge.util.TextUtil;

public class NeutronListener {
    private final GCVBridge plugin;

    public NeutronListener(GCVBridge plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onNeutronBroadcast(AlertBroadcastEvent event) {
        Component component = event.getMessage();
        String message = plugin.getConfig().getNeutronAlertFormat()
                .replace("{message}", TextUtil.toMarkdown(component))
                .replace("{author}", event.getAuthor().map(User::getName).orElse("CONSOLE"));

        if (!message.isEmpty()) {
            plugin.getConfig().getOutChannels(plugin.getDApi())
                    .forEach(channel -> channel.sendMessage(message));
        }
    }

}
