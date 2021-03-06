package jp.co.fmap.nfc.tag3;

import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.util.Log;

import java.io.IOException;

import jp.co.fmap.util.StringUtil;

import static jp.co.fmap.util.StringUtil.hexString;

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

        public boolean keepConnection = true;

        abstract T parseResponse(byte[] rawData);

        abstract byte[] makeCmd();

        protected boolean validation(){
            return true;
        }

        public T transceive(Tag tag) {
            if (!validation()) {
                Log.w(LOG_TAG, "transceive canceled for validation failed !!!");
                return null;
            }

            T response = null;
            NfcF nfcf = NfcF.get(tag);
            Log.d(LOG_TAG, "manufacturer: " + hexString(nfcf.getManufacturer()));
            Log.d(LOG_TAG, "systemCode: " + hexString(nfcf.getSystemCode()));
            Log.d(LOG_TAG, "length: " + nfcf.getMaxTransceiveLength());
            Log.d(LOG_TAG, "timeout: " + nfcf.getTimeout());

            try {
                byte[] uncompletedCmd = makeCmd();

                // set command length
                int length = uncompletedCmd.length;
                byte[] cmd = new byte[length + 1];
                cmd[0] = (byte) (length + 1);
                System.arraycopy(uncompletedCmd, 0, cmd, 1, length);

                Log.i(LOG_TAG, "NFC-F send command: " + StringUtil.hexString(cmd));

                if(!nfcf.isConnected()) {
                    Log.d(LOG_TAG, "nfc connect");
                    nfcf.connect();
                }

                byte[] responseData = nfcf.transceive(cmd);

                if (responseData != null && responseData[1] == responseCode) {
                    Log.i(LOG_TAG, "NFC-F receive response: " + StringUtil.hexString(responseData));
                    response = parseResponse(responseData);
                } else {
                    Log.w(LOG_TAG, "illegal response : " + StringUtil.hexString(responseData));
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "NFC-F connect failed", e);
            } finally {
                if (nfcf.isConnected() && !keepConnection) {
                    Log.d(LOG_TAG, "nfc close");
                    try {
                        nfcf.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "NFC-F close failed", e);
                    }
                }
            }
            return response;
        }
    }

    abstract public class Response {
        public final int OFFSET_START = 1;
        protected int length;
        protected byte responseCode;

        protected byte[] rawData;

        protected Response(byte[] rawData) {
            int offset = 0;
            this.length = rawData[offset += 0];
            this.responseCode = rawData[offset += 1];
            this.rawData = rawData;
            this.parseResponse();
        }

        abstract protected void parseResponse();

        @Override
        public String toString() {
            return "cmd [" + NfcFCommand.this.cmdCode + "] =\n\t\t" + StringUtil.hexString(this.rawData);
        }

        public int getLength() {
            return length;
        }

        public byte getResponseCode() {
            return responseCode;
        }
    }
}

