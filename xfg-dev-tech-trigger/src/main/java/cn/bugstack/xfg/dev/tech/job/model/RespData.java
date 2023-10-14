package cn.bugstack.xfg.dev.tech.job.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class RespData{

	@SerializedName("topics")
	private List<TopicsItem> topics;
}