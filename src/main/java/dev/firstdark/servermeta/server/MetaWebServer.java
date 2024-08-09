package dev.firstdark.servermeta.server;

import dev.firstdark.servermeta.MetaServer;
import dev.firstdark.servermeta.database.DatabaseController;
import dev.firstdark.servermeta.database.IVersionTable;
import dev.firstdark.servermeta.database.tables.*;
import dev.firstdark.servermeta.models.api.VersionResponse;
import dev.firstdark.servermeta.utils.MinecraftVersionConverter;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Header;
import io.javalin.plugin.bundled.CorsPluginConfig;
import lombok.Getter;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * @author HypherionSA
 * Main API logic
 */
public class MetaWebServer {

    public static final MetaWebServer INSTANCE = new MetaWebServer();

    private final HashMap<String, Class<?>> routes = new LinkedHashMap<>() {{
        put("fabric", FabricVersionsTable.class);
        put("forge", ForgeVersionsTable.class);
        put("quilt", QuiltVersionsTable.class);
        put("vanilla", MinecraftVersionsTable.class);
        put("paper", PaperVersionsTable.class);
        put("neoforge", NeoForgeVersionsTable.class);
    }};

    @Getter
    private final Javalin javalin;

    protected MetaWebServer() {
        javalin = Javalin.create(c -> c.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost)));
        javalin.before(ctx -> ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*").header(Header.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept"));
        javalin.routes(() -> path("v1/{platform}", () -> {
            get("/", this::getAllVersions);
            get("/{version}", this::getVersion);
        }));
    }

    private void getVersion(Context context) {
        String platform = context.pathParam("platform");
        Class<?> t = routes.get(platform);

        try {
            IVersionTable result = (IVersionTable) DatabaseController.INSTANCE.getDB().findById(context.pathParam("version"), t);
            context.json(result.toVersionResponse());
            return;
        } catch (Exception ignored) {}

        context.status(404);
    }

    private void getAllVersions(Context context) {
        String platform = context.pathParam("platform");
        Class<?> t = routes.get(platform);

        List<VersionResponse> responses = new ArrayList<>(DatabaseController.INSTANCE.getDB().getCollection(t).stream().map(tt -> ((IVersionTable) tt).toVersionResponse()).toList());

        // Note here: Minecraft, Fabric and Quilt versions CAN be unordered in the Database.
        // We sort them here to make sure they are in the correct order
        responses.sort(Comparator.comparing(v -> {
            if (platform.equalsIgnoreCase("vanilla"))
                return MinecraftVersionConverter.parse(v.getVersion());

            if (platform.equalsIgnoreCase("fabric") || platform.equalsIgnoreCase("quilt")) {
                String mc = v.getVersion().split("\\+")[0];
                String ver = v.getVersion().split("\\+")[1];

                DefaultArtifactVersion dav = MinecraftVersionConverter.parse(mc);
                return new DefaultArtifactVersion(dav + "+" + ver);
            }

            return new DefaultArtifactVersion(v.getVersion());
        }));

        // Reverse the entries, so they are from new to old
        Collections.reverse(responses);

        context.json(responses);
    }

    public void start(int port) {
        MetaServer.LOGGER.info("Starting API Server on port {}", port);
        javalin.start(port);
    }

}
