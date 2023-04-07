package EthSign;


import java.math.BigInteger;

public class EthRawTransaction {

    private BigInteger chainId;
    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String to;
    private BigInteger value;
    private String data;

    protected EthRawTransaction(BigInteger chainId, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                           BigInteger value, String data) {
        this.chainId = chainId;
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.to = to;
        this.value = value;

        if (data != null) {
            this.data = Numeric.cleanHexPrefix(data);
        }
    }

    //===private net
    @Deprecated
    protected EthRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
		            BigInteger value, String data) {
		this.chainId = BigInteger.valueOf(0);
		this.nonce = nonce;
		this.gasPrice = gasPrice;
		this.gasLimit = gasLimit;
		this.to = to;
		this.value = value;
		
		if (data != null) {
		this.data = Numeric.cleanHexPrefix(data);
		}
	}

    @Deprecated 
    protected EthRawTransaction(BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data) {
		this.gasPrice = gasPrice;
		this.gasLimit = gasLimit;
		this.to = to;
		this.value = value;
		
		if (data != null) {
		this.data = Numeric.cleanHexPrefix(data);
		}
    }

    public static EthRawTransaction createTransaction(
    		BigInteger chainId,BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, String data) {
        return createTransaction(chainId, nonce, gasPrice, gasLimit, to, BigInteger.ZERO, data);
    }

    public static EthRawTransaction createTransaction(
    		BigInteger chainId,BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data) {
    	//System.out.println( gasLimit.longValue() * gasPrice.longValue() + value.longValue());
        return new EthRawTransaction(chainId, nonce, gasPrice, gasLimit, to, value, data);
    }

    //===private net
    @Deprecated
    public static EthRawTransaction createTransaction(
    		BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, String data) {
        return createTransaction(nonce, gasPrice, gasLimit, to, BigInteger.ZERO, data);
    }
    @Deprecated
    public static EthRawTransaction createTransaction(
    		BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data) {
    	//System.out.println( gasLimit.longValue() * gasPrice.longValue() + value.longValue());
        return new EthRawTransaction(nonce, gasPrice, gasLimit, to, value, data);
    }

    
    
    public BigInteger getChainId() {
        return chainId;
    }
    public BigInteger getNonce() {
        return nonce;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public String getData() {
        return data;
    }
}
