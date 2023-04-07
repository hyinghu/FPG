package EthSign;

import java.math.BigInteger;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;

public class ECDSASignature {
    public final BigInteger r;
    public final BigInteger s;
    X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");


    public ECDSASignature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    public ECDSASignature toCanonicalised() {
	        ECDomainParameters CURVE = new ECDomainParameters(
	                CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
	        BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);
	        
	     if (!(s.compareTo(HALF_CURVE_ORDER) <= 0)){
            return new ECDSASignature(r, CURVE.getN().subtract(s));
        } else {
            return this;
        }
    }
}
