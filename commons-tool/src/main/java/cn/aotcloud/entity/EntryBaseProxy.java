package cn.aotcloud.entity;

import java.io.Serializable;

public class EntryBaseProxy implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String getSct() {
		return secret;
	}

	public void setSct(String sct) {
		this.secret = sct;
	}
	
}
