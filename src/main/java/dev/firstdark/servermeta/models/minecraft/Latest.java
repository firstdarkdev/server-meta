package dev.firstdark.servermeta.models.minecraft;

import com.google.gson.annotations.SerializedName;

/**
 * @author HypherionSA
 * POJO representing the Latest Minecraft Versions
 */
@SuppressWarnings("unused")
public class Latest {

    @SerializedName("release")
    public String mRelease;
    @SerializedName("snapshot")
    public String mSnapshot;
}