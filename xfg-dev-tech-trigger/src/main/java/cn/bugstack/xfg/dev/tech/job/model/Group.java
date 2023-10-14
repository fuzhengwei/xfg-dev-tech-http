package cn.bugstack.xfg.dev.tech.job.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Group{

	@SerializedName("group_id")
	private long groupId;

	@SerializedName("background_url")
	private String backgroundUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("type")
	private String type;
}