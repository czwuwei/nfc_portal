package jp.co.fmap.nfc.tag3;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by z00066 on 2016/11/28.
 *
 * @see "http://nfcpy.readthedocs.io/en/latest/modules/tag.html"
 *
 * The Search Service Code command provides access to the iterable list of services and areas within
 * the activated system. The service_index argument may be any value from 0 to 0xffff. As long as
 * there is a service or area found for a given service_index, the information returned is a tuple
 * with either one or two 16-bit integer elements. Two integers are returned for an area definition,
 * the first is the area code and the second is the largest possible service index for the area.
 * One integer, the service code, is returned for a service definition.
 */

public class SearchServiceCode extends NfcFCommand {
    public SearchServiceCode() {
        super((byte) 0x0A, (byte) 0x0B);
    }

    public class Request extends NfcFCommand.Request<Response> {

        public byte[] idm;
        public int serviceIndex;

        @Override
        protected boolean validation() {
            if (serviceIndex > 0xFFFF) {
                Log.w(LOG_TAG, "serviceIndex must be under 16bit");
                return false;
            }
            return true;
        }

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
                byteStream.write((byte) (serviceIndex & 0xFF));
                byteStream.write((byte) ((serviceIndex >> 8) & 0xFF));

                cmd = byteStream.toByteArray();

            } catch (IOException e) {
                Log.e(LOG_TAG, "byte stream exception", e);
            }
            return cmd;
        }
    }

    public class Response extends NfcFCommand.Response {

        public byte[] idm;
        public byte[] serviceCode;
        public byte[] areaInfo;

        protected Response(byte[] rawData) {
            super(rawData);
        }

        @Override
        protected void parseResponse() {
            int offset = OFFSET_START;
            idm = Arrays.copyOfRange(rawData, offset += 1, offset += 8);

            if (rawData.length - offset == 4) {
                // area definition
                areaInfo = Arrays.copyOfRange(rawData, offset, offset += 4);

                // little-endian
                byte b = areaInfo[0];
                areaInfo[0] = areaInfo[1];
                areaInfo[1] = b;

            } else if (rawData.length - offset == 2) {
                // service code
                serviceCode = Arrays.copyOfRange(rawData, offset, offset += 2);

                // little-endian
                byte b = serviceCode[0];
                serviceCode[0] = serviceCode[1];
                serviceCode[1] = b;
            }
        }
    }
}
