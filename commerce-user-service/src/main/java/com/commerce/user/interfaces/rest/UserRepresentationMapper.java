package com.commerce.user.interfaces.rest;

import com.commerce.user.UserAddressSlotResponse;
import com.commerce.user.UserResponse;
import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserAddressSlot;

final class UserRepresentationMapper {

	private UserRepresentationMapper() {
	}

	static UserResponse toResponse(User user) {
		return new UserResponse(
				user.id(),
				user.firstName(),
				user.secondName(),
				user.phone(),
				user.email(),
				user.defaultAddressSlotId(),
				user.roles(),
				user.addressSlots().stream()
						.map(UserRepresentationMapper::toResponse)
						.toList(),
				user.enabled(),
				user.deleted(),
				user.createTime(),
				user.updateTime()
		);
	}

	private static UserAddressSlotResponse toResponse(UserAddressSlot slot) {
		return new UserAddressSlotResponse(
				slot.id(),
				slot.userId(),
				slot.addressId(),
				slot.slotName(),
				slot.deleted(),
				slot.createTime()
		);
	}

}
