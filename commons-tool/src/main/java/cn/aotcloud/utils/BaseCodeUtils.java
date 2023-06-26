package cn.aotcloud.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import cn.aotcloud.logger.LoggerHandle;

public class BaseCodeUtils {

	protected static LoggerHandle logger = new LoggerHandle(BaseCodeUtils.class);
	
	public static final String CHARSET = "UTF-8";

	public static final String IAMGE_FORMAT = "png";
	
	public static ByteArrayInputStream convertBase64ToImage(String imageBase64) {
		if(StringUtils.startsWith(imageBase64, "data:")) { //data:imgage/jpg;base64或者data:img/jpg;base64
			imageBase64 = StringUtils.substringAfterLast(imageBase64, ";base64,");
			imageBase64 = RegExUtils.replacePattern(imageBase64, "\r\n", "");
			imageBase64 = StringUtils.replace(imageBase64, " ", "").trim();
        }
    	byte[] bytes = Base64.decodeBase64(imageBase64);
    	return new ByteArrayInputStream(bytes);
    }
	
	public static String convertImageToBase64(ByteArrayInputStream inputStream) {
        try {
			return Base64.encodeBase64String(StreamUtils.copyToByteArray(inputStream));
		} catch (IOException e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
        
        return null;
    }
	
	public static String convertImageToBase64(BufferedImage image) {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, IAMGE_FORMAT, baos);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        byte[] bytes = baos.toByteArray();//转换成字节
        IOUtils.closeQuietly(baos);
        
        return Base64.encodeBase64String(bytes);
    }
	
}
