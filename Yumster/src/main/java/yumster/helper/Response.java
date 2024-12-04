package yumster.helper;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class Response {
	@Expose public String status;
	@Expose public String description;
	
	
	public Response() {
		this.status = "success";
	}
	
	public Response(String status, String description) {
		this.status = status;
		this.description = description;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}