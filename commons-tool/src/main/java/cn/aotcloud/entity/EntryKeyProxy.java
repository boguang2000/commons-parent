package cn.aotcloud.entity;

import java.io.Serializable;

public class EntryKeyProxy implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getK() {
		return key;
	}

	public void setK(String k) {
		this.key = k;
	}
}
