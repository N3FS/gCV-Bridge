package uk.co.n3fs.mc.gcvbridge.discord;

import com.velocitypowered.api.proxy.ProxyServer;
import me.lucko.gchat.api.ChatFormat;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.serializer.ComponentSerializers;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;

public class ChatListener {

    private final GCVBridge plugin;
    private final ProxyServer proxy;

    public ChatListener(GCVBridge plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    public void onMessage(MessageCreateEvent event) {
        if (plugin.getConfig().isPlayerlistEnabled() && event.getMessage().getReadableContent().startsWith("playerlist")) return;
        if (!plugin.getConfig().getInChannels(event.getApi()).contains(event.getChannel())) return;
        if (event.getMessageAuthor().isYourself()) return;

        ChatFormat format = plugin.getConfig().getInFormat(plugin.getGChatApi());
        String message = event.getReadableMessageContent();
        MessageAuthor author = event.getMessageAuthor();

        String formattedMsg = replacePlaceholders(format.getFormatText(), author, message);
        String hover = replacePlaceholders(format.getHoverText(), author, message);

        ClickEvent.Action clickType = format.getClickType();
        String clickValue = replacePlaceholders(format.getClickValue(), author, message);

        TextComponent component = ComponentSerializers.LEGACY.deserialize(formattedMsg, '&').toBuilder()
            .applyDeep(m -> {
                if (hover != null) {
                    m.hoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.of(hover)));
                }
                if (clickType != null) {
                    m.clickEvent(new ClickEvent(clickType, clickValue));
                }
            })
            .build();

        proxy.getAllPlayers().stream()
            .filter(player -> !plugin.getConfig().isRequireSeePerm() || player.hasPermission("gcvb.see"))
            .forEach(player -> player.sendMessage(component));

        plugin.getLogger().info(ComponentSerializers.PLAIN.serialize(component));
    }

    private static String replacePlaceholders(String format, MessageAuthor author, String message) {
        return format == null ? null : format
            .replace("{name}", author.getName())
            .replace("{username}", author.getName())
            .replace("{display_name}", author.getDisplayName())
            .replace("{server_name}", "discord")
            .replace("{message}", message);
    }
}
