package jp.co.fmap.util;

/**
 * Created by z00066 on 2016/10/31.
 */

public class CollectionUtil {


    public static String mkString(byte[] bytes) {
        Byte[] byteObjs = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteObjs[i] = bytes[i];
        }
        return mkString(byteObjs);
    }

    public static <T> String mkString(T[] list) {
        return mkString(list, ",");
    }

    public static <T> String mkString(T[] list, String delimit) {
        StringBuilder buf = new StringBuilder();
        for (T s : list) {
            buf.append(s.toString()).append(delimit);
        }
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }


    public static byte[] concatByteArray(byte[] arr1, byte[] arr2) {
        byte[] arr = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, arr, 0, arr1.length);
        System.arraycopy(arr2, 0, arr, arr1.length, arr2.length);
        return arr;
    }
}
