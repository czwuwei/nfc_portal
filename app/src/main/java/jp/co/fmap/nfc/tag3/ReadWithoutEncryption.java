package jp.co.fmap.nfc.tag3;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by z00066 on 2016/11/04.
 */

public class ReadWithoutEncryption extends NfcFCommand {
    public static byte BLOCK_INDEX_LENGTH_2 = 0b1;
    public static byte BLOCK_INDEX_LENGTH_3 = 0b0;
    public static byte BLOCK_ACCESS_MODE_CACHE = 0b0001;
    public static byte BLOCK_ACCESS_MODE_CACHE_EXCLUDE = 0b0000;

    public ReadWithoutEncryption() {
        super((byte) 0x06, (byte) 0x07);
    }

    public class Request extends NfcFCommand.Request<Response> {

        public byte[] idm;
        public byte[] serviceCodeList;
        public int numberOfBlock;
        public byte blockIndexLengthType = BLOCK_INDEX_LENGTH_2;
        public byte blockAccessMode = BLOCK_ACCESS_MODE_CACHE_EXCLUDE;
        public int serviceOrderIndex = 0;

        @Override
        Response parseResponse(byte[] rawData) {
            return new Response(rawData);
        }

        @Override
        byte[] makeCmd() {
            byte[] cmd = null;
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                byteStream.write(cmdCode);
                byteStream.write(idm);

                int numberOfService = serviceCodeList.length / 2;
                byteStream.write(numberOfService);

                for (int i = 0; i < numberOfService; i++) {
                    byteStream.write(serviceCodeList[2 * i + 1]);
                    byteStream.write(serviceCodeList[2 * i]);
                }

                byteStream.write(numberOfBlock);

                for (int i = 0; i < numberOfBlock; i++) {
                    byteStream.write(blockIndexLengthType << 7 | blockAccessMode << 4 | serviceOrderIndex);
                    byteStream.write((byte) i);
                }

                cmd = byteStream.toByteArray();
            } catch (IOException e) {
                Log.e(LOG_TAG, "byte stream exception", e);
            }
            return cmd;
        }
    }

    public class Response extends NfcFCommand.Response {

        public byte[] idm;
        public byte status1 = (byte) 0xff;
        public byte status2 = (byte) 0xff;
        public int numberOfBlock;
        public byte[] blockData;

        private Response(byte[] rawData) {
            super(rawData);
        }

        @Override
        protected void parseResponse() {
            int cursor = 2;
            idm = Arrays.copyOfRange(rawData, cursor, cursor += 8);
            status1 = rawData[cursor += 1];
            status2 = rawData[cursor += 1];

            if (status1 == 0x00) {
                numberOfBlock = rawData[cursor += 1];
                blockData = Arrays.copyOfRange(rawData, cursor, rawData.length - 1);
            }
        }

    }
}
