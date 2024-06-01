package dev.firstdark.servermeta.models.minecraft;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

/**
 * @author HypherionSA
 * POJO representing the Minecraft Version Manifest
 */
@SuppressWarnings("unused")
public class MinecraftManifest {

    @SerializedName("latest")
    public Latest mLatest;
    @SerializedName("versions")
    public LinkedList<Version> mVersions;

}