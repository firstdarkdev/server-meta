package dev.firstdark.servermeta.models.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class MetaResponse {
    List<String> gameVersions;
    HashMap<String, List<String>> versions;
}
