package com.degreedean.payments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StubPaymentPort implements PaymentPort {
    private final PaymentIntentRepository repository;

    public StubPaymentPort(PaymentIntentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentReceipt create(UUID userId, String productCode, BigDecimal amount) {
        PaymentIntent intent = new PaymentIntent();
        intent.setId(UUID.randomUUID());
        intent.setUserId(userId);
        intent.setProductCode(productCode);
        intent.setAmount(amount);
        intent.setCurrency("USD");
        intent.setStatus("STUB");
        intent.setProvider("NONE");
        intent.setCreatedAt(Instant.now());
        repository.save(intent);
        return new PaymentReceipt(intent.getId(), productCode, amount, "STUB");
    }
}

@Entity
@Table(name = "payment_intents")
class PaymentIntent {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "product_code", nullable = false)
    private String productCode;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency = "USD";
    @Column(nullable = false)
    private String status = "STUB";
    @Column(nullable = false)
    private String provider = "NONE";
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

interface PaymentIntentRepository extends JpaRepository<PaymentIntent, UUID> {}
