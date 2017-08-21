package life.pdx.dapp.sample.db.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;

import biz.pdxtech.daap.ethereum.crypto.ECKey;
import biz.pdxtech.daap.ethereum.crypto.ECKey.ECDSASignature;
import biz.pdxtech.daap.ethereum.crypto.HashUtil;
import biz.pdxtech.daap.ethereum.util.ByteUtil;

public class DBUtil {

	public static void main(String[] args) {
		System.out.println(new Date(getTimesmorning(0)));
		System.out.println(new Date(getTimesnight(0)));
		
	}
	
	// 获得当天0点时间
	public static long getTimesmorning(long timestamp) {
		Calendar cal = Calendar.getInstance();
		if (timestamp != 0) {
			cal.setTimeInMillis(timestamp);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	// 获得当天24点时间
	public static long getTimesnight(long timestamp) {
		Calendar cal = Calendar.getInstance();
		if (timestamp != 0) {
			cal.setTimeInMillis(timestamp);
		}
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	public  static String checkAddress(String address) {
		if (StringUtils.isBlank(address)) {
			return null;
		}
		if (address.startsWith("0x")) {
			address = address.substring(2);
		}
		
		String regex = "[a-f0-9A-F]{40}";
		if (Pattern.matches(regex, address)) {
			return address;
		}else {
			return null;
		}
	}
	
	/**
	 * 通过公钥获得Address
	 * 
	 * @param pubKey
	 * @return
	 */
	public static String getAddrByPubKey(byte[] pubKey) {
		ECKey key = ECKey.fromPublicOnly(pubKey);
		String address = Hex.toHexString(key.getAddress());
		return address;
	}
	
	public static byte[] sign(byte[] data, byte[] priKey) {
		byte[] hash = HashUtil.sha3(data);
		ECKey key = ECKey.fromPrivate(priKey);
		ECDSASignature eSignature = key.sign(hash);
		byte[] sigData = new byte[65]; // 1 header + 32 bytes for R + 32 bytes for S
		sigData[0] = eSignature.v;
		System.arraycopy(ByteUtil.bigIntegerToBytes(eSignature.r, 32), 0, sigData, 1, 32);
		System.arraycopy(ByteUtil.bigIntegerToBytes(eSignature.s, 32), 0, sigData, 33, 32);
		return sigData;
	}
	
	public static boolean verify(byte[] data, byte[] signature, byte[] pubKey) {
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		System.arraycopy(signature, 1, r, 0, 32);
		System.arraycopy(signature, 33, s, 0, 32);
		
		byte[] hash = HashUtil.sha3(data);
		ECDSASignature ecSignature = new ECDSASignature(ByteUtil.bytesToBigInteger(r), ByteUtil.bytesToBigInteger(s)).toCanonicalised();
		return ECKey.verify(hash, ecSignature, pubKey) ;
	}
	
	public static String getDappId(Class clazz) {
		try {
			Annotation an = clazz.getAnnotation(javax.ws.rs.Path.class);
			Class<? extends Annotation> type = an.annotationType();
			Method meth = type.getMethod("value", (Class<?>[]) null);
			Object path = meth.invoke(an, (Object[]) null);
			return (String) path;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
