package com.commerce.payment.infrastructure.persistence.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class PaymentReconcileRecordData {

	private Long id;
	private LocalDate billDate;
	private String billType;
	private String downloadUrl;
	private String status;
	private String rawResponse;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public PaymentReconcileRecordData() {
	}

	public PaymentReconcileRecordData(Long id, LocalDate billDate, String billType, String downloadUrl, String status, String rawResponse, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.billDate = billDate;
		this.billType = billType;
		this.downloadUrl = downloadUrl;
		this.status = status;
		this.rawResponse = rawResponse;
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

	public LocalDate billDate() {
		return billDate;
	}

	public LocalDate getBillDate() {
		return billDate;
	}

	public void setBillDate(LocalDate billDate) {
		this.billDate = billDate;
	}

	public String billType() {
		return billType;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String downloadUrl() {
		return downloadUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String status() {
		return status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String rawResponse() {
		return rawResponse;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
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
