package net.bruhitsalex.lobbyspammer.cmds;

import net.bruhitsalex.lobbyspammer.script.Chat;
import net.bruhitsalex.lobbyspammer.script.Script;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class StopMacroCMD extends CommandBase {

    @Override
    public String getCommandName() {
        return "stoplobbyspammer";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "Stops lobby spammer";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        Chat.addToChat("STOPPING SCRIPT", EnumChatFormatting.GREEN);
        Script.stop();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
