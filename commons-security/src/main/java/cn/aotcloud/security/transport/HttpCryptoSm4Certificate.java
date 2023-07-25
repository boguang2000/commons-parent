package cn.aotcloud.security.transport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import cn.aotcloud.crypto.sm.SM4TextEncryptor;
import cn.aotcloud.crypto.sm.SMImplMode;
import cn.aotcloud.crypto.sm.delegate.SMCryptoFactory;

/**
 * @author xkxu
 */
public class HttpCryptoSm4Certificate {

    private String secretKey = "58b1463a76ca4bbc95fc1e255bbc9109";
    
    private String secretIv = "58b1463a76ca4bbc95fc1e255bbc9109";

    private TextEncryptor textEncryptor;

    public HttpCryptoSm4Certificate() {
    }

    public HttpCryptoSm4Certificate(String secretKey) {
        this.secretKey = secretKey;
        this.secretIv = null;
    }
    
    public HttpCryptoSm4Certificate(String secretKey, String secretIv) {
        this.secretKey = secretKey;
        this.secretIv = secretIv;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        this.secretIv = null;
    }
    
    public void setSecretKey(String secretKey, String secretIv) {
        this.secretKey = secretKey;
        this.secretIv = secretIv;
    }

    public void setTextEncryptor(TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }

    public TextEncryptor getTextEncryptor() {
        if (textEncryptor == null) {
        	if(StringUtils.isBlank(this.secretIv)) {
        		this.textEncryptor = SMCryptoFactory.createSM4TextEncryptor(secretKey, SMImplMode.java);
        	} else {
        		this.textEncryptor = SMCryptoFactory.createSM4TextEncryptor(secretKey, secretIv, SM4TextEncryptor.SM4_CBC, SMImplMode.java);
        	}
        }
        return textEncryptor;
    }
}
