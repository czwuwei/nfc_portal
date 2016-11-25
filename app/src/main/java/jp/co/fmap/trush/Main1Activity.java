package jp.co.fmap.trush;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.co.fmap.nfc.f.ReadWithoutEncryption;
import jp.co.fmap.nfcportal.R;
import jp.co.fmap.util.StringUtil;

import static jp.co.fmap.util.CollectionUtil.mkString;
import static jp.co.fmap.util.StringUtil.hexString;
import static jp.co.fmap.util.StringUtil.utfString;

public abstract class Main1Activity extends Activity {

  public static String TAG = "WNFC";
  private PendingIntent pendingIntent;
  private NfcAdapter mAdapter;

  private Tag tag;
  private NfcF nfcF;
  private NFCCmdReadWithNoSecurity nfcCmdReadWithNoSecurity = new NFCCmdReadWithNoSecurity();
  private EditText edtCmdType;
  private EditText edtSystemCodes;
  private SeekBar seekBarBlockCount;
  private SeekBar seekBarSystemCodeIndex;
  private RadioGroup radioGroupBlockLength;
  private RadioGroup radioGroupBlockAccessMode;
  private Button btnTransceive;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    mAdapter = NfcAdapter.getDefaultAdapter(this);
    Log.d(TAG, "onCreate");

    edtCmdType = (EditText) findViewById(R.id.edtCmdType);
    edtSystemCodes = (EditText) findViewById(R.id.edtSystemCodes);
    seekBarBlockCount = (SeekBar) findViewById(R.id.seekBar_blockCount);
    seekBarBlockCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(Main1Activity.this, "block count : " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
      }
    });
    seekBarSystemCodeIndex = (SeekBar) findViewById(R.id.seekBar_system_index);
    seekBarSystemCodeIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(Main1Activity.this, "system code index : " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
      }
    });
    radioGroupBlockLength = (RadioGroup) findViewById(R.id.radioGroup_block_length);
    radioGroupBlockAccessMode = (RadioGroup) findViewById(R.id.radioGroup_block_access_mode);
    btnTransceive = (Button)findViewById(R.id.btnTransceive);
    btnTransceive.setEnabled(false);
  }

  @Override
  protected void onResume() {
    super.onResume();
//        String[][] techListsArray = new String[][]{new String[]{NfcFCommand.class.getName()}};
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
      this.btnTransceive.setEnabled(true);

      if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
        Log.d(TAG, "found ACTION_NDEF_DISCOVERED");
        for (Parcelable rawMsg : intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
          NdefMessage msg = (NdefMessage) rawMsg;
          for (NdefRecord record : msg.getRecords()) {
            Log.d(TAG, "type: " + hexString(record.getType()));
            Log.d(TAG, "id: " + hexString(record.getId()));
            Log.d(TAG, "payload: " + utfString(record.getPayload()));
          }
        }
      } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        Log.d(TAG, "found ACTION_TAG_DISCOVERED");
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "id: " + hexString(tag.getId()));
        Log.d(TAG, "techList: " + mkString(tag.getTechList()));
        nfcF = NfcF.get(tag);
        Log.d(TAG, "manufacturer: " + hexString(nfcF.getManufacturer()));
        Log.d(TAG, "systemCode: " + hexString(nfcF.getSystemCode()));
        Log.d(TAG, "length: " + nfcF.getMaxTransceiveLength());
        Log.d(TAG, "timeout: " + nfcF.getTimeout());

        // request systemCodes

        ReadWithoutEncryption.Request cmd = new ReadWithoutEncryption().new Request();
        cmd.idm = tag.getId();
        cmd.serviceCodeList = new byte[]{0x09, 0x0f};
        cmd.numberOfBlock = 12;
        cmd.transceive(tag);


//        new AsyncTask<Void, Void, Void>() {
//          @Override
//          protected Void doInBackground(Void... voids) {
//            try {
//              if (!nfcF.isConnected()) {
//                nfcF.connect();
//              }
//              Log.d(TAG, "NFC connected");
//
//              NFCCmdSearchServiceCode requestServiceCode = new NFCCmdSearchServiceCode();
//              requestServiceCode.idm = tag.getId();
//              requestServiceCode.serviceIndex = 1;
//
//              Log.d(TAG, "send data: " + StringUtil.hexString(requestServiceCode.toBytes()));
//              byte[] response = nfcF.transceive(requestServiceCode.toBytes());
//              Log.d(TAG, "receive data: " + StringUtil.hexString(response));
//            } catch (IOException e) {
//              Log.e(TAG, "nfc connect exception", e);
//            }
//            return null;
//          }
//        }.execute();
      }
    }
  }

  private byte[] readWithoutEncryption(byte[] idm, int size) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

    bout.write(0);           // データ長バイトのダミー
    bout.write(0x06);        // Felicaコマンド「Read Without Encryption」
    bout.write(idm);         // カードID 8byte
    bout.write(1);           // サービスコードリストの長さ(以下２バイトがこの数分繰り返す)
    bout.write(0x0f);        // 履歴のサービスコード下位バイト
    bout.write(0x09);        // 履歴のサービスコード上位バイト
    bout.write(size);        // ブロック数
    for (int i = 0; i < size; i++) {
      bout.write(0x80);    // ブロックエレメント上位バイト 「Felicaユーザマニュアル抜粋」の4.2項参照
      bout.write(i);       // ブロック番号
    }

    byte[] msg = bout.toByteArray();
    msg[0] = (byte) msg.length; // 先頭１バイトはデータ長
    return msg;
  }

  public void onBtnTransceive(View view) {
    Log.d(TAG, "on button Transceive");

    nfcCmdReadWithNoSecurity.cmdType = this.edtCmdType.getText().toString();
    nfcCmdReadWithNoSecurity.idm = tag.getId();
    nfcCmdReadWithNoSecurity.systemCodes = this.edtSystemCodes.getText().toString().split(",");
    nfcCmdReadWithNoSecurity.blockCount = this.seekBarBlockCount.getProgress();
    nfcCmdReadWithNoSecurity.systemOrderIndex = this.seekBarSystemCodeIndex.getProgress();
    if (this.radioGroupBlockLength.getCheckedRadioButtonId() == R.id.radioButton_2bytes) {
      nfcCmdReadWithNoSecurity.blockIndexLengthType = NFCCmdReadWithNoSecurity.BLOCK_INDEX_LENGTH_2;
    } else if (this.radioGroupBlockLength.getCheckedRadioButtonId() == R.id.radioButton_3bytes) {
      nfcCmdReadWithNoSecurity.blockIndexLengthType = NFCCmdReadWithNoSecurity.BLOCK_INDEX_LENGTH_3;
    }
    if (this.radioGroupBlockAccessMode.getCheckedRadioButtonId() == R.id.radioButton_withoutCache) {
      nfcCmdReadWithNoSecurity.blockAccessMode = NFCCmdReadWithNoSecurity.BLOCK_ACCESS_MODE_CACHE_EXCLUDE;
    } else if (this.radioGroupBlockAccessMode.getCheckedRadioButtonId() == R.id.radioButton_withCache) {
      nfcCmdReadWithNoSecurity.blockAccessMode = NFCCmdReadWithNoSecurity.BLOCK_ACCESS_MODE_CACHE;
    }

    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) {
        try {
          if (!nfcF.isConnected()) {
            nfcF.connect();
          }
          Log.d(TAG, "NFC connected");
//                            byte[] cmd = makeCmd("06" + hexString(tag.getId()) + "010f090A8000800180028003800480058006800780088009");
//                            byte[] cmd = readWithoutEncryption(tag.getId(), 10);
//          NFCCmdReadWithNoSecurity cmd = new NFCCmdReadWithNoSecurity();
//          cmd.cmdType = "06";
//          cmd.idm = StringUtil.hexString(tag.getId());
//          cmd.systemCodes = new String[]{"0f09"};
//          cmd.numberOfBlock = 12;

          Log.d(TAG, "send data: " + StringUtil.hexString(nfcCmdReadWithNoSecurity.toBytes()));
          byte[] response = nfcF.transceive(nfcCmdReadWithNoSecurity.toBytes());
          Log.d(TAG, "receive data: " + StringUtil.hexString(response));
        } catch (IOException e) {
          Log.e(TAG, "nfc connect exception", e);
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        if(!nfcF.isConnected()) {
          btnTransceive.setEnabled(false);
        }
      }
    }.execute();
  }

}
