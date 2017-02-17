package jp.co.fmap.nfc.tag4;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.co.fmap.util.StringUtil;

/**
 * Created by wuv1982 on 2016/12/05.
 */

public abstract class Apdu {

    public static class TLV {
        public byte tag;
        public byte length;
        public byte[] value;
    }

    public abstract class Request<T extends Response> {
        public boolean keepConnection = false;

        private static final String LOG_TAG = "fmap-apdu";
        protected byte cla;
        protected byte ins;
        protected byte p1;
        protected byte p2;
        protected byte[] payload;
        protected byte le;
        private byte lc;

        private byte[] makeCmd() {
            byte[] cmd = null;
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                if (payload != null) {
                    this.lc = (byte) payload.length;
                    byteStream.write(new byte[]{cla, ins, p1, p2, lc});
                    byteStream.write(payload);
                } else {
                    byteStream.write(new byte[]{cla, ins, p1, p2});
                }

                byteStream.write(le);
                cmd = byteStream.toByteArray();
            } catch (IOException e) {
                Log.e(LOG_TAG, "byte stream exception", e);
            }
            return cmd;
        }

        abstract T parseResponse(byte[] rawData);

        public T transceive(Tag tag) throws Exception {
            T response = null;
            IsoDep isodep = IsoDep.get(tag);
            if (isodep != null) {
                try {
                    if (!isodep.isConnected()) {
                        isodep.connect();
                    }

                    byte[] cmd = makeCmd();
                    Log.i(LOG_TAG, "ISODEP send command: " + StringUtil.hexString(cmd));
                    byte[] responseData = isodep.transceive(cmd);
                    if (responseData != null) {
                        Log.i(LOG_TAG, "ISODEP receive response: " + StringUtil.hexString(responseData));
                        response = parseResponse(responseData);
                    } else {
                        Log.w(LOG_TAG, "illegal response : " + StringUtil.hexString(responseData));
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "transceive exception", e);
                    throw e;
                } finally {
                    if (isodep.isConnected() && !keepConnection) {
                        Log.d(LOG_TAG, "nfc close");
                        try {
                            isodep.close();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "isodep close failed", e);
                        }
                    }
                }
            }
            return response;
        }

    }

    public abstract class Response {
        protected byte[] data;
        protected byte sw1;
        protected byte sw2;
        private byte[] rawData;

        protected Response(byte[] rawData) {
            this.rawData = rawData;
            this.parseResponse();
        }

        public byte[] getRawData() {
            return rawData;
        }

        abstract protected void parseResponse();
    }
}
