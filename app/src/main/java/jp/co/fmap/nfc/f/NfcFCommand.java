package jp.co.fmap.nfc.f;

import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.util.Log;

import java.io.IOException;

import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/04.
 */


public abstract class NfcFCommand {
  //  Polling((byte) 0x00, (byte) 0x01)
//  ,RequestService((byte) 0x02, (byte) 0x03)
//  ,RequestResponse((byte) 0x04, (byte) 0x05)
//  ,ReadWithoutEncryption((byte) 0x06, (byte) 0x07)
//  ,WriteWithoutEncryption((byte) 0x08, (byte) 0x09)
//  ,SearchServiceCode((byte) 0x0a, (byte) 0x0b)
//  ,RequestSystemCode((byte) 0x0c, (byte) 0x0d)
//  ;
  public static final String LOG_TAG = "fmap-nfc";

  public final byte cmdCode;
  public final byte responseCode;

  protected NfcFCommand(byte cmdCode, byte responseCode) {
    this.cmdCode = cmdCode;
    this.responseCode = responseCode;
  }

  abstract public class Request<T extends Response> {
    abstract T getResponse();
    abstract byte[] makeCmd();

    T response;

    public T transceive(Tag tag) {
      NfcF nfcf = NfcF.get(tag);
      try {
        nfcf.connect();

        byte[] uncompletedCmd = makeCmd();

        // set command length
        int length = uncompletedCmd.length;
        byte[] cmd = new byte[length + 1];
        cmd[0] = (byte) (length + 1);
        System.arraycopy(uncompletedCmd, 0, cmd, 1, length);

        Log.i(LOG_TAG, "NFC-F send command: " + StringUtil.hexString(cmd));
        byte[] responseData = nfcf.transceive(cmd);

        if (responseData != null && responseData[1] == responseCode) {
          response = getResponse();
          response.length = responseData[0];
          Log.i(LOG_TAG, "NFC-F receive response: " + StringUtil.hexString(responseData));
          response.parseResponseData(responseData);
        } else {
          Log.w(LOG_TAG, "illegal response : " + StringUtil.hexString(responseData));
        }

      } catch (IOException e) {
        Log.e(LOG_TAG, "NFC-F connect failed", e);
      }
      return response;
    }
  }

  abstract public class Response {
    protected int length;
    abstract public void parseResponseData(byte[] responseData);
  }
}

