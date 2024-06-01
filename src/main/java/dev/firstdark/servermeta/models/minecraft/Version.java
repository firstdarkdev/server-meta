package dev.firstdark.servermeta.models.minecraft;

import com.google.gson.annotations.SerializedName;

/**
 * @author HypherionSA
 * POJO representing a Minecraft Version
 */
@SuppressWarnings("unused")
public class Version {

    @SerializedName("complianceLevel")
    public Long mComplianceLevel;
    @SerializedName("id")
    public String mId;
    @SerializedName("releaseTime")
    public String mReleaseTime;
    @SerializedName("sha1")
    public String mSha1;
    @SerializedName("time")
    public String mTime;
    @SerializedName("type")
    public String mType;
    @SerializedName("url")
    public String mUrl;

}