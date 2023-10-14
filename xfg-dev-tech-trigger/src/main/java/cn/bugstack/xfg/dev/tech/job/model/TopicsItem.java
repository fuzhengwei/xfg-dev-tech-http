package cn.bugstack.xfg.dev.tech.job.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TopicsItem{

	@SerializedName("reading_count")
	private int readingCount;

	@SerializedName("create_time")
	private String createTime;

	@SerializedName("user_specific")
	private UserSpecific userSpecific;

	@SerializedName("rewards_count")
	private int rewardsCount;

	@SerializedName("show_comments")
	private List<ShowCommentsItem> showComments;

	@SerializedName("type")
	private String type;

	@SerializedName("digested")
	private boolean digested;

	@SerializedName("likes_count")
	private int likesCount;

	@SerializedName("comments_count")
	private int commentsCount;

	@SerializedName("sticky")
	private boolean sticky;

	@SerializedName("talk")
	private Talk talk;

	@SerializedName("topic_id")
	private long topicId;

	@SerializedName("readers_count")
	private int readersCount;

	@SerializedName("group")
	private Group group;
}