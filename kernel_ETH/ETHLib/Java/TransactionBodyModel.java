package EthSign;

import java.math.BigInteger;

public class TransactionBodyModel {

    private String from;

    private String to;

    private String gas;

    private String data;
    
    
    //HYH
    private String gasPrice;
    //private String nonce = "0x5";
    
    private BigInteger value;
   
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
    //HYH
    public String getGasPrice(){
    	return gasPrice;
    }
    public void setGasPrice(String gasPrice){
    	this.gasPrice = gasPrice;
    }
//    public String getNonce(){
//    	return nonce;
//    }
//    public void setNonce(String nonce){
//    	this.nonce = nonce;
//    }

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}


}