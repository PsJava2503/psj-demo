package com.psj.commerce.security.internal;

public final class InternalAuthConstants {

	public static final String ATT_SERVICE = "x-internal-service";
	public static final String ATT_TIMESTAMP = "x-internal-ts";
	public static final String ATT_NONCE = "x-internal-nonce";
	public static final String ATT_SIGNATURE = "x-internal-sign";
	public static final long DEFAULT_ALLOWED_SKEW_MS = 5 * 60_000L;

	private InternalAuthConstants() {
	}

}
