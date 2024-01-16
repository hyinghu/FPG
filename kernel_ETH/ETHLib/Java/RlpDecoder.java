package EthSign;

import java.util.ArrayList;


public class RlpDecoder {


    public static int OFFSET_SHORT_STRING = 0x80;

    public static int OFFSET_LONG_STRING = 0xb7;

    public static int OFFSET_SHORT_LIST = 0xc0;

    public static int OFFSET_LONG_LIST = 0xf7;


    public static RlpList decode(byte[] rlpEncoded) {
        RlpList rlpList = new RlpList(new ArrayList());
        traverse(rlpEncoded, 0, rlpEncoded.length, rlpList);
        return rlpList;
    }

    private static void traverse(byte[] data, int startPos, int endPos, RlpList rlpList) {

        try {
            if (data == null || data.length == 0) {
                return;
            }

            while (startPos < endPos) {

                int prefix = data[startPos] & 0xff;

                if (prefix < OFFSET_SHORT_STRING) {

 
                    byte[] rlpData = {(byte) prefix};
                    rlpList.getValues().add(RlpString.create(rlpData));
                    startPos += 1;

                } else if (prefix == OFFSET_SHORT_STRING) {


                    rlpList.getValues().add(RlpString.create(new byte[0]));
                    startPos += 1;

                } else if (prefix > OFFSET_SHORT_STRING && prefix <= OFFSET_LONG_STRING) {

                    byte strLen = (byte) (prefix - OFFSET_SHORT_STRING);

                    byte[] rlpData = new byte[strLen];
                    System.arraycopy(data, startPos + 1, rlpData, 0, strLen);

                    rlpList.getValues().add(RlpString.create(rlpData));
                    startPos += 1 + strLen;

                } else if (prefix > OFFSET_LONG_STRING && prefix < OFFSET_SHORT_LIST) {

                    byte lenOfStrLen = (byte) (prefix - OFFSET_LONG_STRING);
                    int strLen = calcLength(lenOfStrLen, data, startPos);

                    byte[] rlpData = new byte[strLen];
                    System.arraycopy(data, startPos + lenOfStrLen + 1, rlpData, 0, strLen);

                    rlpList.getValues().add(RlpString.create(rlpData));
                    startPos += lenOfStrLen + strLen + 1;

                } else if (prefix >= OFFSET_SHORT_LIST && prefix <= OFFSET_LONG_LIST) {
                    byte listLen = (byte) (prefix - OFFSET_SHORT_LIST);

                    RlpList newLevelList = new RlpList(new ArrayList());
                    traverse(data, startPos + 1, startPos + listLen + 1, newLevelList);
                    rlpList.getValues().add(newLevelList);

                    startPos += 1 + listLen;

                } else if (prefix > OFFSET_LONG_LIST) {

                    byte lenOfListLen = (byte) (prefix - OFFSET_LONG_LIST);
                    int listLen = calcLength(lenOfListLen, data, startPos);

                    RlpList newLevelList = new RlpList(new ArrayList());
                    traverse(data, startPos + lenOfListLen + 1,
                            startPos + lenOfListLen + listLen + 1, newLevelList);
                    rlpList.getValues().add(newLevelList);

                    startPos += lenOfListLen + listLen + 1;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("RLP wrong encoding", e);
        }
    }

    private static int calcLength(int lengthOfLength, byte[] data, int pos) {
        byte pow = (byte) (lengthOfLength - 1);
        int length = 0;
        for (int i = 1; i <= lengthOfLength; ++i) {
            length += (data[pos + i] & 0xff) << (8 * pow);
            pow--;
        }
        return length;
    }

}
