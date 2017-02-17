package jp.co.fmap.nfc.tag4;

import java.util.Arrays;

/**
 * Created by z00066 on 2017/02/17.
 */

public class ReadBinary extends Apdu {

    public class Request extends Apdu.Request<Response> {
        public Request() {
            super();
            this.cla = 0x00;
            this.ins = (byte)0xB0;
            this.p1 = (byte) 0x80;
            this.p2 = 0x00;
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
