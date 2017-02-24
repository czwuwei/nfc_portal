package jp.co.fmap.util;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;

/**
 * Created by z00066 on 2016/10/31.
 */

public class StringUtilTest {

    static {
        Throwable th = new Throwable();
        if (th == null) {
            throw new NullPointerException(String.valueOf("null throwable"));
        }
        StackTraceElement[] stackTrace = th.getStackTrace();
        int i = stackTrace.length >= 3 ? 1 : 0;
        int length = stackTrace.length;
//        if (i == 0) {
//            throw new IllegalStateException(Preconditions.format("Unexpected stack trace length (should be >= %s): [%s]", Integer.valueOf(3), Integer.valueOf(length)));
//        }
        String className = stackTrace[2].getClassName();
//        if ((!Platform.stringIsNullOrEmpty(className) ? 1 : 0) == 0) {
//            throw new IllegalArgumentException(String.valueOf("null or empty fullClassName"));
//        }
        String substring = className.contains(".") ? className.substring(className.lastIndexOf(46) + 1) : className;
//        if ((substring.length() > 0 ? 1 : 0) == 0) {
//            throw new IllegalArgumentException(Preconditions.format("empty simple class name for : [%s]", className));
//        } else if (substring == null) {
//            throw new NullPointerException(String.valueOf("null tag"));
//        } else {
//            LOG = new SimpleFormattingLogger(substring, substring.length() > 23 ? substring.substring(0, 23) : substring);
//        }
        System.out.println(substring);
    }


    public static byte[] getSelectCommand(byte[] var0) {
        byte[] var2 = ByteBuffer.allocate(4).putInt(10748928).array();
        byte var1 = (byte) var0.length;
        byte[] var3 = new byte[]{(byte) 0};
        return concat(new byte[][]{var2, {var1}, var0, var3});
    }

    public static byte[] concat(byte[]... var0) {
        int var3 = var0.length;
        int var1 = 0;

        int var2;
        for (var2 = 0; var1 < var3; ++var1) {
            var2 += var0[var1].length;
        }

        byte[] var4 = new byte[var2];
        var3 = var0.length;
        var1 = 0;

        for (var2 = 0; var1 < var3; ++var1) {
            byte[] var5 = var0[var1];
            System.arraycopy(var5, 0, var4, var2, var5.length);
            var2 += var5.length;
        }

        return var4;
    }

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

    @Test
    public void valueTest() throws Exception {
        byte sw1 = (byte) -112;
        System.out.println(Integer.toBinaryString(sw1));
        byte value = (byte) 0x90;
        System.out.println(Integer.toBinaryString(value));
        assertTrue(sw1 == value);

        byte[] SMART_TAP_AID_V1_3 = new byte[]{(byte) -96, (byte) 0, (byte) 0, (byte) 4, (byte) 118, (byte) -48, (byte) 0, (byte) 1, (byte) 1};
        byte[] SMART_TAP_AID_V2_0 = new byte[]{(byte) -96, (byte) 0, (byte) 0, (byte) 4, (byte) 118, (byte) -48, (byte) 0, (byte) 1, (byte) 17};

        System.out.println(StringUtil.hexString(SMART_TAP_AID_V1_3));
        System.out.println(StringUtil.hexString(SMART_TAP_AID_V2_0));
        System.out.println(StringUtil.hexString(getSelectCommand(SMART_TAP_AID_V1_3)));
        System.out.println(StringUtil.hexString(getSelectCommand(SMART_TAP_AID_V2_0)));
    }

    @Test
    public void int2Byte() throws Exception {

        int[] ints = new int[]{-92, -112, -64, 80, 82,
                57153, 57155, 57169, 57171, 57186,
                57105, 57106, 57137, 57138, 57139, 57153};

        for (int i : ints) {
            System.out.println(i + " = " + Integer.toHexString(i) + ": " + Integer.toBinaryString(i));
        }
    }

}

