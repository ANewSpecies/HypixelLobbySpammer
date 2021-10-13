package net.bruhitsalex.lobbyspammer.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Script {

    public static boolean isRunning;
    private static int spacesAvailable;
    private static List<String> playerNameList;
    private static boolean definatelyInChat;
    private static List<Timer> timers;
    private static int successfullyAdvertised = 0;

    private static void resetVariables(boolean isRunning) {
        Script.isRunning = isRunning;
        spacesAvailable = 5;
        playerNameList = new ArrayList<>();
        definatelyInChat = false;
        sequenceRunning = false;
        sequenceTick = 0;
        sequencePlayername = new ArrayList<>();
        timers = new ArrayList<>();
        successfullyAdvertised = 0;
    }

    public static void start() {
        resetVariables(true);
        loadPlayerNames();
        checkChannel();
        invitePlayer();
    }

    public static void stop() {
        if (successfullyAdvertised != 0) {
            Chat.addToChat("Successfully advertised to " + successfullyAdvertised + " players.", EnumChatFormatting.GREEN);
        }

        sendCommand("/p disband");
        resetVariables(false);
    }

    private static void invitePlayer() {
        invitePlayer(false);
    }

    private static void invitePlayer(boolean bypass) {
        if (!isRunning || spacesAvailable == 0) {
            return;
        }

        if (playerNameList.size() == 0 && !bypass) {
            Chat.addToChat("No more invites left for this lobby, go next!", EnumChatFormatting.GREEN);
            Chat.addToChat("STOPPING SCRIPT.", EnumChatFormatting.GREEN);
            stop();
            return;
        }

        String nameToAdd = playerNameList.get(0);
        playerNameList.remove(nameToAdd);

        boolean stillOnline = false;
        Collection<NetworkPlayerInfo> playersC = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        for (NetworkPlayerInfo info : playersC) {
            String loadedPlayerName = info.getGameProfile().getName();
            if (loadedPlayerName.equals(nameToAdd)) {
                stillOnline = true;
                break;
            }
        }

        if (!stillOnline) {
            Chat.addToChat(nameToAdd + " is no longer online, skipping.", EnumChatFormatting.RED);
            invitePlayer();
            return;
        }

        inviteToParty(nameToAdd);
        Chat.addToChat("New player invited, " + playerNameList.size()
                        + " players left. (" + (spacesAvailable - 1) + " spaces available in party).",
                EnumChatFormatting.YELLOW);
    }

    private static void checkChannel() {
        if (!definatelyInChat) {
            sendCommand("/chat p");
            definatelyInChat = true;
        }
    }

    private static void loadPlayerNames() {
        if (!isRunning) {
            return;
        }

        Collection<NetworkPlayerInfo> playersC = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        playersC.forEach((loadedPlayer) -> {
            String loadedPlayerName = loadedPlayer.getGameProfile().getName();
            if (!loadedPlayerName.equals(Minecraft.getMinecraft().thePlayer.getDisplayNameString()) && !loadedPlayerName.startsWith("!")) {
                playerNameList.add(loadedPlayerName);
            }
        });
        Chat.addToChat("Loaded " + playerNameList.size() + " players into invite list.", EnumChatFormatting.GREEN);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!isRunning) {
            return;
        }

        String msg = event.message.getFormattedText();
        String rawMsg = event.message.getFormattedText();
        msg = Chat.stripColor(msg);
        msg = Chat.keepScoreboardChars(msg);

        if (rawMsg.contains("They have §r§c60 §r§eseconds to accept.§r")) {
            spacesAvailable--;
            Chat.addToChat("New invite successfully sent.", EnumChatFormatting.YELLOW);

            if (spacesAvailable != 0 && playerNameList.size() != 0) {
                timers.add(new Timer(20, () -> Script.invitePlayer(true)));
            }
        }  else if (msg.contains("has expired")) {
            spacesAvailable++;
            Chat.addToChat("Invite expired.", EnumChatFormatting.YELLOW);
            invitePlayer();

        } else if (msg.contains("joined the party")) {
            String personJoined = msg.substring(0, msg.indexOf("joined the party") - 1)
                            .replace("VIP", "")
                                    .replace("VIP+", "")
                                            .replace("MVP", "")
                                                    .replace("MVP+", "")
                                                            .replace("MVP++", "")
                                                                    .replace("[", "")
                                                                            .replace("]", "")
                                                                                    .trim();
            Chat.addToChat("Invite accepted!", EnumChatFormatting.GREEN);
            startSequence(personJoined);

        } else if (msg.contains("they're not online")
                || msg.contains("Internal Server Error") || msg.contains("You cannot invite that player.")) {
            spacesAvailable++;
            Chat.addToChat("Invite unable to send.", EnumChatFormatting.YELLOW);
            invitePlayer();

        } else if (msg.contains("You have 5 or more invites pending,")) {
            Chat.addToChat("Counted wrong! Re-setting spaces available to 0.", EnumChatFormatting.RED);
            spacesAvailable = 0;
        }
    }

    private static boolean sequenceRunning;
    private static int sequenceTick;
    private static List<String> sequencePlayername;

    private static void startSequence(String sequencePlayername) {
        if (sequenceRunning) {
            sequenceTick = 0;
        }

        Script.sequencePlayername.add(sequencePlayername);
        sequenceRunning = true;
    }

    private static void stopSequence() {
        sequenceRunning = false;
        sequenceTick = 0;
        sequencePlayername.clear();
    }

    private static void sequenceTick() {
        if (!sequenceRunning || sequencePlayername.isEmpty()) {
            return;
        }

        sequenceTick++;

        if (sequenceTick == 1) {
            advertiseMessage(Config.getFirstMessage());
        } else if (sequenceTick == 5) {
            advertiseMessage(Config.getSecondMessage());
            successfullyAdvertised++;
        } else if (sequenceTick == 10) {
            int addedTime = 2;
            for (String username : sequencePlayername) {
                timers.add(new Timer(addedTime, () -> kickFromParty(username)));
                addedTime += 10;
            }
            spacesAvailable++;
            Chat.addToChat("Advertising sequence to player '" + sequencePlayername + "' completed!", EnumChatFormatting.GREEN);
        } else if (sequenceTick == 20) {
            invitePlayer();
            stopSequence();
        }
    }

    private static void timersTick() {
        ArrayList<Timer> timersToRemove = new ArrayList<>();

        timers.forEach(timer -> {
            if (timer.tick == timer.tickLength) {
                timer.action.run();
                timersToRemove.add(timer);
            } else {
                timer.tick++;
            }
        });

        timersToRemove.forEach(timer -> timers.remove(timer));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START || !isRunning) {
            return;
        }

        sequenceTick();
        timersTick();
    }

    private static void advertiseMessage(String message) {
        checkChannel();
        sendInChat(message);
    }

    private static void sendInChat(String msg) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(msg);
    }

    private static void sendCommand(String cmd) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(cmd);
    }

    private static void inviteToParty(String playerName) {
        sendCommand("/p " + playerName);
    }

    private static void kickFromParty(String playerName) {
        sendCommand("/p kick " + playerName);
    }

}
