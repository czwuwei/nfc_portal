package jp.co.fmap.nfc.tag4;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by wuv1982 on 2016/12/05.
 */

public abstract class Apdu {

    public abstract class Request<T extends Response> {
        private static final String LOG_TAG = "fmap-apdu";

        protected byte cla;
        protected byte ins;
        protected byte p1;
        protected byte p2;
        private byte lc;
        protected byte[] payload;
        protected byte le;

        private byte[] makeCmd() {
            byte[] cmd = null;
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                this.lc = (byte) payload.length;
                byteStream.write(new byte[]{cla, ins, p1, p2, lc});
                byteStream.write(payload);
                byteStream.write(le);

                cmd = byteStream.toByteArray();
            } catch (IOException e) {
                Log.e(LOG_TAG, "byte stream exception", e);
            }
            return cmd;
        }

        abstract T parseResponse(byte[] rawData);

        public T transeive(Tag tag) {
            T response = null;
            IsoDep isodep = IsoDep.get(tag);
            if (isodep != null) {
                try {
                    isodep.connect();
                    byte[] responseData = isodep.transceive(makeCmd());
                    response = parseResponse(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

    }

    public abstract class Response {
        private byte[] rawData;

        protected byte[] data;
        protected byte sw1;
        protected byte sw2;

        protected Response(byte[] rawData) {
            this.rawData = rawData;
            this.parseResponse();
        }

        abstract protected void parseResponse();
    }

    public static class TLV {
        public byte tag;
        public byte length;
        public byte[] value;
    }
}
