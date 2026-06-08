package com.psj.commerce.security.internal;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class DubboSigner {

	private static final String HMAC_SHA256 = "HmacSHA256";

	private DubboSigner() {
	}

	public static String canonical(
			String serviceName,
			String interfaceName,
			String methodName,
			String timestamp,
			String nonce
	) {
		return String.join(
				"\n",
				nullToEmpty(serviceName),
				nullToEmpty(interfaceName),
				nullToEmpty(methodName),
				nullToEmpty(timestamp),
				nullToEmpty(nonce)
		);
	}

	public static String signBase64(String secret, String canonical) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA256);
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
			byte[] digest = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(digest);
		}
		catch (Exception e) {
			throw new IllegalStateException("dubbo internal sign failed", e);
		}
	}

	public static boolean safeEquals(String a, String b) {
		if (a == null || b == null || a.length() != b.length()) {
			return false;
		}
		int result = 0;
		for (int i = 0; i < a.length(); i++) {
			result |= a.charAt(i) ^ b.charAt(i);
		}
		return result == 0;
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

}
