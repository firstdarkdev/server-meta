package dev.firstdark.servermeta.database;

import dev.firstdark.servermeta.database.tables.*;
import io.jsondb.JsonDBTemplate;
import io.jsondb.annotation.Document;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author HypherionSA
 * Controller class to handle database set up and operations
 */
public class DatabaseController {

    public static final DatabaseController INSTANCE = new DatabaseController();
    private final JsonDBTemplate jsonDBTemplate;
    private final Set<Class<?>> tables = new LinkedHashSet<>();

    protected DatabaseController() {
        jsonDBTemplate = new JsonDBTemplate("data", "dev.firstdark.servermeta.database.tables");

        tables.add(MinecraftVersionsTable.class);
        tables.add(PaperVersionsTable.class);
        tables.add(ForgeVersionsTable.class);
        tables.add(NeoForgeVersionsTable.class);
        tables.add(FabricVersionsTable.class);
        tables.add(QuiltVersionsTable.class);

        jsonDBTemplate.setupDB(tables);
        this.setupTables();
    }

    protected void setupTables() {
        for (Class<?> table : tables) {
            if (!jsonDBTemplate.collectionExists(table)) {
                jsonDBTemplate.createCollection(table);
                jsonDBTemplate.reloadCollection(table.getAnnotation(Document.class).collection());
            }
        }
    }

    public void insertData(Object o) {
        jsonDBTemplate.insert(o);
        jsonDBTemplate.reloadCollection(o.getClass().getAnnotation(Document.class).collection());
    }

    public JsonDBTemplate getDB() {
        return this.jsonDBTemplate;
    }
}
