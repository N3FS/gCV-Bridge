package uk.co.n3fs.mc.gcvbridge.velocity;

import com.velocitypowered.api.event.Subscribe;
import me.lucko.gchat.api.events.GChatMessageFormedEvent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;
import uk.co.n3fs.mc.gcvbridge.util.TextUtil;

//TODO: testing
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.slf4j.Logger;

public class GChatListener {

    private final GCVBridge plugin;

    public GChatListener(GCVBridge plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onGChatMessage(GChatMessageFormedEvent event) {
        Logger logger = plugin.getLogger();

        if (plugin.getConfig().isRequireSendPerm() && !event.getSender().hasPermission("gcvb.send")) return;
        final String msg = TextUtil.stripString(TextUtil.toMarkdown(event.getMessage()));

        plugin.getConfig().getWebhooks(plugin.getDApi())
            .forEach(webhookUrl -> {
//                try (WebhookClient client = WebhookClient.withUrl(webhookUrl)) {
                //TODO: the webhook library's url parsing is broken, so we take a more trusting and gung-ho approach.
                // Start workaround.
                String[] webhookUrlParts = webhookUrl.split("/");
                long id = Long.parseLong(webhookUrlParts[5]);
                String token = webhookUrlParts[6];

                try (WebhookClient client = WebhookClient.withId(id, token)) {
                // End workaround.
                    WebhookMessageBuilder hookMsg = new WebhookMessageBuilder()
                    .setUsername(event.getSender().getUsername())
                    .setAvatarUrl("https://crafatar.com/avatars/" + event.getSender().getUniqueId() + "?size=512&overlay")
                    .setContent(TextUtil.stripString(event.getRawMessage()));
                    client.send(hookMsg.build());
                }
            });
        // This should be a config option, but for now I'm just commenting it out.
        /*
        plugin.getConfig().getOutChannels(plugin.getDApi())
                .forEach(textChannel -> textChannel.sendMessage(msg));
        */
    }

}
