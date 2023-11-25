package cn.aotcloud.mybatis.plus;

import java.io.Serializable;

public class Column implements Serializable {
    private static final long serialVersionUID = 1L;
	private String name;
	private Class<?> type;
    private String typeName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
    
}
