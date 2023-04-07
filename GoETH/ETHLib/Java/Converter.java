package EthSign;

import java.math.BigInteger;
import java.util.ArrayList;

public class Converter {
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static Converter converter = null;

    public Converter() {

    }

    public static Converter converterInstance() {
        if (converter == null) converter = new Converter();
        return converter;
    }

    public String stringToHex(String input) {
        if (input == null) throw new NullPointerException();

        return asHex(input.getBytes());
    }

    public String hexToString(String txtInHex) {
        byte[] txtInByte = new byte[txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2) {
            txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }

        return new String(txtInByte);
    }

    private String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }

        return new String(chars);
    }

    public String arrayOfStringToHex(ArrayList array) {
        String encode = "";
        for (Object element : array) {
            encode += addZero(stringToHex(String.valueOf(element)), 'l');
        }
        return encode;
    }

    public String arrayOfAddress(ArrayList array) {
        String encode = "";
        for (Object element : array) {
            encode += addZero(String.valueOf(element).substring(2), 'r');
        }
        return encode;
    }
    
    public ArrayList decode_uint256_arr(String hex) {
        ArrayList list = new ArrayList();
        String[] hexString = hex.substring(2).split("(?<=\\G.{" + 64 + "})");

        for (String element : hexString) {

           		//list.add(Long.parseLong(element, 16));
           		list.add(new BigInteger(element, 16));
        	
        }

        //list.subList(0, 2).clear();
        return list;
    }
    
    public ArrayList decode_bytes32_arr(String hex) {
        ArrayList list = new ArrayList();
        String[] hexString = hex.substring(2).split("(?<=\\G.{" + 64 + "})");
//System.out.println(hexString.length);
        for (String element : hexString) {
    		if (element.equals(hexString[0]) || element.equals(hexString[1]) || element.equals(hexString[2]))
    		{
    			//System.out.println("111 222 : " + element);
				continue;
    		}

        		list.add(element);
    			//System.out.println("-------: " + element);

        }

        //list.subList(0, 3).clear();
        return list;
    }

    public String addZero(String str, char side) {
        int finally_lenght = 64;
        int leng = str.length();
        int zero_count = finally_lenght - leng;
        String sero_str = "";

        for (int i = 0; i < zero_count; i++) {
            sero_str += "0";
        }
        if (side == 'l') {
            return str + sero_str;
        } else {
            return sero_str + str;
        }
    }

    public String integerToHex(Integer number) {
        Integer i = new Integer(number);
        String hex = Integer.toHexString(i);
        return hex;
    }

}