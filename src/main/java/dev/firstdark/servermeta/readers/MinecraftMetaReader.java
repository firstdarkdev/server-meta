package dev.firstdark.servermeta.readers;

import com.google.gson.JsonObject;
import dev.firstdark.servermeta.MetaServer;
import dev.firstdark.servermeta.database.DatabaseController;
import dev.firstdark.servermeta.database.tables.MinecraftVersionsTable;
import dev.firstdark.servermeta.models.minecraft.MinecraftManifest;

import java.util.Collections;

import static dev.firstdark.servermeta.utils.FileUtils.getJson;
import static dev.firstdark.servermeta.utils.FileUtils.gson;

/**
 * @author HypherionSA
 * Worker to pull minecraft versions and build the database for it
 */
public class MinecraftMetaReader extends Thread {

    public MinecraftMetaReader() {
        this.setName("Minecraft Meta Updater");
    }

    @Override
    public void run() {
        MetaServer.LOGGER.info("Start Refreshing Minecraft Meta");
        try {
            String MC_MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
            MinecraftManifest manifest = gson.fromJson(getJson(MC_MANIFEST), MinecraftManifest.class);

            Collections.reverse(manifest.mVersions);

            manifest.mVersions.forEach(v -> {
                if (DatabaseController.INSTANCE.getDB().findById(v.mId, MinecraftVersionsTable.class) == null) {
                    JsonObject version = gson.fromJson(getJson(v.mUrl), JsonObject.class);

                    if (version.has("downloads") && version.getAsJsonObject("downloads").has("server")) {
                        JsonObject server = version.getAsJsonObject("downloads").getAsJsonObject("server");

                        MinecraftVersionsTable table = new MinecraftVersionsTable();
                        table.setVersion(v.mId);
                        table.setHash(server.get("sha1").getAsString());
                        table.setDownloadUrl(server.get("url").getAsString());
                        table.setSize(server.get("size").getAsLong());
                        table.setType(v.mType);
                        DatabaseController.INSTANCE.insertData(table);
                        MetaServer.LOGGER.warn("Added " + v.mId + " to cache");
                    }
                }
            });
        } catch (Exception e) {
            MetaServer.LOGGER.error("Failed to read minecraft meta", e);
        }
    }

}
