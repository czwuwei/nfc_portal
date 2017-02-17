package jp.co.fmap.nfcportal;

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

import jp.co.fmap.nfc.tag4.GetData;
import jp.co.fmap.nfc.tag4.ReadBinary;
import jp.co.fmap.nfc.tag4.SelectFile;

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
    public void onClick(View view) {
        try {
            MainActivity context = (MainActivity) getActivity();
            Tag tag = context.getNfcTag();

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
