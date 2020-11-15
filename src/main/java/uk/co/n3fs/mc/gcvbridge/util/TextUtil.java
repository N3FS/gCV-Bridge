package uk.co.n3fs.mc.gcvbridge.util;

import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer;
import net.kyori.adventure.text.Component;

public class TextUtil {

    public static String toMarkdown(Component component) {
        return DiscordSerializer.INSTANCE.serialize(component);
    }

    public static String stripString(final String msg) {
        return msg.replaceAll("@everyone", "(at)everyone")
                .replaceAll("@here", "(at)here")
                .replaceAll("<@&[0-9]+>", "");
    }

}
