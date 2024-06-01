package dev.firstdark.servermeta.readers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.firstdark.servermeta.Constants;
import dev.firstdark.servermeta.MetaServer;
import dev.firstdark.servermeta.database.DatabaseController;
import dev.firstdark.servermeta.database.tables.PaperVersionsTable;

import static dev.firstdark.servermeta.utils.FileUtils.*;

/**
 * @author HypherionSA
 * Worker to pull paper versions and build the database for it
 */
public class PaperMetaReader extends Thread {

    public PaperMetaReader() {
        this.setName("Paper Meta Updater");
    }

    @Override
    public void run() {
        MetaServer.LOGGER.info("Start Refreshing Paper Meta");
        JsonObject mainMeta = gson.fromJson(getJson("https://api.papermc.io/v2/projects/paper"), JsonObject.class);
        if (!mainMeta.has("version_groups"))
            return;

        JsonArray versionsArray = mainMeta.getAsJsonArray("version_groups");

        versionsArray.asList().forEach(v -> {
            String versionUrl = String.format("https://api.papermc.io/v2/projects/paper/version_group/%s/builds", v.getAsString());
            JsonObject meta = gson.fromJson(getJson(versionUrl), JsonObject.class);

            if (!meta.has("builds"))
                return;

            JsonArray builds = meta.getAsJsonArray("builds");

            for (JsonElement element : builds.asList()) {
                JsonObject build = element.getAsJsonObject();

                if (!build.has("downloads") && !build.getAsJsonObject("downloads").has("application"))
                    continue;

                String versionId = String.format("%s-%s", build.get("version").getAsString(), build.get("build").getAsString());

                PaperVersionsTable t = DatabaseController.INSTANCE.getDB().findById(versionId, PaperVersionsTable.class);

                if (t == null) {
                    String url = String.format("%s/projects/paper/versions/%s/builds/%s/downloads/paper-%s-%s.jar", Constants.MIRROR, build.get("version").getAsString(), build.get("build"), build.get("version").getAsString(), build.get("build"));
                    long size = getFileSizeFromURL(url);

                    PaperVersionsTable table = new PaperVersionsTable();
                    table.setVersion(versionId);
                    table.setSize(size != -1 ? size : 0);
                    table.setType(build.get("channel").getAsString());
                    table.setHash(build.getAsJsonObject("downloads").getAsJsonObject("application").get("sha256").getAsString());
                    table.setDownloadUrl(url);
                    DatabaseController.INSTANCE.insertData(table);
                    MetaServer.LOGGER.warn("Added " + versionId + " to cache");
                }
            }
        });
    }
}
