package jp.co.fmap.nfc.f;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by z00066 on 2016/11/04.
 */

public class ReadWithoutEncryption extends NfcFCommand {
  public static byte BLOCK_INDEX_LENGTH_2 = 0b1;
  public static byte BLOCK_INDEX_LENGTH_3 = 0b0;
  public static byte BLOCK_ACCESS_MODE_CACHE = 0b0001;
  public static byte BLOCK_ACCESS_MODE_CACHE_EXCLUDE = 0b0000;

  public ReadWithoutEncryption() {
    super((byte)0x06, (byte)0x07);
  }

  public class Request extends NfcFCommand.Request {

    public byte[] idm;
    public byte[] serviceCodeList;
    public int numberOfBlock;
    public byte blockIndexLengthType = BLOCK_INDEX_LENGTH_2;
    public byte blockAccessMode = BLOCK_ACCESS_MODE_CACHE_EXCLUDE;
    public int serviceOrderIndex = 0;

    @Override
    NfcFCommand.Response getResponse() {
      return new Response();
    }

    @Override
    byte[] makeCmd() {
      byte[] cmd = null;
      try {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        byteStream.write(cmdCode);
        byteStream.write(idm);

        int numberOfService = serviceCodeList.length / 2;
        byteStream.write(numberOfService);

        for (int i = 0; i < numberOfService; i ++) {
          byteStream.write(serviceCodeList[2 * i + 1]);
          byteStream.write(serviceCodeList[2 * i]);
        }

        byteStream.write(numberOfBlock);

        for (int i = 0; i < numberOfBlock; i ++) {
          byteStream.write(blockIndexLengthType << 7 | blockAccessMode << 4 | serviceOrderIndex);
          byteStream.write((byte)i);
        }

        cmd = byteStream.toByteArray();
      } catch (IOException e) {
        Log.e(LOG_TAG, "byte stream exception", e);
      }
      return cmd;
    }
  }

  public class Response extends NfcFCommand.Response {

    public byte[] idm;
    public byte responseCode;
    public byte status1 = (byte)0xff;
    public byte status2 = (byte)0xff;
    public int numberOfBlock;
    public byte[] blockData;

    @Override
    public void parseResponseData(byte[] responseData) {
      int cursor = 1;
      responseCode = responseData[cursor += 1];
      idm = Arrays.copyOfRange(responseData, cursor, cursor += 8);
      status1 = responseData[cursor += 1];
      status2 = responseData[cursor += 1];

      if (status1 == 0x00) {
        numberOfBlock = responseData[cursor += 1];
        blockData = Arrays.copyOfRange(responseData, cursor, responseData.length - 1);
      }
    }
  }
}
