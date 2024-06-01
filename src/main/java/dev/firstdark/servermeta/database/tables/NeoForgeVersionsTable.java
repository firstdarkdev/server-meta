package dev.firstdark.servermeta.database.tables;

import dev.firstdark.servermeta.database.IVersionTable;
import dev.firstdark.servermeta.models.api.VersionResponse;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HypherionSA
 * Database POJO to handle NeoForge Versions
 */
@Getter
@Setter
@Document(collection = "neoforge", schemaVersion = "1.0")
public class NeoForgeVersionsTable implements IVersionTable {

    @Id
    private String version;
    private String downloadUrl;
    private String hash;
    private long size;
    private String type;

    @Override
    public VersionResponse toVersionResponse() {
        return new VersionResponse(getVersion(), getHash(), getDownloadUrl(), getType(), getSize());
    }

}
