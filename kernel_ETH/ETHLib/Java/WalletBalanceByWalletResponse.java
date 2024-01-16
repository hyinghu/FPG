package EthSign;

import java.math.BigDecimal;
import java.math.BigInteger;

public class WalletBalanceByWalletResponse {
    private String token_name;
    private BigDecimal amount;
    private BigDecimal frozen_amount;
    private BigInteger fraction_base;

    public WalletBalanceByWalletResponse(String token_name, BigDecimal amount, BigDecimal frozen_amount, BigInteger fraction_base) {
        this.token_name = token_name;
        this.amount = amount;
        this.frozen_amount = frozen_amount;
        this.fraction_base = fraction_base;
    }

    public String getToken_name() {
        return token_name;
    }

    public void setToken_name(String token_name) {
        this.token_name = token_name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFrozen_amount() {
        return frozen_amount;
    }

    public void setFrozen_amount(BigDecimal frozen_amount) {
        this.frozen_amount = frozen_amount;
    }

    public BigInteger getFraction_base() {
        return fraction_base;
    }

    public void setFraction_base(BigInteger fraction_base) {
        this.fraction_base = fraction_base;
    }
}