package net.oschina.app.v2.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.umeng.update.UpdateResponse;

public class Version implements Parcelable {

	public static final String BUNDLE_KEY_VERSION = "bundle_key_version";
	public boolean delta;
	public boolean hasUpdate;
	public String new_md5;
	public String origin;
	public String patch_md5;
	public String path;
	public String proto_ver;
	public String size;
	public String target_size;
	public String updateLog;
	public String version;

	public Version() {
	}

	public Version(UpdateResponse updateInfo) {
		delta = updateInfo.delta;
		hasUpdate = updateInfo.hasUpdate;
		new_md5 = updateInfo.new_md5;
		origin = updateInfo.origin;
		patch_md5 = updateInfo.patch_md5;
		path = updateInfo.path;
		proto_ver = updateInfo.proto_ver;
		size = updateInfo.size;
		target_size = updateInfo.target_size;
		updateLog = updateInfo.updateLog;
		version = updateInfo.version;
	}

	public UpdateResponse toVersion() {
		try {
			JSONObject json = new JSONObject();
			json.put("delta", delta);
			json.put("hasUpdate", hasUpdate);
			json.put("new_md5", new_md5);
			json.put("origin", origin);
			json.put("patch_md5", patch_md5);
			json.put("path", path);
			json.put("proto_ver", proto_ver);
			json.put("size", size);
			json.put("target_size", target_size);
			json.put("updateLog", updateLog);
			json.put("version", version);
			return new UpdateResponse(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Version(Parcel source) {
		delta = source.readByte() == 1;
		hasUpdate = source.readByte() == 1;
		new_md5 = source.readString();
		origin = source.readString();
		patch_md5 = source.readString();
		path = source.readString();
		proto_ver = source.readString();
		size = source.readString();
		target_size = source.readString();
		updateLog = source.readString();
		version = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(delta ? (byte) 1 : 0);
		dest.writeByte(hasUpdate ? (byte) 1 : 0);
		dest.writeString(new_md5);
		dest.writeString(origin);
		dest.writeString(patch_md5);
		dest.writeString(path);
		dest.writeString(proto_ver);
		dest.writeString(size);
		dest.writeString(target_size);
		dest.writeString(updateLog);
		dest.writeString(version);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Version> CREATOR = new Creator<Version>() {

		@Override
		public Version[] newArray(int size) {
			return new Version[size];
		}

		@Override
		public Version createFromParcel(Parcel source) {
			return new Version(source);
		}
	};
}
