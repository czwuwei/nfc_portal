package jp.co.fmap.nfc.tag4;

import android.util.Log;

import java.util.Arrays;

import jp.co.fmap.nfcportal.MainActivity;
import jp.co.fmap.util.StringUtil;

/**
 * Created by wuv1982 on 2016/12/05.
 */

public class SelectFile extends Apdu {

    // PPSE: 00A404000e325041592e5359532e444446303100

    public class Request extends Apdu.Request<Response> {

        public Request(String aid) {
            super();
            this.cla = 0x00;
            this.ins = (byte) 0xA4;
            this.p1 = 0x04;
            this.p2 = 0x00;
            this.payload = StringUtil.parseToByte(aid);
        }

        @Override
        Response parseResponse(byte[] rawData) {
            return new Response(rawData);
        }
    }

    public class Response extends Apdu.Response {

        protected Response(byte[] rawData) {
            super(rawData);
        }

        public boolean success() {
            if (sw1 == 0x90 && sw2 == 0x00) {
                return true;
            } else if (sw1 == 0x6a && sw2 == 0x82) {
                Log.d(MainActivity.TAG, "File not found");
            }
            return false;
        }

        @Override
        protected void parseResponse() {
            byte[] rawData = getRawData();
            if (rawData != null) {
                int reverseOffset = rawData.length;
                if (rawData.length > 1) {
                    sw2 = rawData[reverseOffset -= 1];
                    sw1 = rawData[reverseOffset -= 1];
                    if (reverseOffset > 0) data = Arrays.copyOf(rawData, reverseOffset);
                }
            }
        }
    }
}
