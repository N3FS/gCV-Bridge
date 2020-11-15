package uk.co.n3fs.mc.gcvbridge.discord;

import com.vdurmont.emoji.EmojiParser;
import com.velocitypowered.api.proxy.ProxyServer;
import me.lucko.gchat.api.ChatFormat;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;

public class ChatListener {

    private final GCVBridge plugin;
    private final ProxyServer proxy;

    private static final LegacyComponentSerializer LEGACY_LINKING_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .extractUrls()
            .build();

    public ChatListener(GCVBridge plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    public void onMessage(MessageCreateEvent event) {
        if (plugin.getConfig().isPlayerlistEnabled() && event.getMessage().getReadableContent().toLowerCase().startsWith("playerlist")) return;
        if (!plugin.getConfig().getInChannels(event.getApi()).contains(event.getChannel())) return;
        if (event.getMessageAuthor().isYourself()) return;

        ChatFormat format = plugin.getConfig().getInFormat(plugin.getGChatApi());
        String message = event.getReadableMessageContent();
        message = EmojiParser.parseToAliases(message);
        MessageAuthor author = event.getMessageAuthor();

        String formattedMsg = replacePlaceholders(format.getFormatText(), author, message);
        String hover = replacePlaceholders(format.getHoverText(), author, message);

        ClickEvent.Action clickType = format.getClickType();
        String clickValue = replacePlaceholders(format.getClickValue(), author, message);

        TextComponent component = LEGACY_LINKING_SERIALIZER.deserialize(formattedMsg).toBuilder()
            .applyDeep(m -> {
                if (hover != null) {
                    m.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacy('&').deserialize(hover)));
                }
                if (clickType != null) {
                    m.clickEvent(ClickEvent.clickEvent(clickType, clickValue));
                }
            })
            .build();

        proxy.getAllPlayers().stream()
            .filter(player -> !plugin.getConfig().isRequireSeePerm() || player.hasPermission("gcvb.see"))
            .forEach(player -> player.sendMessage(component));

        plugin.getLogger().info(PlainComponentSerializer.plain().serialize(component));
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
