package com.commerce.address;

public record AddressResponse(
		Long id,
		Long userId,
		String recipientName,
		String phone,
		String province,
		String city,
		String district,
		String detail,
		Boolean defaultAddress
) {
	public String fullAddress() {
		return String.join(" ", province, city, district, detail);
	}
}
