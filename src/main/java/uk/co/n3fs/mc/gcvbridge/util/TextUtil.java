package uk.co.n3fs.mc.gcvbridge.util;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextDecoration;

public class TextUtil {

    public static String toMarkdown(TextComponent component) {
        return toMarkdown(component, "");
    }

    private static String toMarkdown(TextComponent component, String base) {
        String currentSegment = component.content();
        for (TextDecoration decoration : component.decorations()) {
            switch (decoration) {
                case BOLD:
                    currentSegment = String.format("**%s**", currentSegment);
                case ITALIC:
                    currentSegment = String.format("*%s*", currentSegment);
                case UNDERLINED:
                    currentSegment = String.format("__%s__", currentSegment);
                case STRIKETHROUGH:
                    currentSegment = String.format("~~%s~~", currentSegment);
                case OBFUSCATED:
                    currentSegment = String.format("||%s||", currentSegment);
            }
        }

        base = base + currentSegment;

        for (Component child : component.children()) {
            base = toMarkdown((TextComponent) child, base);
        }

        return base;
    }

}
