package jp.co.fmap.util;

import java.nio.ByteBuffer;

/**
 * Created by z00066 on 2017/02/24.
 */

public class ByteUtil {

    public static final int LONG_BYTES = 8;

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(LONG_BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(LONG_BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

}
