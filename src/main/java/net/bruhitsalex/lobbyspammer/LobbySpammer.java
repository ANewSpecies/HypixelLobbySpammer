package net.bruhitsalex.lobbyspammer;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.bruhitsalex.lobbyspammer.cmds.StartMacroCMD;
import net.bruhitsalex.lobbyspammer.cmds.StopMacroCMD;
import net.bruhitsalex.lobbyspammer.script.Config;
import net.bruhitsalex.lobbyspammer.script.Script;

@Mod(modid = "LobbySpammer", version = "1.0", clientSideOnly = true, acceptedMinecraftVersions = "1.8.9", name = "LobbySpammer")
public class LobbySpammer {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Config.loadConfig();
        ClientCommandHandler.instance.registerCommand(new StartMacroCMD());
        ClientCommandHandler.instance.registerCommand(new StopMacroCMD());
        MinecraftForge.EVENT_BUS.register(new Script());
    }

}
