package dev.firstdark.servermeta;

import dev.firstdark.servermeta.readers.*;
import dev.firstdark.servermeta.server.MetaWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author HypherionSA
 * Main Entry Point
 */
public class MetaServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(MetaServer.class);
    
    private static final ScheduledExecutorService threader = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private static MinecraftMetaReader metaReader;
    private static ForgeMetaReader forgeMetaReader;
    private static PaperMetaReader paperMetaReader;
    private static NeoForgeMetaReader neoForgeMetaReader;
    private static QuiltMetaReader quiltMetaReader;
    private static FabricMetaReader fabricMetaReader;
    
    
    public static void main(String[] args) {
        
        metaReader.start();

        threader.scheduleAtFixedRate(() -> {
            if (metaReader == null || !metaReader.isAlive()) {
                metaReader = new MinecraftMetaReader();
                metaReader.start();
            }
        }, 1, 5, TimeUnit.MINUTES);

        threader.scheduleAtFixedRate(() -> {
            if (forgeMetaReader == null || !forgeMetaReader.isAlive()) {
                forgeMetaReader = new ForgeMetaReader();
                forgeMetaReader.start();
            }
        }, 1, 5, TimeUnit.MINUTES);

        threader.scheduleAtFixedRate(() -> {
            if (paperMetaReader == null || !paperMetaReader.isAlive()) {
                paperMetaReader = new PaperMetaReader();
                paperMetaReader.start();
            }
        }, 1, 5, TimeUnit.MINUTES);

        threader.scheduleAtFixedRate(() -> {
            if (neoForgeMetaReader == null || !neoForgeMetaReader.isAlive()) {
                neoForgeMetaReader = new NeoForgeMetaReader();
                neoForgeMetaReader.start();
            }
        }, 1, 5, TimeUnit.MINUTES);

        threader.scheduleAtFixedRate(() -> {
            if (quiltMetaReader == null || !quiltMetaReader.isAlive()) {
                quiltMetaReader = new QuiltMetaReader();
                quiltMetaReader.start();
            }
        }, 1, 5, TimeUnit.MINUTES);

        threader.scheduleAtFixedRate(() -> {
            if (fabricMetaReader == null || !fabricMetaReader.isAlive()) {
                fabricMetaReader = new FabricMetaReader();
                fabricMetaReader.start();
            }
        }, 1, 5, TimeUnit.MINUTES);

        MetaWebServer.INSTANCE.start(2500);
    }

}
