package jp.co.fmap.nfcportal;

import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/01.
 */

public class NFCCommand {
  public int length;
  public String cmdType;
  public String idm;
  public String[] systemCodes;
  public int blockCount;
  public byte blockIndexLengthType = BLOCK_INDEX_LENGTH_2;
  public byte blockAccessMode = BLOCK_ACCESS_MODE_CACHE_EXCLUDE;
  public int systemOrderIndex = 0;

  public static byte BLOCK_INDEX_LENGTH_2 = 0b1;
  public static byte BLOCK_INDEX_LENGTH_3 = 0b0;
  public static byte BLOCK_ACCESS_MODE_CACHE = 0b001;
  public static byte BLOCK_ACCESS_MODE_CACHE_EXCLUDE = 0b0000;

  public byte[] toBytes() {
    StringBuffer cmdBuf = new StringBuffer();
    cmdBuf.append(cmdType);
    cmdBuf.append(idm);
    cmdBuf.append(StringUtil.hexString(systemCodes.length));
    for (String systemCode:systemCodes) {
      cmdBuf.append(systemCode);
    }
    cmdBuf.append(StringUtil.hexString(blockCount));
    for (int i = 0; i < blockCount; i ++) {
      cmdBuf.append(StringUtil.hexString(blockIndexLengthType << 7 | blockAccessMode << 4 | systemOrderIndex));
      cmdBuf.append(StringUtil.hexString(i));
    }
    length = cmdBuf.length() / 2 + 1;
    cmdBuf.insert(0, StringUtil.hexString(length));

    return StringUtil.parseToByte(cmdBuf.toString());
  }

//  private byte[] makeCmd(String cmd) {
//    byte[] dataWithoutLength = StringUtil.parseToByte(cmd);
//    int length = dataWithoutLength.length;
//    byte[] data = new byte[length + 1];
//    data[0] = (byte) (length + 1);
//    System.arraycopy(dataWithoutLength, 0, data, 1, length);
//    return data;
//  }
}
