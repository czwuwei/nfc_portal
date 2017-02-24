package jp.co.fmap.util;

import org.junit.Test;

/**
 * Created by z00066 on 2017/02/24.
 */

public class CollectionUtilTest {

    @Test
    public void testByteConcat() {
        byte[] arr1 = new byte[] { 0x01, 0x02,0x03};
        byte[] arr2 = new byte[] { (byte)0xa1, (byte)0xa2, (byte)0xa3 };

        byte[] arr = CollectionUtil.concatByteArray(arr1, arr2);

        System.out.println(StringUtil.hexString(arr));
    }
}
