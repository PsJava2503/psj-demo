package com.commerce.payment.infrastructure.alipay;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class AlipaySignUtils {

	private AlipaySignUtils() {}

	public static String signRsa2(Map<String, String> params, String privateKey) {
		try {
			String content = canonicalContent(params);
			PrivateKey key = parsePrivateKey(privateKey);
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(key);
			signature.update(content.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(signature.sign());
		} catch (Exception ex) {
			throw new IllegalStateException("failed to sign alipay request", ex);
		}
	}

	public static boolean verifyRsa2(Map<String, String> params, String sign, String publicKey) {
		try {
			String content = canonicalContent(params);
			PublicKey key = parsePublicKey(publicKey);
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(key);
			signature.update(content.getBytes(StandardCharsets.UTF_8));
			return signature.verify(Base64.getDecoder().decode(sign));
		} catch (Exception ex) {
			return false;
		}
	}

	public static String canonicalContent(Map<String, String> params) {
		List<Map.Entry<String, String>> entries = new ArrayList<>(params.entrySet());
		entries.sort(Comparator.comparing(Map.Entry::getKey));
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : entries) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null || value.isBlank() || "sign".equals(key)) {
				continue;
			}
			if (!builder.isEmpty()) {
				builder.append("&");
			}
			builder.append(key).append("=").append(value);
		}
		return builder.toString();
	}

	private static PrivateKey parsePrivateKey(String pemKey) throws Exception {
		String normalized = pemKey
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
		byte[] keyBytes = Base64.getDecoder().decode(normalized);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
	}

	private static PublicKey parsePublicKey(String pemKey) throws Exception {
		String normalized = pemKey
				.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");
		byte[] keyBytes = Base64.getDecoder().decode(normalized);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		return KeyFactory.getInstance("RSA").generatePublic(keySpec);
	}
}
