package jp.co.fmap.nfc.tag4;

import java.util.Arrays;

import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2017/02/24.
 */

public class RawCommand extends Apdu {

    public class Request extends Apdu.Request<Response> {

        public Request(String cmd) {
            super();
            byte[] cmdBytes = StringUtil.parseToByte(cmd);
            init(cmdBytes);
        }

        public Request(byte[] cmdBytes) {
            super();
            init(cmdBytes);
        }

        private void init(byte[] cmdBytes) {
            this.cla = cmdBytes[0];
            this.ins = cmdBytes[1];
            this.p1 = cmdBytes[2];
            this.p2 = cmdBytes[3];
            if (cmdBytes.length > 4) {
                if (cmdBytes.length == 5) {
                    // case le
                    this.le = cmdBytes[4];
                } else {
                    this.payload = Arrays.copyOfRange(cmdBytes, 5, 5 + cmdBytes[4]);
                    if (cmdBytes.length > 5 + cmdBytes[4]) {
                        // case lc + payload + le
                        this.le = cmdBytes[cmdBytes.length - 1];
                    }
                    // case lc + payload
                }
            } else {
                // case no body
            }
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

        }
    }
}
