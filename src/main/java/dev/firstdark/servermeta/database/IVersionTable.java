package dev.firstdark.servermeta.database;

import dev.firstdark.servermeta.models.api.VersionResponse;

/**
 * @author HypherionSA
 * Helper Interfact to convert tables to the correct format for API requests
 */
public interface IVersionTable {

    VersionResponse toVersionResponse();

}
