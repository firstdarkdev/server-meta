package dev.firstdark.servermeta.readers;

import com.google.gson.JsonArray;
import dev.firstdark.servermeta.MetaServer;
import dev.firstdark.servermeta.database.DatabaseController;
import dev.firstdark.servermeta.database.tables.FabricVersionsTable;
import dev.firstdark.servermeta.models.minecraft.MinecraftManifest;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.firstdark.servermeta.utils.FileUtils.getJson;
import static dev.firstdark.servermeta.utils.FileUtils.gson;

/**
 * @author HypherionSA
 * Worker to pull fabric versions and build the database for it
 */
public class FabricMetaReader extends Thread {

    public FabricMetaReader() {
        this.setName("Fabric Meta Updater");
    }

    @Override
    public void run() {
        MetaServer.LOGGER.info("Start Refreshing Fabric Meta");
        try {
            String MC_MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
            MinecraftManifest manifest = gson.fromJson(getJson(MC_MANIFEST), MinecraftManifest.class);
            Collections.reverse(manifest.mVersions);

            JsonArray fabricLoader = gson.fromJson(getJson("https://meta.fabricmc.net/v2/versions/loader"), JsonArray.class);

            manifest.mVersions.forEach(v -> {
                if (v.mId.startsWith("3D"))
                    return;

                DefaultArtifactVersion lowest = new DefaultArtifactVersion("1.14");
                DefaultArtifactVersion current = new DefaultArtifactVersion(v.mId);

                if (current.compareTo(lowest) < 0 || isLowerThan(v.mId))
                    return;

                fabricLoader.asList().forEach(fv -> {
                    String rawV = fv.getAsJsonObject().get("version").getAsString();
                    String fabricV = v.mId + "+" + fv.getAsJsonObject().get("version").getAsString();
                    if (fabricV.contains("+build"))
                        return;

                    if (DatabaseController.INSTANCE.getDB().findById(fabricV, FabricVersionsTable.class) == null) {
                        FabricVersionsTable table = new FabricVersionsTable();
                        table.setVersion(fabricV);
                        table.setHash("");
                        table.setDownloadUrl(String.format("https://meta.fabricmc.net/v2/versions/loader/%s/%s/1.0.1/server/jar", v.mId, rawV));
                        table.setSize(0);
                        table.setType(v.mType);
                        DatabaseController.INSTANCE.insertData(table);
                        MetaServer.LOGGER.warn("Added " + fabricV + " to cache");
                    }
                });

            });
        } catch (Exception e) {
            MetaServer.LOGGER.error("Failed to read fabric meta", e);
        }

        MetaServer.LOGGER.info("DONE");
    }

    public static boolean isLowerThan(String version) {
        if (!version.matches("\\d{2}w[0-9]+[a-z]?")) {
            return false;
        }

        Matcher matcher = Pattern.compile("(\\d{2})w([0-9]+)([a-z]?)").matcher(version);
        if (!matcher.find()) {
            return false;
        }

        int majorVersion = Integer.parseInt(matcher.group(1));
        int minorVersion = Integer.parseInt(matcher.group(2));

        return Integer.parseInt(String.format("%s%s", majorVersion, minorVersion)) < 1843;
    }


}
