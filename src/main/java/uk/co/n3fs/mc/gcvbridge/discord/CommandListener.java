package uk.co.n3fs.mc.gcvbridge.discord;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandListener {

    private final GCVBridge plugin;
    private final ProxyServer proxy;

    public CommandListener(GCVBridge plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    public void onPlayerlist(MessageCreateEvent event) {
        Message commandMsg = event.getMessage();
        if (!plugin.getConfig().isPlayerlistEnabled() || !commandMsg.getReadableContent().startsWith("playerlist")) return;

        final int count = proxy.getPlayerCount();
        final String players = proxy.getAllPlayers().stream()
            .map(Player::getGameProfile)
            .map(GameProfile::getName)
            .collect(Collectors.joining(plugin.getConfig().getPlayerlistSeparator()));

        final String response = plugin.getConfig().getPlayerlistFormat()
            .replace("{count}", Integer.toString(count))
            .replace("{players}", players);

        event.getChannel().sendMessage(response).thenAccept(responseMsg -> {
            if (plugin.getConfig().getPlayerlistCommandRemoveDelay() >= 0) {
                proxy.getScheduler().buildTask(plugin, commandMsg::delete)
                    .delay(plugin.getConfig().getPlayerlistCommandRemoveDelay(), TimeUnit.SECONDS)
                    .schedule();
            }
            if (plugin.getConfig().getPlayerlistResponseRemoveDelay() >= 0) {
                proxy.getScheduler().buildTask(plugin, responseMsg::delete)
                    .delay(plugin.getConfig().getPlayerlistResponseRemoveDelay(), TimeUnit.SECONDS)
                    .schedule();
            }
        });
    }
}
