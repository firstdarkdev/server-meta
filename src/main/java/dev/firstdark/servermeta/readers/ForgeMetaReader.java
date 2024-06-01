package dev.firstdark.servermeta.readers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.firstdark.servermeta.Constants;
import dev.firstdark.servermeta.MetaServer;
import dev.firstdark.servermeta.database.DatabaseController;
import dev.firstdark.servermeta.database.tables.ForgeVersionsTable;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.List;

import static dev.firstdark.servermeta.utils.FileUtils.*;

/**
 * @author HypherionSA
 * Worker to pull forge versions and build the database for it
 */
public class ForgeMetaReader extends Thread {

    private final DefaultArtifactVersion minForge = new DefaultArtifactVersion("1.5.2-7.8.0.684");

    public ForgeMetaReader() {
        this.setName("Forge Meta Reader");
    }

    @Override
    public void run() {
        MetaServer.LOGGER.info("Start Refreshing Forge Meta");
        JsonObject mainMeta = gson.fromJson(getJson("https://files.minecraftforge.net/net/minecraftforge/forge/maven-metadata.json"), JsonObject.class);

        for (String k : mainMeta.keySet()) {
            List<JsonElement> versionArray = mainMeta.getAsJsonArray(k).asList();

            for (JsonElement e : versionArray) {
                String version = e.getAsString();
                DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(version);

                if (currentVersion.compareTo(minForge) < 0)
                    continue;

                if (DatabaseController.INSTANCE.getDB().findById(version, ForgeVersionsTable.class) == null) {
                    String base_url = Constants.MIRROR + "/net/minecraftforge/forge/%s/forge-%s-installer.jar";
                    String url = String.format(base_url, version, version);
                    String sha1 = getSha1(url + ".sha1");
                    long fileSize = getFileSizeFromURL(url);

                    if (sha1.equalsIgnoreCase("unknown") || fileSize == 0)
                        continue;

                    ForgeVersionsTable table = new ForgeVersionsTable();
                    table.setVersion(version);
                    table.setType("release");
                    table.setHash(sha1);
                    table.setDownloadUrl(url);
                    table.setSize(fileSize);
                    DatabaseController.INSTANCE.insertData(table);
                    MetaServer.LOGGER.warn("Added " + version + " to cache");
                }
            }
        }
    }
}
