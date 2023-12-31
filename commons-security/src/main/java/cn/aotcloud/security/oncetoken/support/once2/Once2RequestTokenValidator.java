package cn.aotcloud.security.oncetoken.support.once2;

import cn.aotcloud.crypto.EncryptionProperties;
import cn.aotcloud.crypto.pcode.PcodeEncoder;
import cn.aotcloud.crypto.sm.SM2TextEncryptor;
import cn.aotcloud.security.oncetoken.IllegalRequestTokenException;
import cn.aotcloud.security.oncetoken.OnceProtocol;
import cn.aotcloud.security.oncetoken.RequestToken;
import cn.aotcloud.security.oncetoken.RequestTokenStore;
import cn.aotcloud.security.oncetoken.support.simple.SimpleRequestTokenValidator;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xkxu
 */
public class Once2RequestTokenValidator extends SimpleRequestTokenValidator {

    private SM2TextEncryptor sm2TextEncryptor;

    public Once2RequestTokenValidator(RequestTokenStore requestTokenStore,
                                      PcodeEncoder pscodeEncoder,
                                      EncryptionProperties encryptionProperties,
                                      long timeinterval,
                                      String requestTokenSalt) {
        super(requestTokenStore, pscodeEncoder, timeinterval, requestTokenSalt);
        this.sm2TextEncryptor = new SM2TextEncryptor(encryptionProperties);
    }

    @Override
    public boolean support(RequestToken requestToken) {
        return requestToken != null
                && StringUtils.equalsIgnoreCase(requestToken.getProtocol(), OnceProtocol.once2.name());
    }

    @Override
    protected void isValidSign(RequestToken requestTokenFromRequest) throws IllegalRequestTokenException {
        String sign = sm2TextEncryptor.decrypt(requestTokenFromRequest.getSign());
        requestTokenFromRequest.setSign(sign);
        super.isValidSign(requestTokenFromRequest);
    }

    @Override
    protected String getRequestTokenAsStr(RequestToken requestTokenFromRequest) {
        return super.getRequestTokenAsStr(requestTokenFromRequest) + "," + requestTokenSalt;
    }
}
