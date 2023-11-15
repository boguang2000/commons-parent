package cn.aotcloud.entity;

import java.io.Serializable;
import java.util.Map;

public class EntryPackageProxy implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String secretScan;
    
    private Map<String, Object> secretResource;

	public String getSecretScan() {
		return secretScan;
	}

	public void setSecretScan(String secretScan) {
		this.secretScan = secretScan;
	}

	public Map<String, Object> getSecretResource() {
		return secretResource;
	}

	public void setSecretResource(Map<String, Object> secretResource) {
		this.secretResource = secretResource;
	}

}
