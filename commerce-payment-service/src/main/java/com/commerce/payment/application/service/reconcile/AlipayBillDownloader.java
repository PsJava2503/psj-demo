package com.commerce.payment.application.service.reconcile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AlipayBillDownloader {

	private final RestTemplate restTemplate = new RestTemplate();

	public String download(String downloadUrl) {
		byte[] body = restTemplate.getForObject(downloadUrl, byte[].class);
		if (body == null || body.length == 0) {
			return "";
		}
		if (isZip(body)) {
			return unzipFirstTextFile(body);
		}
		return new String(body, StandardCharsets.UTF_8);
	}

	private boolean isZip(byte[] body) {
		return body.length > 3 && body[0] == 'P' && body[1] == 'K';
	}

	private String unzipFirstTextFile(byte[] body) {
		try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(body), StandardCharsets.UTF_8)) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					return new String(zip.readAllBytes(), StandardCharsets.UTF_8);
				}
			}
			return "";
		} catch (Exception ex) {
			throw new IllegalStateException("failed to unzip alipay bill", ex);
		}
	}
}
