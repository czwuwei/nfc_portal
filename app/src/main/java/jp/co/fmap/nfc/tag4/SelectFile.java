package jp.co.fmap.nfc.tag4;

import java.util.Arrays;

import jp.co.fmap.util.StringUtil;

/**
 * Created by wuv1982 on 2016/12/05.
 */

public class SelectFile extends Apdu {

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
            }
            return false;
        }

        @Override
        protected void parseResponse() {
            byte[] rawData = getRawData();
            if (rawData != null) {
                int reverseCursor = rawData.length;
                if (rawData.length > 1) {
                    sw2 = rawData[reverseCursor -= 1];
                    sw1 = rawData[reverseCursor -= 1];
                    if (reverseCursor > 0) data = Arrays.copyOf(rawData, reverseCursor);
                }
            }
        }
    }
}
