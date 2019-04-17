package uk.co.n3fs.mc.gcvbridge.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import org.javacord.api.DiscordApi;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;

public class VelocityListener {

    private final GCVBridge plugin;

    public VelocityListener(GCVBridge plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        if (event.getPlayer().hasPermission("gcvb.silentjoin")) return;

        String message = plugin.getConfig().getJoinFormat()
            .replace("{player}", event.getPlayer().getUsername());

        plugin.getConfig().getOutChannels(plugin.getDApi()).forEach(chan -> chan.sendMessage(message));
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        if (event.getPlayer().hasPermission("gcvb.silentquit")) return;

        String message = plugin.getConfig().getQuitFormat()
            .replace("{player}", event.getPlayer().getUsername());

        plugin.getConfig().getOutChannels(plugin.getDApi()).forEach(chan -> chan.sendMessage(message));
    }

}
