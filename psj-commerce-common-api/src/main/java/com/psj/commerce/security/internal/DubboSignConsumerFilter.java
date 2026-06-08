package com.psj.commerce.security.internal;

import java.time.Instant;
import java.util.UUID;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

@Activate(group = CommonConstants.CONSUMER)
public class DubboSignConsumerFilter implements Filter {

	private static final String DEFAULT_SECRET = "replace-with-shared-secret";

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String callerService = resolveCallerService(invoker);
		String timestamp = String.valueOf(Instant.now().toEpochMilli());
		String nonce = UUID.randomUUID().toString();
		String canonical = DubboSigner.canonical(
				callerService,
				invoker.getInterface().getName(),
				invocation.getMethodName(),
				timestamp,
				nonce
		);
		String signature = DubboSigner.signBase64(resolveSecret(), canonical);

		invocation.setAttachment(InternalAuthConstants.ATT_SERVICE, callerService);
		invocation.setAttachment(InternalAuthConstants.ATT_TIMESTAMP, timestamp);
		invocation.setAttachment(InternalAuthConstants.ATT_NONCE, nonce);
		invocation.setAttachment(InternalAuthConstants.ATT_SIGNATURE, signature);
		return invoker.invoke(invocation);
	}

	private String resolveCallerService(Invoker<?> invoker) {
		String application = invoker.getUrl().getParameter("application");
		return (application == null || application.isBlank()) ? "unknown-service" : application;
	}

	private String resolveSecret() {
		String secretFromProperty = System.getProperty("internal.auth.secret");
		if (secretFromProperty != null && !secretFromProperty.isBlank()) {
			return secretFromProperty;
		}
		String secretFromEnv = System.getenv("INTERNAL_AUTH_SECRET");
		if (secretFromEnv != null && !secretFromEnv.isBlank()) {
			return secretFromEnv;
		}
		return DEFAULT_SECRET;
	}

}
