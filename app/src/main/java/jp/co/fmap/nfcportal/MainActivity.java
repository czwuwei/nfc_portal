package jp.co.fmap.nfcportal;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.nio.charset.Charset;

public class MainActivity extends Activity {

    public static String TAG = "WNFC";
    private PendingIntent pendingIntent;
    private NfcAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        String[][] techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        mAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        if (intent != null) {
            Log.d(TAG, "new intent: " + intent.toString());
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Log.d(TAG, "found ACTION_NDEF_DISCOVERED");
                for (Parcelable rawMsg : intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
                    NdefMessage msg = (NdefMessage)rawMsg;
                    for (NdefRecord record: msg.getRecords()) {
                        Log.d(TAG, "type: " + utfString(record.getType()));
                        Log.d(TAG, "id: " + utfString(record.getId()));
                        Log.d(TAG, "payload: " + utfString(record.getPayload()));
                    }
                }
            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                Log.d(TAG, "found ACTION_TAG_DISCOVERED");
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Log.d(TAG, "id: " + utfString(tag.getId()));
                Log.d(TAG, "techList: " + mkString(tag.getTechList()));
            }
        }
    }

    static String utfString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    static String mkString(String[] list) {
        return mkString(list, ",");
    }
    static String mkString(String[] list, String delimit) {
        StringBuilder buf = new StringBuilder();
        for (String s : list) {
            buf.append(s).append(delimit);
        }
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
}
