package com.degreedean.payments;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Class 2 stub. Stripe checkout is out of scope.
 */
public interface PaymentPort {
    PaymentReceipt create(UUID userId, String productCode, BigDecimal amount);

    record PaymentReceipt(UUID id, String productCode, BigDecimal amount, String status) {}
}
