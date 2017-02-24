package jp.co.fmap.nfc.tag4;

import java.util.Arrays;

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
            this.p1 = (byte) 0x04;
            this.p2 = 0x00;
            this.payload = StringUtil.parseToByte(aid);
            this.le = 0x00;
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
