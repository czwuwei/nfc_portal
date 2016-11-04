package jp.co.fmap.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by z00066 on 2016/10/31.
 */

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

  @Test
  public void indexTest() throws Exception {
    byte[] bytes = new byte[]{'0', 'A', '1', 'B', '2', 'C', '3', 'D'};
    int cursor = 0;
    byte a = bytes[cursor += 1];
    System.out.println(a);
    assertTrue(a == 'A');

    byte b = bytes[cursor += 2];
    System.out.println(b);
    assertTrue(b == 'B');
  }
}
