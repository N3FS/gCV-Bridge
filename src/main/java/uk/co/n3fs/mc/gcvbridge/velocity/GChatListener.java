package uk.co.n3fs.mc.gcvbridge.velocity;

import com.velocitypowered.api.event.Subscribe;
import me.lucko.gchat.api.events.GChatMessageFormedEvent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;
import uk.co.n3fs.mc.gcvbridge.util.TextUtil;

import com.velocitypowered.api.proxy.Player;
import club.minnced.discord.webhook.*;
import club.minnced.discord.webhook.send.*;

public class GChatListener {

    private final GCVBridge plugin;
    private final String webhook;
    private Boolean has_webhook = false;
    private WebhookClient client = null;

    public GChatListener(GCVBridge plugin) {
        this.plugin = plugin;

        this.webhook = plugin.getConfig().getOutWebhook();

        if (this.webhook != null && !this.webhook.isEmpty()) {
            this.has_webhook = true;

            WebhookClientBuilder builder = new WebhookClientBuilder(this.webhook);

            // builder.setThreadFactory((job) -> {
            //     Thread thread = new Thread(job);
            //     thread.setName("Hello");
            //     thread.setDaemon(true);
            //     return thread;
            // });

            //builder.setWait(true);
            this.client = builder.build();
        }
    }

    @Subscribe
    public void onGChatMessage(GChatMessageFormedEvent event) {

        Player player = event.getSender();

        if (plugin.getConfig().isRequireSendPerm() && !player.hasPermission("gcvb.send")) return;
        final String msg = TextUtil.stripString(TextUtil.toMarkdown(event.getMessage()));

        if (this.has_webhook) {
            WebhookMessageBuilder builder = new WebhookMessageBuilder();

            builder.setUsername(player.getUsername());

            String avatar_url = "https://crafatar.com/avatars/" + player.getUniqueId();

            builder.setAvatarUrl(avatar_url);

            String raw_msg = event.getRawMessage();

            builder.setContent(raw_msg);

            this.client.send(builder.build());
        }

        plugin.getConfig().getOutChannels(plugin.getDApi())
                .forEach(textChannel -> textChannel.sendMessage(msg));
    }

}
