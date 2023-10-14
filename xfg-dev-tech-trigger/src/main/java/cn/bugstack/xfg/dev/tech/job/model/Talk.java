package cn.bugstack.xfg.dev.tech.job.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Talk{

	@SerializedName("owner")
	private Owner owner;

	@SerializedName("text")
	private String text;
}