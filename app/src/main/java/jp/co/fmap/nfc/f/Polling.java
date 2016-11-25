package jp.co.fmap.nfc.f;

import java.util.Arrays;

/**
 * Created by z00066 on 2016/11/04.
 */

public class Polling extends NfcFCommand {

  public static final String[] ENUM_REQUEST_CODE = { "00", "01", "02" };
  public static final String[] ENUM_TIME_SLOT = { "00", "01", "03", "07", "0F" };

  public Polling() {
    super((byte) 0x00, (byte) 0x01);
  }


  public class Request extends NfcFCommand.Request {
    public byte[] systemCodeMask;
    public byte requestCode;
    public byte timeSlot;

    @Override
    NfcFCommand.Response getResponse() {
      return new Response();
    }

    @Override
    byte[] makeCmd() {
      return new byte[]{
            cmdCode,
            systemCodeMask[0],
            systemCodeMask[1],
            requestCode,
            timeSlot
        };
    }
  }

  public class Response extends NfcFCommand.Response {
    public byte[] idm;
    public byte[] pmm;
    public byte[] requestData;

    @Override
    public void parseResponseData(byte[] responseData) {
      int cursor = 2;
      idm = Arrays.copyOfRange(responseData, cursor, cursor += 8);
      pmm = Arrays.copyOfRange(responseData, cursor, cursor += 8);
      requestData = Arrays.copyOfRange(responseData, cursor, responseData.length);
    }
  }
}
