package com.example.demo.utils;

import org.bouncycastle.crypto.engines.SM2Engine;

/**
 * 简单单例SM2加解密, 配合前端
 */
public class LoSM2 {

    private static String ECNameModel = "sm2p256v1";
    private static SM2Engine.Mode CipherMode = SM2Engine.Mode.C1C3C2;
    private final String privateKey;
    private final String publicKey;

    private LoSM2(String privateKey, String publicKey){
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    private volatile static LoSM2 instance = null;

    public static LoSM2 getInstance(){
        if(instance == null){
            throw new RuntimeException("请InitKey初始化密钥!!!");
        }
        return instance;
    }

    /**
     * SM2初始密钥(私钥,公钥)
     * @param privateKey
     * @param publicKey
     * @return
     */
    public static LoSM2 InitKey(String privateKey, String publicKey) {
        if(instance == null){
            synchronized(LoSM2.class){
                if(instance == null){
                    instance = new LoSM2(privateKey, publicKey);
                }
            }
        }
        // 这里可以放心地访问 instance.privateKey，因为此时 instance 已经不是 null 了。
        System.out.println(instance.privateKey);
        return instance;
    }

    private static class SM2SimpSelfLoader {
        private static final LoSM2 instance = InitKey("", "");
    }

    private static boolean IsInitKey(){
        if(instance == null) {
            throw new RuntimeException("请InitKey初始化密钥!!!");
        }else {
            return true;
        }
    }

    /**
     * SM2加密
     * @param cleartext          明文数据
     */
    public String encrypt(String cleartext) {
        if(!IsInitKey()){
            return "";

        }
        return SimpSM2Util.encrypt(instance.publicKey, cleartext);
    }

    /**
     * SM2解密
     * @param cipherData        密文数据
     */
    public static String decrypt(String cipherData) {
        if(!IsInitKey()){
            System.out.println(instance);
            System.out.println(IsInitKey());
            return "";
        }
        return SimpSM2Util.decrypt(instance.privateKey, cipherData);
    }

}