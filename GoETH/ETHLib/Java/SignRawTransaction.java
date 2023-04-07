package EthSign;

import java.math.BigInteger;


public class SignRawTransaction {
	public static byte[] sign(long chainId, long nonce, String gasPrice, String gas, String ActiveContractAddress, String hash, Credentials cd){
        EthRawTransaction rt = EthRawTransaction.createTransaction(BigInteger.valueOf(chainId),BigInteger.valueOf(nonce), new BigInteger(gasPrice.substring(2), 16), new BigInteger(gas.substring(2), 16), ActiveContractAddress, hash);
        byte[] te = EncodeTransaction.signMessage(rt,(byte)chainId, cd);

		return te;
	}
	
	
	public static byte[] sign(long chainId, long nonce, String gasPrice, String gas, String ActiveContractAddress, BigInteger value, String hash, Credentials cd){
        EthRawTransaction rt = EthRawTransaction.createTransaction(BigInteger.valueOf(chainId),BigInteger.valueOf(nonce), new BigInteger(gasPrice.substring(2), 16), new BigInteger(gas.substring(2), 16), ActiveContractAddress, value, hash);
        byte[] te = EncodeTransaction.signMessage(rt,(byte)chainId, cd);

		return te;
	}
	
	
	////====private net
	@Deprecated
	public static byte[] sign(long nonce, String gasPrice, String gas, String ActiveContractAddress, String hash, Credentials cd){
        EthRawTransaction rt = EthRawTransaction.createTransaction(BigInteger.valueOf(nonce), new BigInteger(gasPrice.substring(2), 16), new BigInteger(gas.substring(2), 16), ActiveContractAddress, hash);
        byte[] te = EncodeTransaction.signMessage(rt, cd);

		return te;
	}
	
	@Deprecated
	public static byte[] sign(long nonce, String gasPrice, String gas, String ActiveContractAddress, BigInteger value, String hash, Credentials cd){
        EthRawTransaction rt = EthRawTransaction.createTransaction(BigInteger.valueOf(nonce), new BigInteger(gasPrice.substring(2), 16), new BigInteger(gas.substring(2), 16), ActiveContractAddress, value, hash);
        byte[] te = EncodeTransaction.signMessage(rt, cd);

		return te;
	}

}
