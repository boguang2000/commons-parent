package cn.aotcloud.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.util.Base64Utils;

import cn.aotcloud.utils.RandomStringUtils;

public class UUIDGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateUpperCase() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
    
    public static String generateToken() {
    	return Base64Utils.encodeToString(generate().getBytes());
    }
    
    public static String generatorSecret() {
    	return RandomStringUtils.randomAlphanumeric(64);
    }
    
    public static String generatorSecret(int length) {
    	return RandomStringUtils.randomAlphanumeric(length);
    }
    
    public static String generatorCode(int length) {
    	return RandomStringUtils.randomNumeric(length);
    }
    
    public static String generatorB64Secret() {
    	String secret = UUIDGenerator.generate();
    	byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    	return Base64Utils.encodeToString(secretBytes);
    }
}
