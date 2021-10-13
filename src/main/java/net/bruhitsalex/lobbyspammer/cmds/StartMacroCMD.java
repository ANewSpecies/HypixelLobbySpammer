package net.bruhitsalex.lobbyspammer.cmds;

import net.bruhitsalex.lobbyspammer.script.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.bruhitsalex.lobbyspammer.script.Script;

public class StartMacroCMD extends CommandBase {

    @Override
    public String getCommandName() {
        return "startlobbyspammer";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "Starts lobby spammer";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        Chat.addToChat("STARTING SCRIPT", EnumChatFormatting.GREEN);
        Script.start();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
