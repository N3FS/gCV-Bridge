package uk.co.n3fs.mc.gcvbridge.discord;

import com.vdurmont.emoji.EmojiParser;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import me.lucko.gchat.api.ChatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import uk.co.n3fs.mc.gcvbridge.GCVBridge;

public class ChatListener {

    private static final LegacyComponentSerializer LEGACY_LINKING_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .extractUrls()
            .build();
    private final GCVBridge plugin;
    private final ProxyServer proxy;

    public ChatListener(GCVBridge plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    private static String replacePlaceholders(String format, MessageAuthor author, String message) {
        return format == null ? null : format
                .replace("{name}", author.getName())
                .replace("{username}", author.getName())
                .replace("{display_name}", author.getDisplayName())
                .replace("{server_name}", "discord")
                .replace("{message}", message);
    }

    private static TextComponent formatMessage(String format, MessageAuthor author, String message) {
        if (format == null) return Component.empty();
        String replaced = replacePlaceholders(format, author, "{message}");
        Component convertedMessage = MinecraftSerializer.INSTANCE.serialize(message);
        return Component.text("").append(LEGACY_LINKING_SERIALIZER.deserialize(replaced).replaceText("{message}", convertedMessage));
    }

    public void onMessage(MessageCreateEvent event) {
        if (plugin.getConfig().isPlayerlistEnabled() && event.getMessage().getReadableContent().toLowerCase().startsWith("playerlist"))
            return;
        if (!plugin.getConfig().getInChannels(event.getApi()).contains(event.getChannel())) return;
        if (event.getMessageAuthor().isYourself()) return;

        MessageAuthor author = event.getMessageAuthor();

        // Ignore webhook authors (probably ourselves)
        if (author.isWebhook()) {
            return;
        }

        ChatFormat format = plugin.getConfig().getInFormat();

        String message = event.getReadableMessageContent();
        message = EmojiParser.parseToAliases(message);

        if (format == null) {
            return;
        }

        String hover_text = format.getHoverText();

        final Component hover;

        if (hover_text != null) {
            hover = formatMessage(format.getHoverText(), author, message);
        } else {
            hover = null;
        }

        ClickEvent.Action clickType = format.getClickType();
        String clickValue = replacePlaceholders(format.getClickValue(), author, message);

        TextComponent component = formatMessage(format.getFormatText(), author, message)
                .toBuilder()
                .applyDeep(m -> {
                    if (hover != null) {
                        m.hoverEvent(HoverEvent.showText(hover));
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
}
