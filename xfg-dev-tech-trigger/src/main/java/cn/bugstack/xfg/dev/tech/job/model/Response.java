package cn.bugstack.xfg.dev.tech.job.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Response{

	@SerializedName("resp_data")
	private RespData respData;

	@SerializedName("succeeded")
	private boolean succeeded;
}