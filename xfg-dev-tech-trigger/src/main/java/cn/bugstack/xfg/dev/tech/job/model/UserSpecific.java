package cn.bugstack.xfg.dev.tech.job.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserSpecific{

	@SerializedName("subscribed")
	private boolean subscribed;

	@SerializedName("liked")
	private boolean liked;
}