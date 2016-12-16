package jp.co.fmap.nfc.tag3;

import java.util.Arrays;

/**
 * Created by z00066 on 2016/11/04.
 */

public class Polling extends NfcFCommand {

    public static final String[] ENUM_REQUEST_CODE = {"00", "01", "02"};
    public static final String[] ENUM_TIME_SLOT = {"00", "01", "03", "07", "0F"};

    public Polling() {
        super((byte) 0x00, (byte) 0x01);
    }


    public class Request extends NfcFCommand.Request<Response> {
        public byte[] systemCodeMask;
        public byte requestCode;
        public byte timeSlot;

        @Override
        Response parseResponse(byte[] rawData) {
            return new Response(rawData);
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
        public byte[] requestedData;

        private Response(byte[] responseData) {
            super(responseData);
        }

        @Override
        protected void parseResponse() {
            int cursor = CURSOR_START;
            if (rawData.length > cursor + 8) {
                idm = Arrays.copyOfRange(rawData, cursor += 1, cursor += 8);
            }
            if (rawData.length > cursor + 8) {
                pmm = Arrays.copyOfRange(rawData, cursor, cursor += 8);
            }
            if (rawData.length > cursor + 2) {
                requestedData = Arrays.copyOfRange(rawData, cursor, rawData.length);
            }
        }
    }
}
