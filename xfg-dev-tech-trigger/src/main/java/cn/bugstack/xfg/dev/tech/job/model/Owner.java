package cn.bugstack.xfg.dev.tech.job.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Owner{

	@SerializedName("avatar_url")
	private String avatarUrl;

	@SerializedName("user_id")
	private long userId;

	@SerializedName("name")
	private String name;

	@SerializedName("location")
	private String location;
}