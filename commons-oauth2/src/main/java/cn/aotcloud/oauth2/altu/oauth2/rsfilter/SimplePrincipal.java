package cn.aotcloud.oauth2.altu.oauth2.rsfilter;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

/**
 * 接口 {@link Principal} 的简单实现。支持 Principal 的扩展属性Map，并且返回一个不可修改的属性Map对象。
 * 
 * @author xkxu
 */
public class SimplePrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 对象 Principal 的唯一标识
	 */
    private final String id;

    /**
     * 对象 Principal 支持的属性集合
     */
    private Map<String, Object> attributes;

    public SimplePrincipal(final String id) {
        this(id, null);
    }

    public SimplePrincipal(final String id, final Map<String, Object> attributes) {
        Assert.notNull(id, "用户ID已清空，请重新登录");
        this.id = id;
        this.attributes = attributes;
    }
    
    public final String getId() {
        return this.id;
    }
    
    /**
     * @return 返回一个不可修改的属性Map对象
     */
    public Map<String, Object> getAttributes() {
        return this.attributes == null
                ? Collections.<String, Object>emptyMap()
                : Collections.unmodifiableMap(this.attributes);
    }
    
    public String toString() {
        return this.id;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePrincipal other = (SimplePrincipal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return id;
	}

}
