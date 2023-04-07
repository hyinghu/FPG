package com.company;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.script.Script;
import org.bitcoinj.signers.LocalTransactionSigner;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletTransaction;
import org.bitcoinj.wallet.WalletTransaction.Pool;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class FIBTCSignTest {
    private String url = "http://alice:RazsEZ9NvmSxtctpS7i-bYPzS0TT-mpUP1YYveTn1gM=@127.0.0.1:58332/";
    public String urlTarget = url;
    private HostnameVerifier hostnameVerifier;
    private  URL noAuthURL = null;
    private  String authStr = null;
    private SSLSocketFactory sslSocketFactory;
    public static final Charset QUERY_CHARSET = Charset.forName("ISO8859-1");



    public static void main(String[] args) {
        FIBTCSignTest t = new FIBTCSignTest();
        t.url = "http://alice:RazsEZ9NvmSxtctpS7i-bYPzS0TT-mpUP1YYveTn1gM=@127.0.0.1:18444/";

        NetworkParameters n = NetworkParameters.fromID(NetworkParameters.ID_REGTEST);

        //String to a private key
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(n,
                "cNBvhGBigZqyYBGBSH2hhqtDLYtij5uDBR1JYnWLTDs77dQtn1dr");
//		dumpedPrivateKey = DumpedPrivateKey.fromBase58(n,
//		        "cRWTQBRpQYmbTA2ngDXSgA34GjMSXvJdhnUUSVwWBEzAaL4Z1o7M");

        ECKey key = dumpedPrivateKey.getKey();

        Wallet w = Wallet.createDeterministic(n, Script.ScriptType.P2WPKH);
        w.importKey(key);

        String fundingTxString = "020000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff04011a0101ffffffff0200f2052a0100000016001400f59654369aa40daad510afa9f32f3eb42cd6a80000000000000000266a24aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf90120000000000000000000000000000000000000000000000000000000000000000000000000";
        Transaction fundingTx = new BitcoinSerializer(n, false).makeTransaction(Hex.decode(fundingTxString));
        w.addWalletTransaction(new WalletTransaction(Pool.UNSPENT, fundingTx));

        SendRequest req = SendRequest.to(Address.fromString(n, "bcrt1qw5s6hkxm3ecz6nqzqlhamemnurwuxyz04ncya3"), Coin.parseCoin("1"));
        req.tx.addInput(fundingTx.getOutput(0));
        req.tx.setVersion(2);
        try {
            w.completeTx(req);
        } catch (InsufficientMoneyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String wtran = javax.xml.bind.DatatypeConverter.printHexBinary(req.tx.bitcoinSerialize());//byteArrayToHexString(tx.bitcoinSerialize());
        System.out.println("wtran = " + wtran);
        System.out.println(t.decodeRawtransaction(wtran));
        LinkedHashMap drt_wtran = (LinkedHashMap) t.decodeRawtransaction(wtran);
        t.printOutJson(new JSONObject(drt_wtran).toString());
        String rt_wtran = (String) t.sendRawtransaction(wtran);
        t.printOutJson("----??????????????? == " + rt_wtran);
        System.out.println("=============================????????????????????==================================");

    }



    public String sendToAddress(String toAddress, double amount, String comment, String commentTo){
        try {
            return (String) query("sendtoaddress", toAddress, amount, comment, commentTo);
        }catch(Exception e)
        {
            return e.getMessage();
        }
    }

    public Object decodeRawtransaction (String tr){
        urlTarget = url;

        try {
            return (Object) query("decoderawtransaction", tr);
        }catch(Exception e)
        {
            return e.getMessage();

        }
    }

    public Object sendRawtransaction (String tr){
        urlTarget = url;

        try {
            return (Object) query("sendrawtransaction", tr);
        }catch(Exception e)
        {
            return e.getMessage();

        }
    }

    public Object getEstimatefee (int conf_target){
        urlTarget = url;

        try {
            return (Object) query("estimatefee", conf_target);
        }catch(Exception e)
        {
            return e.getMessage();

        }
    }

    public LinkedHashMap getEstimatesmartfee (int conf_target){
        urlTarget = url;

        try {
            return (LinkedHashMap) query("estimatesmartfee", conf_target);
        }catch(Exception e)
        {
            //return e.getMessage();

            return null;
        }
    }

    public Object loadWallet(String walletName) {
        try {
            return  query("loadwallet", walletName);
        }catch(Exception e)
        {
            return e.getMessage();
        }
    }

    public BigDecimal getBalance(String walletName) throws GenericRpcException {
        urlTarget = url + "wallet/" + walletName;
        return (BigDecimal) query("getbalance");
    }

    public String getRawchangeaddress(String walletName) throws GenericRpcException {
        urlTarget = url + "wallet/" + walletName;
        return (String) query("getrawchangeaddress");
    }

    public ArrayList listUnspent(String walletName) throws GenericRpcException {
        urlTarget = url + "wallet/" + walletName;
        return (ArrayList) query("listunspent");
    }

    private  String printOutJson(String rt) {
        System.out.println("======================================================================");
        try {
            if (rt == null) {
                System.out.println(rt);
                return "";
            }


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(rt);
            String rtp = gson.toJson(je);

            System.out.println(rtp);

            return rtp;
        }catch(Exception e) {
            System.out.println("error:: " + rt);

            //e.printStackTrace();
            return "error";
        }
    }

    public Object query(String method, Object... o) throws GenericRpcException {
        HttpURLConnection conn;
        try {
            //System.out.println("urlTarget = " + urlTarget);

            URL rpc = new URL(urlTarget);
            noAuthURL = new URI(rpc.getProtocol(), null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), null).toURL();
            authStr = rpc.getUserInfo() == null ? null : String.valueOf(Base64Coder.encode(rpc.getUserInfo().getBytes(Charset.forName("ISO8859-1"))));


            conn = (HttpURLConnection) noAuthURL.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (conn instanceof HttpsURLConnection) {
                if (hostnameVerifier != null)
                    ((HttpsURLConnection) conn).setHostnameVerifier(hostnameVerifier);
                if (sslSocketFactory != null)
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            }

            ((HttpURLConnection) conn).setRequestProperty("Authorization", "Basic " + authStr);
            byte[] r = prepareRequest(method, o);
            conn.getOutputStream().write(r);
            conn.getOutputStream().close();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                InputStream errorStream = conn.getErrorStream();
                throw new BitcoinRPCException(method,
                        Arrays.deepToString(o),
                        responseCode,
                        conn.getResponseMessage(),
                        errorStream == null ? null : new String(loadStream(errorStream, true)));
            }
            return loadResponse(conn.getInputStream(), "1", true);
        } catch (IOException ex) {
            throw new BitcoinRPCException(method, Arrays.deepToString(o), ex);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] prepareRequest(final String method, final Object... params) {
        return JSON.stringify(new LinkedHashMap<String, Object>() {
            {
                put("method", method);
                put("params", params);
                put("id", "1");
            }
        }).getBytes(QUERY_CHARSET);
    }

    @SuppressWarnings("rawtypes")
    public Object loadResponse(InputStream in, Object expectedID, boolean close) throws IOException, GenericRpcException {
        try {
            String r = new String(loadStream(in, close), QUERY_CHARSET);
            try {
                Map response = (Map) JSON.parse(r);

                if (!expectedID.equals(response.get("id")))
                    throw new BitcoinRPCException("Wrong response ID (expected: " + String.valueOf(expectedID) + ", response: " + response.get("id") + ")");

                if (response.get("error") != null)
                    throw new BitcoinRPCException(new BitcoinRPCError((Map)response.get("error")));

                return response.get("result");
            } catch (ClassCastException ex) {
                throw new BitcoinRPCException("Invalid server response format (data: \"" + r + "\")");
            }
        } finally {
            if (close)
                in.close();
        }
    }


    private static byte[] loadStream(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (;;) {
            int nr = in.read(buffer);

            if (nr == -1)
                break;
            if (nr == 0)
                throw new IOException("Read timed out");

            o.write(buffer, 0, nr);
        }
        return o.toByteArray();
    }

}



class GenericRpcException extends RuntimeException {

    public GenericRpcException() {
    }

    public GenericRpcException(String msg) {
        super(msg);
    }

    public GenericRpcException(Throwable cause) {
        super(cause);
    }

    public GenericRpcException(String message, Throwable cause) {
        super(message, cause);
    }

}


class Base64Coder {

    //The line separator string of the operating system.
    private static final String systemLineSeparator = System.getProperty("line.separator");

    //Mapping table from 6-bit nibbles to Base64 characters.
    private static final char[] map1 = new char[64];
    static {
        int i=0;
        for (char c='A'; c<='Z'; c++) map1[i++] = c;
        for (char c='a'; c<='z'; c++) map1[i++] = c;
        for (char c='0'; c<='9'; c++) map1[i++] = c;
        map1[i++] = '+'; map1[i++] = '/'; }

    //Mapping table from Base64 characters to 6-bit nibbles.
    private static final byte[] map2 = new byte[128];
    static {
        for (int i=0; i<map2.length; i++) map2[i] = -1;
        for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; }

    /**
     * Encodes a string into Base64 format.
     * No blanks or line breaks are inserted.
     * @param s  A String to be encoded.
     * @return   A String containing the Base64 encoded data.
     */
    public static String encodeString (String s) {
        return new String(encode(s.getBytes())); }

    /**
     * Encodes a byte array into Base 64 format and breaks the output into lines of 76 characters.
     * This method is compatible with <code>sun.misc.BASE64Encoder.encodeBuffer(byte[])</code>.
     * @param in  An array containing the data bytes to be encoded.
     * @return    A String containing the Base64 encoded data, broken into lines.
     */
    public static String encodeLines (byte[] in) {
        return encodeLines(in, 0, in.length, 76, systemLineSeparator); }

    /**
     * Encodes a byte array into Base 64 format and breaks the output into lines.
     * @param in            An array containing the data bytes to be encoded.
     * @param iOff          Offset of the first byte in <code>in</code> to be processed.
     * @param iLen          Number of bytes to be processed in <code>in</code>, starting at <code>iOff</code>.
     * @param lineLen       Line length for the output data. Should be a multiple of 4.
     * @param lineSeparator The line separator to be used to separate the output lines.
     * @return              A String containing the Base64 encoded data, broken into lines.
     */
    public static String encodeLines (byte[] in, int iOff, int iLen, int lineLen, String lineSeparator) {
        int blockLen = (lineLen*3) / 4;
        if (blockLen <= 0) throw new IllegalArgumentException();
        int lines = (iLen+blockLen-1) / blockLen;
        int bufLen = ((iLen+2)/3)*4 + lines*lineSeparator.length();
        StringBuilder buf = new StringBuilder(bufLen);
        int ip = 0;
        while (ip < iLen) {
            int l = Math.min(iLen-ip, blockLen);
            buf.append (encode(in, iOff+ip, l));
            buf.append (lineSeparator);
            ip += l; }
        return buf.toString(); }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in  An array containing the data bytes to be encoded.
     * @return    A character array containing the Base64 encoded data.
     */
    public static char[] encode (byte[] in) {
        return encode(in, 0, in.length); }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in    An array containing the data bytes to be encoded.
     * @param iLen  Number of bytes to process in <code>in</code>.
     * @return      A character array containing the Base64 encoded data.
     */
    public static char[] encode (byte[] in, int iLen) {
        return encode(in, 0, iLen); }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in    An array containing the data bytes to be encoded.
     * @param iOff  Offset of the first byte in <code>in</code> to be processed.
     * @param iLen  Number of bytes to process in <code>in</code>, starting at <code>iOff</code>.
     * @return      A character array containing the Base64 encoded data.
     */
    public static char[] encode (byte[] in, int iOff, int iLen) {
        int oDataLen = (iLen*4+2)/3;       // output length without padding
        int oLen = ((iLen+2)/3)*4;         // output length including padding
        char[] out = new char[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;
        int op = 0;
        while (ip < iEnd) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
            int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '='; op++;
            out[op] = op < oDataLen ? map1[o3] : '='; op++; }
        return out; }

    /**
     * Decodes a string from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param s  A Base64 String to be decoded.
     * @return   A String containing the decoded data.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static String decodeString (String s) {
        return new String(decode(s)); }

    /**
     * Decodes a byte array from Base64 format and ignores line separators, tabs and blanks.
     * CR, LF, Tab and Space characters are ignored in the input data.
     * This method is compatible with <code>sun.misc.BASE64Decoder.decodeBuffer(String)</code>.
     * @param s  A Base64 String to be decoded.
     * @return   An array containing the decoded data bytes.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decodeLines (String s) {
        char[] buf = new char[s.length()];
        int p = 0;
        for (int ip = 0; ip < s.length(); ip++) {
            char c = s.charAt(ip);
            if (c != ' ' && c != '\r' && c != '\n' && c != '\t')
                buf[p++] = c; }
        return decode(buf, 0, p); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param s  A Base64 String to be decoded.
     * @return   An array containing the decoded data bytes.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode (String s) {
        return decode(s.toCharArray()); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param in  A character array containing the Base64 encoded data.
     * @return    An array containing the decoded data bytes.
     * @throws    IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode (char[] in) {
        return decode(in, 0, in.length); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param in    A character array containing the Base64 encoded data.
     * @param iOff  Offset of the first character in <code>in</code> to be processed.
     * @param iLen  Number of characters to process in <code>in</code>, starting at <code>iOff</code>.
     * @return      An array containing the decoded data bytes.
     * @throws      IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode (char[] in, int iOff, int iLen) {
        if (iLen%4 != 0) throw new IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[iOff+iLen-1] == '=') iLen--;
        int oLen = (iLen*3) / 4;
        byte[] out = new byte[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;
        int op = 0;
        while (ip < iEnd) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iEnd ? in[ip++] : 'A';
            int i3 = ip < iEnd ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");
            int o0 = ( b0       <<2) | (b1>>>4);
            int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            int o2 = ((b2 &   3)<<6) |  b3;
            out[op++] = (byte)o0;
            if (op<oLen) out[op++] = (byte)o1;
            if (op<oLen) out[op++] = (byte)o2; }
        return out; }

    //Dummy constructor.
    private Base64Coder() {}

} // end class Base64Coder




class BitcoinRPCException extends GenericRpcException {

    private String rpcMethod;
    private String rpcParams;
    private int responseCode;
    private String responseMessage;
    private String response;
    private BitcoinRPCError rpcError;

    /**
     * Creates a new instance of <code>BitcoinRPCException</code> with response
     * detail.
     *
     * @param method the rpc method called
     * @param params the parameters sent
     * @param responseCode the HTTP code received
     * @param responseMessage the HTTP response message
     * @param response the error stream received
     */
    @SuppressWarnings("rawtypes")
    public BitcoinRPCException(String method,
                               String params,
                               int    responseCode,
                               String responseMessage,
                               String response) {
        //super("RPC Query Failed (method: " + method + ", params: " + params + ", response code: " + responseCode + " responseMessage " + responseMessage + ", response: " + response);
        super(response);
        this.rpcMethod = method;
        this.rpcParams = params;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.response = response;
        if ( responseCode == 500 ) {
            // Bitcoind application error when handle the request
            // extract code/message for callers to handle
            Map error = (Map) ((Map)JSON.parse(response)).get("error");
            if ( error != null ) {
                rpcError = new BitcoinRPCError(error);
            }
        }
    }

    public BitcoinRPCException(String method, String params, Throwable cause) {
        super("RPC Query Failed (method: " + method + ", params: " + params + ")", cause);
        this.rpcMethod = method;
        this.rpcParams = params;
    }

    /**
     * Constructs an instance of <code>BitcoinRPCException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BitcoinRPCException(String msg) {
        super(msg);
    }

    public BitcoinRPCException(BitcoinRPCError error) {
        super(error.getMessage());
        this.rpcError  = error;
    }

    public BitcoinRPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getRpcMethod() {
        return rpcMethod;
    }

    public String getRpcParams() {
        return rpcParams;
    }

    /**
     * @return the HTTP response message
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @return response message from bitcored
     */
    public String getResponse() {
        return this.response;
    }

    /**
     * @return response message from bitcored
     */
    public BitcoinRPCError getRPCError() {
        return this.rpcError;
    }
}




class BitcoinRPCError {
    private int code;
    private String message;

    @SuppressWarnings({ "rawtypes" })
    public BitcoinRPCError(Map errorMap) {
        Number n = (Number) errorMap.get("code");
        this.code    = n != null ? n.intValue() : 0;
        this.message = (String) errorMap.get("message");
    }

    /**
     * get the code returned by the bitcoind.<br/>
     * some of the error codes are defined in {@link BitcoinRPCErrorCode}
     */
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}




class JSON {

    public static String stringify(Object o) {
        if (o == null)
            return "null";
        if ((o instanceof Number) || (o instanceof Boolean))
            return String.valueOf(o);
        if (o instanceof Date)
            return "new Date("+((Date)o).getTime()+")";
        if (o instanceof Map)
            return stringify((Map)o);
        if (o instanceof Iterable)
            return stringify((Iterable)o);
        if (o instanceof Object[])
            return stringify((Object[])o);
        return stringify(String.valueOf(o));
    }

    public static String stringify(Map m) {
        StringBuilder b = new StringBuilder();
        b.append('{');
        boolean first = true;
        for (Map.Entry e : ((Map<Object, Object>)m).entrySet()) {
            if (first)
                first = false;
            else
                b.append(",");
            b.append(stringify(e.getKey().toString()));
            b.append(':');
            b.append(stringify(e.getValue()));

        }
        b.append('}');
        return b.toString();
    }

    public static String stringify(Iterable c) {
        StringBuilder b = new StringBuilder();
        b.append('[');
        boolean first = true;
        for (Object o : c) {
            if (first)
                first = false;
            else
                b.append(",");
            b.append(stringify(o));
        }
        b.append(']');
        return b.toString();
    }

    public static String stringify(Object[] c) {
        StringBuilder b = new StringBuilder();
        b.append('[');
        boolean first = true;
        for (Object o : c) {
            if (first)
                first = false;
            else
                b.append(",");
            b.append(stringify(o));
        }
        b.append(']');
        return b.toString();
    }

    public static String stringify(String s) {
        StringBuilder b = new StringBuilder(s.length() + 2);
        b.append('"');
        for(; !s.isEmpty(); s = s.substring(1)) {
            char c = s.charAt(0);
            switch (c) {
                case '\t':
                    b.append("\\t");
                    break;
                case '\r':
                    b.append("\\r");
                    break;
                case '\n':
                    b.append("\\n");
                    break;
                case '\f':
                    b.append("\\f");
                    break;
                case '\b':
                    b.append("\\b");
                    break;
                case '"':
                case '\\':
                    b.append("\\");
                    b.append(c);
                    break;
                default:
                    b.append(c);
            }
        }
        b.append('"');
        return b.toString();
    }

    public static Object parse(String s) {
        return CrippledJavaScriptParser.parseJSExpr(s);
    }

}




class CrippledJavaScriptParser {

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isIdStart(char ch) {
        return ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isId(char ch) {
        return isDigit(ch) || ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private static Object parseJSString(StringParser jsString, char delim) {
        StringBuilder b = new StringBuilder();
        while ((!jsString.isEmpty())) {
            char sc = jsString.poll();
            if (sc == '\\') {
                char cc = jsString.poll();
                switch (cc) {
                    case 't':
                        b.append('\t');
                        break;
                    case 'r':
                        b.append('\r');
                        break;
                    case 'n':
                        b.append('\n');
                        break;
                    case 'f':
                        b.append('\f');
                        break;
                    case 'b':
                        b.append('\b');
                        break;
                    case 'u':
                        try {
                            char ec = (char) Integer.parseInt(jsString.peek(4), 16);
                            b.append(ec);
                            jsString.forward(4);
                        } catch (NumberFormatException ex) {
                            b.append("\\u");
                        }
                        break;
                    default:
                        b.append(cc);
                }
            } else if (sc == delim) {
                break;
            } else {
                b.append(sc);
            }
        }
        return b.toString();
    }

    private static List parseJSArray(StringParser jsArray) {
        ArrayList rv = new ArrayList();
        jsArray.trim();
        if (jsArray.peek()==']') {
            jsArray.forward(1);
            return rv;
        }
        while (!jsArray.isEmpty()) {
            rv.add(parseJSExpr(jsArray));
            jsArray.trim();
            if (!jsArray.isEmpty()) {
                char ch = jsArray.poll();
                if (ch == ']')
                    return rv;
                if (ch != ',')
                    throw new RuntimeException(jsArray.toString());
                jsArray.trim();
            }
        }
        return rv;
    }

    private static String parseId(StringParser jsId) {
        StringBuilder b = new StringBuilder();
        b.append(jsId.poll());
        char ch;
        while (isId(ch = jsId.peek())) {
            b.append(ch);
            jsId.forward(1);
        }
        return b.toString();
    }

    private static HashMap parseJSHash(StringParser jsHash) {
        LinkedHashMap rv = new LinkedHashMap();
        jsHash.trim();
        if (jsHash.peek()=='}') {
            jsHash.forward(1);
            return rv;
        }
        while (!jsHash.isEmpty()) {
            Object key;
            if (isIdStart(jsHash.peek())) {
                key = parseId(jsHash);
            } else {
                key = parseJSExpr(jsHash);
            }
            jsHash.trim();
            if (!jsHash.isEmpty()) {
                if (jsHash.peek() != ':')
                    throw new RuntimeException(jsHash.toString());
                jsHash.forward(1);
                jsHash.trim();
            } else
                throw new IllegalArgumentException();
            Object value = parseJSExpr(jsHash);
            jsHash.trim();
            if (!jsHash.isEmpty()) {
                char ch = jsHash.poll();
                if (ch == '}') {
                    rv.put(key, value);
                    return rv;
                }
                if (ch != ',')
                    throw new RuntimeException(jsHash.toString());
                jsHash.trim();
            }
            rv.put(key, value);
        }
        return rv;
    }

    static class Keyword {
        public final String keyword;
        public final Object value;

        public final char firstChar;
        public final String keywordFromSecond;

        public Keyword(String keyword, Object value) {
            this.keyword = keyword;
            this.value = value;
            firstChar = keyword.charAt(0);
            keywordFromSecond = keyword.substring(1);
        }
    }
    private static Keyword[] keywords = {
            new Keyword("null", null),
            new Keyword("true", Boolean.TRUE),
            new Keyword("false", Boolean.FALSE),
    };

    public static Object parseJSExpr(StringParser jsExpr) {
        if (jsExpr.isEmpty())
            throw new IllegalArgumentException();
        jsExpr.trim();
        char start = jsExpr.poll();
        if (start == '[')
            return parseJSArray(jsExpr);
        if (start == '{')
            return parseJSHash(jsExpr);
        if (start == '\'' || start == '\"')
            return parseJSString(jsExpr, start);
        if (isDigit(start) || start == '-' || start == '+') {
            StringBuilder b = new StringBuilder();
            if (start != '+')
                b.append(start);
            char sc, psc = 0;
            boolean exp = false;
            boolean dot = false;
            for(;;) {
                if (jsExpr.isEmpty())
                    break;
                sc = jsExpr.peek();
                if (!isDigit(sc)) {
                    if (sc == 'E' || sc == 'e') {
                        if (exp)
                            throw new NumberFormatException(b.toString() + jsExpr.toString());
                        exp = true;
                    } else if (sc == '.') {
                        if (dot || exp)
                            throw new NumberFormatException(b.toString() + jsExpr.toString());
                        dot = true;
                    } else if ((sc == '-' || sc == '+') && (psc == 'E' || psc == 'e')) {
                    } else
                        break;
                }

                b.append(sc);
                jsExpr.forward(1);

                psc = sc;
            }
            return dot || exp ? (Object)new BigDecimal(b.toString()) : (Object)Long.parseLong(b.toString());
        }
        for (Keyword keyword : keywords) {
            int keywordlen = keyword.keywordFromSecond.length();
            if (start == keyword.firstChar && jsExpr.peek(keywordlen).equals(keyword.keywordFromSecond)) {
                if (jsExpr.length() == keyword.keywordFromSecond.length()) {
                    jsExpr.forward(keyword.keywordFromSecond.length());
                    return keyword.value;
                }
                if (!isId(jsExpr.charAt(keyword.keywordFromSecond.length()))) {
                    jsExpr.forward(keyword.keywordFromSecond.length());
                    jsExpr.trim();
                    return keyword.value;
                } else {
                    throw new IllegalArgumentException(jsExpr.toString());
                }
            }
        }
        if (start == 'n' && jsExpr.peek("ew Date(".length()).equals("ew Date(")) {
            jsExpr.forward("ew Date(".length());
            Number date = (Number) parseJSExpr(jsExpr);
            jsExpr.trim();
            if (jsExpr.poll() != ')')
                throw new RuntimeException("Invalid date");
            return new Date(date.longValue());
        }
        throw new UnsupportedOperationException("Unparsable javascript expression: \""+start+jsExpr+"\"");
    }

    public static Object parseJSExpr(String jsExpr) {
        return parseJSExpr(new StringParser(jsExpr));
    }

    public static LinkedHashMap<String, Object> parseJSVars(String javascript) {
        try {
            BufferedReader r = new BufferedReader(new StringReader(javascript));
            LinkedHashMap<String, Object> rv = new LinkedHashMap();
            String l;
            while ((l = r.readLine()) != null) {
                l = l.trim();
                if (l.isEmpty() || !l.startsWith("var"))
                    continue;
                l = l.substring(3).trim();
                int i = l.indexOf('=');
                if (i == -1)
                    continue;
                String varName = l.substring(0, i).trim();
                String expr = l.substring(i + 1).trim();
                if (expr.endsWith(";"))
                    expr = expr.substring(0, expr.length() - 1).trim();
                rv.put(varName, parseJSExpr(expr));
            }
            return rv;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}





class StringParser {

    private String string;
    int index;

    public int length(){
        return string.length()-index;
    }

    public StringParser(String string) {
        this.string = string;
        index = 0;
    }

    public void forward(int chars) {
        index += chars;
    }

    public char poll() {
        char c = string.charAt(index);
        forward(1);
        return c;
    }

    public String poll(int length) {
        String str = string.substring(index, length+index);
        forward(length);
        return str;
    }

    private void commit(){
        string = string.substring(index);
        index = 0;
    }

    public String pollBeforeSkipDelim(String s) {
        commit();
        int i = string.indexOf(s);
        if (i == -1)
            throw new RuntimeException("\"" + s + "\" not found in \"" + string + "\"");
        String rv = string.substring(0, i);
        forward(i + s.length());
        return rv;
    }

    public char peek() {
        return string.charAt(index);
    }

    public String peek(int length) {
        return string.substring(index, length+index);
    }

    public String trim() {
        commit();
        return string = string.trim();
    }

    public char charAt(int pos) {
        return string.charAt(pos+index);
    }

    public boolean isEmpty() {
        return (string.length()<=index);
    }

    @Override
    public String toString() {
        commit();
        return string;
    }

}