package jp.co.fmap.nfcportal;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import jp.co.fmap.nfc.tag4.Apdu;
import jp.co.fmap.nfc.tag4.GetData;
import jp.co.fmap.nfc.tag4.RawCommand;
import jp.co.fmap.nfc.tag4.ReadBinary;
import jp.co.fmap.nfc.tag4.SelectFile;
import jp.co.fmap.util.ByteUtil;
import jp.co.fmap.util.CollectionUtil;

import static jp.co.fmap.util.CollectionUtil.mkString;
import static jp.co.fmap.util.StringUtil.hexString;

/**
 * Created by z00066 on 2017/02/10.
 */

public class FragmentAndroidPay extends MainActivity.PlaceholderFragment implements View.OnClickListener {

    private Button btnSelect;
    private Button btnGetData;
    private Button btnReadBinary;
    private Button btnDisconnect;
    private Spinner spAidList;

    private String[] AID_LIST = new String[]{
            "4F53452E5641532E3031",
            "A000000476D0000101",
            "A000000476D0000111",
            "325041592E5359532E4444463031" // PPSE
    };
    private Tag tag;

    private final String TAG = getClass().getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(MainActivity.TAG, "FragmentAndroidPay onCreateView");
        View rootView = inflater.inflate(R.layout.frm_android_pay, container, false);

        btnSelect = (Button) rootView.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);

        btnGetData = (Button) rootView.findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(this);

        btnDisconnect = (Button) rootView.findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(this);

        btnReadBinary = (Button) rootView.findViewById(R.id.btnReadBinary);
        btnReadBinary.setOnClickListener(this);

        spAidList = (Spinner) rootView.findViewById(R.id.spAidList);
        ArrayAdapter<String> arrayAdapterRequestCode = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, AID_LIST);
        arrayAdapterRequestCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAidList.setAdapter(arrayAdapterRequestCode);
        spAidList.setSelection(1);

        return rootView;
    }

    @Override
    public void onNfcIntent(Context context, Intent intent) {
        Log.d(TAG, "Android Pay mode");
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "id: " + hexString(tag.getId()));
        Log.d(TAG, "techList: " + mkString(tag.getTechList()));

        try {
            {
                SelectFile cmd = new SelectFile();
                SelectFile.Request request = cmd.new Request(AID_LIST[1]);
                SelectFile.Response response = request.transceive(tag);
                 if (!response.success()) return;
            }
            {
                RawCommand cmd = new RawCommand();

                Apdu.TLV merchantId = new Apdu.TLV();
                merchantId.tag = new byte[]{(byte)0xdf, (byte)0x31};
                merchantId.value = ByteUtil.longToBytes(3187565030047023378L);

                Apdu.TLV storeId = new Apdu.TLV();
                storeId.tag = new byte[]{(byte)0xdf, (byte)0x32};
                storeId.value = "cyberz".getBytes("ASCII");

                RawCommand.Request request = cmd.new Request("90500000");
                request.payload = CollectionUtil.concatByteArray(merchantId.toBytes(), storeId.toBytes());
                RawCommand.Response response = request.transceive(tag);
            }

//            {
//                SelectFile cmd = new SelectFile();
//                SelectFile.Request request = cmd.new Request("0001");
//                request.p1 = 0x02;
//                SelectFile.Response response = request.transceive(tag);
//                if (!response.success()) return;
//            }
//            {
//                ReadBinary cmd = new ReadBinary();
//                ReadBinary.Request request = cmd.new Request();
//                ReadBinary.Response response =request.transceive(tag);
//                if (!response.success()) return;
//            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "NFC transceive failed", e);
        } finally {
            Log.d(getClass().getSimpleName(), "NFC transceive over");
        }
    }

    @Override
    public void onClick(View view) {
        try {
//            MainActivity context = (MainActivity) getActivity();
//            Tag tag = context.getNfcTag();

            switch (view.getId()) {
                case R.id.btnSelect: {
                    String aid = spAidList.getSelectedItem().toString();
                    SelectFile cmd = new SelectFile();
                    SelectFile.Request request = cmd.new Request(aid);
                    request.keepConnection = false;
                    request.transceive(tag);
                    break;
                }
                case R.id.btnGetData: {
                    GetData cmd = new GetData();
                    GetData.Request request = cmd.new Request();
                    request.transceive(tag);
                    break;
                }
                case R.id.btnReadBinary: {
                    ReadBinary cmd = new ReadBinary();
                    ReadBinary.Request request = cmd.new Request();
                    request.transceive(tag);
                    break;
                }
                case R.id.btnDisconnect: {
                    IsoDep.get(tag).close();
                }
                default:
            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "failed to transceive");
        }

    }
}
