package com.zhcong.Code;

import com.zhcong.lgnbjut.Values;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java implementation for the Time-based One-Time Password (TOTP) two factor authentication algorithm. To get this to
 * work you:
 * 
 * <ol>
 * <li>Use generateBase32Secret() to generate a secret key for a user.</li>
 * <li>Store the secret key in the database associated with the user account.</li>
 * <li>Display the QR image URL returned by qrImageUrl(...) to the user.</li>
 * <li>User uses the image to load the secret key into his authenticator application.</li>
 * </ol>
 * 
 * <p>
 * Whenever the user logs in:
 * </p>
 * 
 * <ol>
 * <li>The user enters the number from the authenticator application into the login form.</li>
 * <li>Read the secret associated with the user account from the database.</li>
 * <li>The server compares the user input with the output from generateCurrentNumber(...).</li>
 * <li>If they are equal then the user is allowed to log in.</li>
 * </ol>
 * 
 * <p>
 * See: https://github.com/j256/two-factor-auth
 * </p>
 * 
 * <p>
 * For more details about this magic algorithm, see: http://en.wikipedia.org/wiki/Time-based_One-time_Password_Algorithm
 * </p>
 * 
 * @author graywatson
 */
//源代码出自https://github.com/j256/two-factor-auth
//做了矫正的调整，以适用于没有服务器的环境

/**
 * 安全性：最重要的是时间，当扫描二维码时攻击者记录下时间，破解会变得非常简单
 * 另外，我做的调整，就是加上了矫正步长也在一定程度上降低了安全性，
 */
public class TOTP {

	public static class value{
		public long v;
		public String vs;
		public long step;
		public value(long v,long step){this.v=v;this.step=step;}
		public value(String v,long step){this.vs=v;this.step=step;}
	}
	/** default time-step which is part of the spec, Values.QRtime seconds is default */
	public static final int DEFAULT_TIME_STEP_SECONDS = Values.QRtime;
	/** set to the number of digits to control 0 prefix, set to 0 for no prefix */
	private static int NUM_DIGITS_OUTPUT = 6;

	private static final String blockOfZeros;
	private static final value number;

	static {
		char[] chars = new char[NUM_DIGITS_OUTPUT];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = '0';
		}
		blockOfZeros = new String(chars);
		number = new value("0",0);
	}


	//产生用来共享的种子
	public static String generateBase32Secret() {
		return generateBase32Secret(16);
	}
	//同上
	public static String generateBase32Secret(int length) {
		StringBuilder sb = new StringBuilder(length);
		Random random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int val = random.nextInt(32);
			if (val < 26) {
				sb.append((char) ('A' + val));
			} else {
				sb.append((char) ('2' + (val - 26)));
			}
		}
		return sb.toString();
	}

	public static value generateCurrentMAKENumberString(String base32Secret) throws GeneralSecurityException {
		generateMAKENumberString(base32Secret, System.currentTimeMillis()/1000, DEFAULT_TIME_STEP_SECONDS);
		return number;
	}

	public static String generateCurrentCHKNumberString(String base32Secret,long redress) throws GeneralSecurityException {
		return generateCHKNumberString(base32Secret, System.currentTimeMillis()/1000, DEFAULT_TIME_STEP_SECONDS,redress);
	}


	public static value generateMAKENumberString(String base32Secret, long timeMillis, int timeStepSeconds)
			throws GeneralSecurityException {
		generateMAKENumber(base32Secret, timeMillis, timeStepSeconds);
		number.vs=zeroPrepend(number.v, NUM_DIGITS_OUTPUT);
		return number;
	}

	public static String generateCHKNumberString(String base32Secret, long timeMillis, int timeStepSeconds,long redress)
			throws GeneralSecurityException {
		long number = generateCHKNumber(base32Secret, timeMillis, timeStepSeconds,redress);
		return zeroPrepend(number, NUM_DIGITS_OUTPUT);
	}

	public static value generateMAKENumber(String base32Secret, long timeMillis, int timeStepSeconds)
			throws GeneralSecurityException {

		byte[] key = decodeBase32(base32Secret);

		byte[] data = new byte[8];
		//矫正
		long redress=timeMillis % timeStepSeconds;
		long value = (timeMillis-redress) / timeStepSeconds;
		for (int i = 7; value > 0; i--) {
			data[i] = (byte) (value & 0xFF);
			value >>= 8;
		}

		// encrypt the data with the key and return the SHA1 of it in hex
		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		// if this is expensive, could put in a thread-local
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		// take the 4 least significant bits from the encrypted string as an offset
		int offset = hash[hash.length - 1] & 0xF;

		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = offset; i < offset + 4; ++i) {
			truncatedHash <<= 8;
			// get the 4 bytes at the offset
			truncatedHash |= (hash[i] & 0xFF);
		}
		// cut off the top bit
		truncatedHash &= 0x7FFFFFFF;

		// the token is then the last 6 digits in the number
		truncatedHash %= 1000000;

		number.step=redress;
		number.v=truncatedHash;
		return number;
	}

	public static long generateCHKNumber(String base32Secret, long timeMillis, int timeStepSeconds, long redress)
			throws GeneralSecurityException {

		byte[] key = decodeBase32(base32Secret);

		byte[] data = new byte[8];
		long value = (timeMillis-redress) / timeStepSeconds;
		for (int i = 7; value > 0; i--) {
			data[i] = (byte) (value & 0xFF);
			value >>= 8;
		}

		// encrypt the data with the key and return the SHA1 of it in hex
		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		// if this is expensive, could put in a thread-local
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		// take the 4 least significant bits from the encrypted string as an offset
		int offset = hash[hash.length - 1] & 0xF;

		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = offset; i < offset + 4; ++i) {
			truncatedHash <<= 8;
			// get the 4 bytes at the offset
			truncatedHash |= (hash[i] & 0xFF);
		}
		// cut off the top bit
		truncatedHash &= 0x7FFFFFFF;

		// the token is then the last 6 digits in the number
		truncatedHash %= 1000000;

		return truncatedHash;
	}


	/**
	 * Return the string prepended with 0s. Tested as 10x faster than String.format("%06d", ...); Exposed for testing.
	 */
	static String zeroPrepend(long num, int digits) {
		String numStr = Long.toString(num);
		if (numStr.length() >= digits) {
			return Long.toString(num);
		} else {
			StringBuilder sb = new StringBuilder(digits);
			int zeroCount = digits - numStr.length();
			sb.append(blockOfZeros, 0, zeroCount);
			sb.append(numStr);
			return sb.toString();
		}
	}

	/**
	 * Decode base-32 method. I didn't want to add a dependency to Apache Codec just for this decode method. Exposed for
	 * testing.
	 */
	static byte[] decodeBase32(String str) {
		// each base-32 character encodes 5 bits
		int numBytes = ((str.length() * 5) + 7) / 8;
		byte[] result = new byte[numBytes];
		int resultIndex = 0;
		int which = 0;
		int working = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			int val;
			if (ch >= 'a' && ch <= 'z') {
				val = ch - 'a';
			} else if (ch >= 'A' && ch <= 'Z') {
				val = ch - 'A';
			} else if (ch >= '2' && ch <= '7') {
				val = 26 + (ch - '2');
			} else if (ch == '=') {
				// special case
				which = 0;
				break;
			} else {
				throw new IllegalArgumentException("Invalid base-32 character: " + ch);
			}
			/*
			 * There are probably better ways to do this but this seemed the most straightforward.
			 */
			switch (which) {
				case 0:
					// all 5 bits is top 5 bits
					working = (val & 0x1F) << 3;
					which = 1;
					break;
				case 1:
					// top 3 bits is lower 3 bits
					working |= (val & 0x1C) >> 2;
					result[resultIndex++] = (byte) working;
					// lower 2 bits is upper 2 bits
					working = (val & 0x03) << 6;
					which = 2;
					break;
				case 2:
					// all 5 bits is mid 5 bits
					working |= (val & 0x1F) << 1;
					which = 3;
					break;
				case 3:
					// top 1 bit is lowest 1 bit
					working |= (val & 0x10) >> 4;
					result[resultIndex++] = (byte) working;
					// lower 4 bits is top 4 bits
					working = (val & 0x0F) << 4;
					which = 4;
					break;
				case 4:
					// top 4 bits is lowest 4 bits
					working |= (val & 0x1E) >> 1;
					result[resultIndex++] = (byte) working;
					// lower 1 bit is top 1 bit
					working = (val & 0x01) << 7;
					which = 5;
					break;
				case 5:
					// all 5 bits is mid 5 bits
					working |= (val & 0x1F) << 2;
					which = 6;
					break;
				case 6:
					// top 2 bits is lowest 2 bits
					working |= (val & 0x18) >> 3;
					result[resultIndex++] = (byte) working;
					// lower 3 bits of byte 6 is top 3 bits
					working = (val & 0x07) << 5;
					which = 7;
					break;
				case 7:
					// all 5 bits is lower 5 bits
					working |= (val & 0x1F);
					result[resultIndex++] = (byte) working;
					which = 0;
					break;
			}
		}
		if (which != 0) {
			result[resultIndex++] = (byte) working;
		}
		if (resultIndex != result.length) {
			result = Arrays.copyOf(result, resultIndex);
		}
		return result;
	}

}
