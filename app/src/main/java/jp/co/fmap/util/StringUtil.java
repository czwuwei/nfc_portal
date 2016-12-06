package jp.co.fmap.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by z00066 on 2016/10/31.
 */

public class StringUtil {
  static final Character[] HEX_VALS = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String hexString(int intByte) {
    return hexString(new byte[]{(byte)intByte});
  }

  public static String hexString(byte[] bytes) {
    if (bytes == null) return null;

    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      int highVal = b >> 4 & 0x0F;
      String hightHex = Integer.toHexString(highVal);
      int lowVal = b & 0x0F;
      String lowHex = Integer.toHexString(lowVal);
      String hex = hightHex + lowHex;
      buf.append(hex);
    }

    return buf.toString();
  }

  public static byte[] parseToByte(String hexString) {

    if (hexString == null) return null;

    char[] chars = hexString.toLowerCase(Locale.getDefault()).toCharArray();
    int len = chars.length;
    byte[] bytes = new byte[chars.length/2];
    for (int i =0; i < chars.length; i = i + 2) {
      char hightHex = chars[i];
      char lowHex = chars[i + 1];
      int hightVal = Arrays.binarySearch(HEX_VALS, hightHex) << 4;
      int lowVal = Arrays.binarySearch(HEX_VALS, lowHex);
      byte val = (byte) (hightVal | lowVal);
      bytes[i / 2] = val;
    }

    return bytes;
  }

  public static String utfString(byte[] bytes) {
    return new String(bytes, Charset.forName("UTF-8"));
  }

  public static String prefix(String target, String prefix) {
    String buf = prefix + target;
    return buf.substring(buf.length() - prefix.length());
  }


}
