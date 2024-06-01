package dev.firstdark.servermeta.readers;

import dev.firstdark.servermeta.MetaServer;
import dev.firstdark.servermeta.database.DatabaseController;
import dev.firstdark.servermeta.database.tables.NeoForgeVersionsTable;
import dev.firstdark.servermeta.models.NeoForgeVersions;
import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static dev.firstdark.servermeta.utils.FileUtils.getFileSizeFromURL;
import static dev.firstdark.servermeta.utils.FileUtils.getSha1;

/**
 * @author HypherionSA
 * Worker to pull neoforge versions and build the database for it
 */
public class NeoForgeMetaReader extends Thread {

    public NeoForgeMetaReader() {
        this.setName("NeoForge Meta Reader");
    }

    @SneakyThrows
    @Override
    public void run() {
        MetaServer.LOGGER.info("Start Refreshing NeoForge Meta");
        HashMap<String, String> versions = new LinkedHashMap<>();

        NeoForgeVersions oldVersions = readVersions("forge");
        NeoForgeVersions newVersions = readVersions("neoforge");

        if (oldVersions != null && oldVersions.versions != null) {
            oldVersions.versions.forEach(v -> versions.put(v, "forge"));
        }

        if (newVersions != null && newVersions.versions != null) {
            newVersions.versions.forEach(v -> versions.put(v, "neoforge"));
        }

        for (Map.Entry<String, String> version : versions.entrySet()) {
            if (DatabaseController.INSTANCE.getDB().findById(version.getKey(), NeoForgeVersionsTable.class) == null) {
                String base_url = "https://maven.neoforged.net/releases/net/neoforged/%s/%s/%s-%s-installer.jar";
                String url = String.format(base_url, version.getValue(), version.getKey(), version.getValue(), version.getKey());
                String sha1 = getSha1(url + ".sha1");
                long fileSize = getFileSizeFromURL(url);

                if (sha1.equalsIgnoreCase("unknown") || fileSize == 0)
                    continue;

                NeoForgeVersionsTable table = new NeoForgeVersionsTable();
                table.setVersion(version.getKey());
                table.setType("release");
                table.setHash(sha1);
                table.setDownloadUrl(url);
                table.setSize(fileSize);
                DatabaseController.INSTANCE.insertData(table);
                MetaServer.LOGGER.warn("Added " + version.getKey() + " to cache");
            }
        }
    }

    public NeoForgeVersions readVersions(String identifier) throws Exception {
        Document doc = parse(identifier);
        Element root = doc.getRootElement();
        Element versioning = root.element("versioning");
        Element versionsList = versioning.element("versions");

        List<String> vers = versionsList.elements().stream().map(Element::getStringValue).toList();
        NeoForgeVersions v = new NeoForgeVersions();
        v.latest = versioning.element("latest").getStringValue();
        v.release = versioning.element("release").getStringValue();
        v.versions = vers;

        return v;
    }

    private Document parse(String identifier) throws DocumentException {
        SAXReader reader = new SAXReader();
        String META_URL = "https://maven.neoforged.net/releases/net/neoforged/%s/maven-metadata.xml";
        return reader.read(String.format(META_URL, identifier));
    }

}
