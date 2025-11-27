package com.example.demo.utils;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;


/**
 * 简单的sm2
 */
public class SimpSM2Util {

	/**
	 * SM2加密算法
	 * @param publicKey     公钥
	 * @param data          明文数据
	 * @return
	 */
	public static String encrypt(String publicKey, String data) {
		// 获取一条SM2曲线参数
		X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
		// 构造ECC算法参数，曲线方程、椭圆曲线G点、大整数N
		ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
		//提取公钥点
		ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(publicKey));
		// 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
		ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);

		SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
		// 设置sm2为加密模式
		sm2Engine.init(true, new ParametersWithRandom(publicKeyParameters, new SecureRandom()));

		byte[] arrayOfBytes = null;
		try {
			byte[] in = data.getBytes();
			arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
		} catch (Exception e) {
			System.out.println("SM2加密时出现异常:"+e.getMessage());
		}
		return Hex.toHexString(arrayOfBytes);

	}

	/**
	 * SM2解密算法
	 * @param privateKey        私钥
	 * @param cipherData        密文数据
	 * @return
	 */
	public static String decrypt(String privateKey, String cipherData) {
		// 使用BC库加解密时密文以04开头，传入的密文前面没有04则补上
		if (!cipherData.startsWith("04")){
			cipherData = "04" + cipherData;
		}
		byte[] cipherDataByte = Hex.decode(cipherData);
		BigInteger privateKeyD = new BigInteger(privateKey, 16);
		//获取一条SM2曲线参数
		X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
		//构造domain参数
		ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
		ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);

		SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
		// 设置sm2为解密模式
		sm2Engine.init(false, privateKeyParameters);

		String result = "";
		try {
			byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
			return new String(arrayOfBytes);
		} catch (Exception e) {
			System.out.println("SM2解密时出现异常:"+e.getMessage());
		}
		return result;
	}

	@Test
	//生成密钥
	public void createKey() throws Exception{
		//String M="encryption standard111111111111111111111111111111";
		SimpSM2Util sm2 = new SimpSM2Util();
		ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
		// 获取一个椭圆曲线类型的密钥对生成器
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
		// 使用SM2参数初始化生成器
		kpg.initialize(sm2Spec);
		// 获取密钥对
		KeyPair keyPair = kpg.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		BCECPublicKey p=(BCECPublicKey)publicKey;
		System.out.println("publicKey："+Hex.toHexString(p.getQ().getEncoded(false)));
		PrivateKey privateKey = keyPair.getPrivate();
		BCECPrivateKey s=(BCECPrivateKey)privateKey;
		System.out.println("privateKey："+Hex.toHexString(s.getD().toByteArray()));
	}

	@Test
	//生成密钥
	public void testKey() throws Exception{
		String privateKey = "335e43d2cca18385833e2fd8ddbef1a11798a40167460cfbebdbf2b112b54e64";
		String publicKey = "0408e226cfdb9b6377516164e0228e8bce5c538d88d138b409a807090caf77b0159e5950df46e7df57fd09fe979cd5bc045a5e05ac3af7c157ebd9ee9693cad8d1";
		System.out.println("privateKey"+privateKey);

		LoSM2.InitKey(privateKey, publicKey);

		String encrypt = LoSM2.decrypt("63697b9a399140b93a72e14f99b8993934cf60bd33e4a827f4da5f66c407c1304f0bab656bd47b9187bdc42e8d6a1388e1b451538310168297d0ec94109a0cd46640889fdab6b5d4ecdbcf321b928d993c9eda86ccf7538663c852a9fb18e882afdcac1c253db39bd91eb2efc2e0");
		System.out.println("解码密码："+encrypt);
	}
}