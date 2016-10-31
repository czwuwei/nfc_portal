package jp.co.fmap.util;

import org.junit.Test;

/**
 * Created by z00066 on 2016/10/31.
 */

import static org.junit.Assert.*;

public class StringUtilTest {

  @Test
  public void toHexStringTest() throws Exception {
    byte[] bytes = new byte[]{10, 16, 100, 127};
    System.out.print(StringUtil.hexString(bytes));
    assertTrue("0A10647F".equals(StringUtil.hexString(bytes).toUpperCase()));
  }

  @Test
  public void parseToByteTest() throws Exception {
    String hexString = "0A10647F";
    byte[] bytes = StringUtil.parseToByte(hexString);
    String bytesStr = CollectionUtil.mkString(bytes);
    System.out.print(bytesStr);
    assertTrue("10,16,100,127".equals(bytesStr));
  }
}
