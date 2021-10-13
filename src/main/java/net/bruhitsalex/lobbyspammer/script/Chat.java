package net.bruhitsalex.lobbyspammer.script;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.regex.Pattern;

public class Chat {

    public static void addToChat(String msg, EnumChatFormatting colour) {
        IChatComponent component = new ChatComponentText("[ezLobbySpammer] " + msg);
        ChatStyle textStyle = new ChatStyle();
        textStyle.setColor(colour);
        textStyle.setBold(true);
        component.setChatStyle(textStyle);

        try {
            Minecraft.getMinecraft().thePlayer.addChatMessage(component);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static String stripColor(final String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String keepScoreboardChars(String message){
        return Pattern.compile("[^a-z A-Z:0-9/'._]").matcher(message).replaceAll("");
    }

}
