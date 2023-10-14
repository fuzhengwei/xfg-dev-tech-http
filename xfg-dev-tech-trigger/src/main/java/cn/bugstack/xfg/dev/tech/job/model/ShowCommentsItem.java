package cn.bugstack.xfg.dev.tech.job.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ShowCommentsItem{

	@SerializedName("owner")
	private Owner owner;

	@SerializedName("likes_count")
	private int likesCount;

	@SerializedName("create_time")
	private String createTime;

	@SerializedName("rewards_count")
	private int rewardsCount;

	@SerializedName("sticky")
	private boolean sticky;

	@SerializedName("text")
	private String text;

	@SerializedName("comment_id")
	private long commentId;
}