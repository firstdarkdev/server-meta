package dev.firstdark.servermeta.models.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HypherionSA
 * POJO representing the return format of the API
 */
@Getter
@Setter
@AllArgsConstructor
public class VersionResponse {

    String version;
    String hash;
    String url;
    String type;
    Long size;

}
