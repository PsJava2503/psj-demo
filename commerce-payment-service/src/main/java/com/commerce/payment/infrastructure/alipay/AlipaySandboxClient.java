package com.commerce.payment.infrastructure.alipay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class AlipaySandboxClient {

	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final RestTemplate restTemplate;
	private final AlipaySandboxProperties properties;
	private final ObjectMapper objectMapper;

	public AlipaySandboxClient(RestTemplate alipayRestTemplate, AlipaySandboxProperties properties, ObjectMapper objectMapper) {
		this.restTemplate = alipayRestTemplate;
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	public AlipayGatewayResponse precreate(String outTradeNo, BigDecimal amount, String subject) {
		Map<String, Object> biz = new HashMap<>();
		biz.put("out_trade_no", outTradeNo);
		biz.put("total_amount", amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
		biz.put("subject", subject);
		return call("alipay.trade.precreate", biz, true);
	}

	public AlipayGatewayResponse refund(String outTradeNo, BigDecimal refundAmount, String outRefundNo, String reason) {
		Map<String, Object> biz = new HashMap<>();
		biz.put("out_trade_no", outTradeNo);
		biz.put("refund_amount", refundAmount.toPlainString());
		biz.put("out_request_no", outRefundNo);
		biz.put("refund_reason", reason);
		return call("alipay.trade.refund", biz, false);
	}

	public AlipayGatewayResponse query(String outTradeNo) {
		Map<String, Object> biz = new HashMap<>();
		biz.put("out_trade_no", outTradeNo);
		return call("alipay.trade.query", biz, false);
	}

	public AlipayGatewayResponse close(String outTradeNo) {
		Map<String, Object> biz = new HashMap<>();
		biz.put("out_trade_no", outTradeNo);
		return call("alipay.trade.close", biz, false);
	}

	public AlipayGatewayResponse downloadBill(LocalDate billDate, String billType) {
		Map<String, Object> biz = new HashMap<>();
		biz.put("bill_type", billType);
		biz.put("bill_date", billDate.toString());
		return call("alipay.data.dataservice.bill.downloadurl.query", biz, false);
	}

	public boolean verifyNotifySignature(Map<String, String> formParams) {
		String sign = formParams.get("sign");
		if (!StringUtils.hasText(sign) || !StringUtils.hasText(properties.getAlipayPublicKey())) {
			return false;
		}
		return AlipaySignUtils.verifyRsa2(formParams, sign, properties.getAlipayPublicKey());
	}

	private AlipayGatewayResponse call(String method, Map<String, Object> bizContent, boolean includeNotifyUrl) {
		if (!enabled()) {
			return AlipayGatewayResponse.disabled("alipay sandbox config not set");
		}

		try {
			Map<String, String> params = new LinkedHashMap<>();
			params.put("app_id", properties.getAppId());
			params.put("method", method);
			params.put("format", "JSON");
			params.put("charset", "utf-8");
			params.put("sign_type", "RSA2");
			params.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
			params.put("version", "1.0");
			params.put("biz_content", objectMapper.writeValueAsString(bizContent));
			if (includeNotifyUrl && StringUtils.hasText(properties.getNotifyUrl())) {
				params.put("notify_url", properties.getNotifyUrl());
			}
			params.put("sign", AlipaySignUtils.signRsa2(params, properties.getAppPrivateKey()));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			Map<String, String> bodyParams = new LinkedHashMap<>(params);
			bodyParams.remove("charset");
			HttpEntity<String> request = new HttpEntity<>(toFormBody(bodyParams), headers);
			ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrlWithCharset(), request, String.class);
			return parseResponse(method, response.getBody());
		} catch (RestClientResponseException ex) {
			return AlipayGatewayResponse.failed(
					"HTTP_" + ex.getStatusCode().value(),
					"Alipay sandbox gateway returned " + ex.getStatusCode().value() + " " + ex.getStatusText(),
					ex.getResponseBodyAsString()
			);
		} catch (Exception ex) {
			return AlipayGatewayResponse.failed("CALL_FAILED", ex.getMessage(), "{}");
		}
	}

	private String gatewayUrlWithCharset() {
		String gatewayUrl = properties.getGatewayUrl();
		if (gatewayUrl.contains("charset=")) {
			return gatewayUrl;
		}
		return gatewayUrl + (gatewayUrl.contains("?") ? "&" : "?") + "charset=utf-8";
	}

	private AlipayGatewayResponse parseResponse(String method, String responseBody) {
		try {
			Map<String, Object> root = objectMapper.readValue(responseBody, new TypeReference<>() {});
			String responseKey = method.replace(".", "_") + "_response";
			Object obj = root.get(responseKey);
			if (!(obj instanceof Map<?, ?> map)) {
				return AlipayGatewayResponse.failed("PARSE_FAILED", "missing response payload", responseBody);
			}
			Map<String, Object> payload = new HashMap<>();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				payload.put(String.valueOf(entry.getKey()), entry.getValue());
			}
			String code = str(payload.get("code"));
			String msg = str(payload.get("sub_msg"));
			if (!StringUtils.hasText(msg)) {
				msg = str(payload.get("msg"));
			}
			boolean success = "10000".equals(code);
			return new AlipayGatewayResponse(success, code, msg, payload, responseBody);
		} catch (Exception ex) {
			return AlipayGatewayResponse.failed("PARSE_FAILED", ex.getMessage(), responseBody);
		}
	}

	private boolean enabled() {
		return StringUtils.hasText(properties.getGatewayUrl())
				&& StringUtils.hasText(properties.getAppId())
				&& StringUtils.hasText(properties.getAppPrivateKey())
				&& StringUtils.hasText(properties.getAlipayPublicKey());
	}

	private String toFormBody(Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (!builder.isEmpty()) {
				builder.append("&");
			}
			builder.append(encode(entry.getKey())).append("=").append(encode(entry.getValue()));
		}
		return builder.toString();
	}

	private String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	private String str(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	public record AlipayGatewayResponse(
			boolean success,
			String code,
			String message,
			Map<String, Object> payload,
			String rawBody
	) {
		public static AlipayGatewayResponse disabled(String message) {
			return new AlipayGatewayResponse(false, "SANDBOX_DISABLED", message, Map.of(), "{}");
		}

		public static AlipayGatewayResponse failed(String code, String message, String rawBody) {
			return new AlipayGatewayResponse(false, code, message, Map.of(), rawBody);
		}
	}
}
