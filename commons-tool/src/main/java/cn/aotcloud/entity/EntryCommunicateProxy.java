package cn.aotcloud.entity;

import java.io.Serializable;

public class EntryCommunicateProxy implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String phone;
	
	private String mobile;
	
	private String telephony;
	
	private String email;

	private String address;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPh() {
		return phone;
	}

	public void setPh(String ph) {
		this.phone = ph;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobl() {
		return mobile;
	}

	public void setMobl(String mobl) {
		this.mobile = mobl;
	}
	
	public String getTelephony() {
		return telephony;
	}

	public void setTelephony(String telephony) {
		this.telephony = telephony;
	}

	public String getTph() {
		return telephony;
	}

	public void setTph(String tph) {
		this.telephony = tph;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEml() {
		return email;
	}

	public void setEml(String eml) {
		this.email = eml;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAdrs() {
		return address;
	}

	public void setAdrs(String adrs) {
		this.address = adrs;
	}
}
