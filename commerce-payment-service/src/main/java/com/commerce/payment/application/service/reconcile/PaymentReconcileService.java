package com.commerce.payment.application.service.reconcile;

import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.domain.model.PaymentRefundQueryOptions;
import com.commerce.payment.infrastructure.persistence.PaymentReconcileDetailRepository;
import com.commerce.payment.infrastructure.persistence.PaymentReconcileDiffRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRefundRepository;
import com.commerce.payment.infrastructure.persistence.PaymentOrderEntity;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PaymentReconcileService {

	private final AlipayBillDownloader alipayBillDownloader;
	private final AlipayBillParser alipayBillParser;
	private final PaymentRepository paymentRepository;
	private final PaymentRefundRepository paymentRefundRepository;
	private final PaymentReconcileDetailRepository detailRepository;
	private final PaymentReconcileDiffRepository diffRepository;

	public PaymentReconcileService(
			AlipayBillDownloader alipayBillDownloader,
			AlipayBillParser alipayBillParser,
			PaymentRepository paymentRepository,
			PaymentRefundRepository paymentRefundRepository,
			PaymentReconcileDetailRepository detailRepository,
			PaymentReconcileDiffRepository diffRepository
	) {
		this.alipayBillDownloader = alipayBillDownloader;
		this.alipayBillParser = alipayBillParser;
		this.paymentRepository = paymentRepository;
		this.paymentRefundRepository = paymentRefundRepository;
		this.detailRepository = detailRepository;
		this.diffRepository = diffRepository;
	}

	@Transactional
	public int reconcile(LocalDate billDate, String billType, String downloadUrl) {
		if (!StringUtils.hasText(downloadUrl)) {
			return 0;
		}
		detailRepository.deleteByBill(billDate, billType);
		diffRepository.deleteByBill(billDate, billType);
		var channelLines = alipayBillParser.parse(alipayBillDownloader.download(downloadUrl));
		channelLines.forEach(line -> detailRepository.create(billDate, billType, line));
		Map<String, PaymentBillLine> channelPayments = channelLines.stream()
				.filter(line -> StringUtils.hasText(line.outTradeNo()) && !StringUtils.hasText(line.outRefundNo()))
				.collect(Collectors.toMap(PaymentBillLine::outTradeNo, Function.identity(), (left, right) -> right));
		Map<String, PaymentBillLine> channelRefunds = channelLines.stream()
				.filter(line -> StringUtils.hasText(line.outRefundNo()))
				.collect(Collectors.toMap(PaymentBillLine::outRefundNo, Function.identity(), (left, right) -> right));
		reconcilePayments(billDate, billType, channelPayments);
		reconcileRefunds(billDate, billType, channelRefunds);
		return channelLines.size();
	}

	private void reconcilePayments(LocalDate billDate, String billType, Map<String, PaymentBillLine> channelPayments) {
		Map<String, PaymentOrderEntity> localPayments = paymentRepository.query(PaymentOrderQueryOptions.none()).stream()
				.collect(Collectors.toMap(PaymentOrderEntity::outTradeNo, Function.identity(), (left, right) -> right));
		localPayments.forEach((outTradeNo, local) -> {
			PaymentBillLine channel = channelPayments.get(outTradeNo);
			if (channel == null) {
				diffRepository.create(billDate, billType, "LOCAL_ONLY", outTradeNo, null, local.status().name(), null,
						local.amount(), null, "local payment is missing from channel bill");
				return;
			}
			if (local.amount().compareTo(channel.amount()) != 0) {
				diffRepository.create(billDate, billType, "AMOUNT_MISMATCH", outTradeNo, null, local.status().name(), channel.tradeStatus(),
						local.amount(), channel.amount(), "payment amount mismatch");
			}
			if (!samePaidStatus(local.status().name(), channel.tradeStatus())) {
				diffRepository.create(billDate, billType, "STATUS_MISMATCH", outTradeNo, null, local.status().name(), channel.tradeStatus(),
						local.amount(), channel.amount(), "payment status mismatch");
			}
		});
		channelPayments.forEach((outTradeNo, channel) -> {
			if (!localPayments.containsKey(outTradeNo)) {
				diffRepository.create(billDate, billType, "CHANNEL_ONLY", outTradeNo, null, null, channel.tradeStatus(),
						null, channel.amount(), "channel payment is missing locally");
			}
		});
	}

	private void reconcileRefunds(LocalDate billDate, String billType, Map<String, PaymentBillLine> channelRefunds) {
		Map<String, PaymentRefundData> localRefunds = paymentRefundRepository.query(PaymentRefundQueryOptions.none()).stream()
				.collect(Collectors.toMap(PaymentRefundData::outRefundNo, Function.identity(), (left, right) -> right));
		localRefunds.forEach((outRefundNo, local) -> {
			PaymentBillLine channel = channelRefunds.get(outRefundNo);
			if (channel == null) {
				diffRepository.create(billDate, billType, "LOCAL_ONLY", local.outTradeNo(), outRefundNo, local.status(), null,
						local.refundAmount(), null, "local refund is missing from channel bill");
				return;
			}
			if (local.refundAmount().compareTo(Optional.ofNullable(channel.amount()).orElse(BigDecimal.ZERO)) != 0) {
				diffRepository.create(billDate, billType, "AMOUNT_MISMATCH", local.outTradeNo(), outRefundNo, local.status(), channel.tradeStatus(),
						local.refundAmount(), channel.amount(), "refund amount mismatch");
			}
		});
		channelRefunds.forEach((outRefundNo, channel) -> {
			if (!localRefunds.containsKey(outRefundNo)) {
				diffRepository.create(billDate, billType, "CHANNEL_ONLY", channel.outTradeNo(), outRefundNo, null, channel.tradeStatus(),
						null, channel.amount(), "channel refund is missing locally");
			}
		});
	}

	private boolean samePaidStatus(String localStatus, String channelStatus) {
		if (!StringUtils.hasText(channelStatus)) {
			return true;
		}
		return ("TRADE_SUCCESS".equals(localStatus) || "TRADE_FINISHED".equals(localStatus))
				&& ("TRADE_SUCCESS".equals(channelStatus) || "TRADE_FINISHED".equals(channelStatus));
	}
}
