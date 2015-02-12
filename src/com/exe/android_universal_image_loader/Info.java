package com.exe.android_universal_image_loader;

public class Info {
	private String imgurl;
	private String id;
	private String datetime;
	private String client;
	private String message;

	public Info(String imgurl, String id, String datetime, String client,
			String message) {
		this.imgurl = imgurl;
		this.id = id;
		this.datetime = datetime;
		this.client = client;
		this.message = message;

	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
