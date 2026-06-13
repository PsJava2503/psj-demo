package com.commerce.product.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class ProductData {

	private Long id;
	private String name;
	private BigDecimal price;
	private Long skuId;
	private Boolean enabled;
	private Boolean deleted;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public ProductData() {
	}

	public ProductData(Long id, String name, BigDecimal price, Long skuId, Boolean enabled, Boolean deleted, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.skuId = skuId;
		this.enabled = enabled;
		this.deleted = deleted;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Long id() {
		return id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String name() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal price() {
		return price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long skuId() {
		return skuId;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Boolean enabled() {
		return enabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean deleted() {
		return deleted;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public ZonedDateTime createTime() {
		return createTime;
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}

	public ZonedDateTime updateTime() {
		return updateTime;
	}

	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}

}
