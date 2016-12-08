package jp.co.fmap.nfcportal;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import jp.co.fmap.nfc.tag3.Polling;
import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/24.
 */

public class FragmentPolling extends MainActivity.PlaceholderFragment implements View.OnClickListener {


    private EditText edtSystemCodeMask;
    private Spinner spRequestCode;
    private Spinner spTimeSlot;
    private Button btnTransceive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(MainActivity.TAG, "FragmentPolling onCreateView");
        View rootView = inflater.inflate(R.layout.frm_polling, container, false);

        edtSystemCodeMask = (EditText) rootView.findViewById(R.id.edtSystemMask);
        if (MainActivity.sharedNfcModel.systemCode != null) {
            edtSystemCodeMask.setText(MainActivity.sharedNfcModel.systemCode);
        }

        spRequestCode = (Spinner) rootView.findViewById(R.id.spRequestCode);
        ArrayAdapter<String> arrayAdapterRequestCode = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Polling.ENUM_REQUEST_CODE);
        arrayAdapterRequestCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequestCode.setAdapter(arrayAdapterRequestCode);
        spRequestCode.setSelection(1);

        spTimeSlot = (Spinner) rootView.findViewById(R.id.spTimeSlot);
        ArrayAdapter<String> arrayAdapterTimeSlot = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Polling.ENUM_TIME_SLOT);
        arrayAdapterTimeSlot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeSlot.setAdapter(arrayAdapterTimeSlot);

        btnTransceive = (Button) rootView.findViewById(R.id.btnTransceive);
        btnTransceive.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        Log.d(MainActivity.TAG, "onBtnTransceive");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                MainActivity context = (MainActivity) getActivity();
                Tag tag = context.getNfcTag();
                Polling.Request req = genNfcCommand(tag);
                if (tag != null) {
                    Polling.Response response = req.transceive(tag);
                    Log.d(MainActivity.TAG, response.toString());

                    if (response != null) {
                        MainActivity.sharedNfcModel.idm = StringUtil.hexString(response.idm);
                        MainActivity.sharedNfcModel.pmm = StringUtil.hexString(response.pmm);
                    }
                }
                return null;
            }
        }.execute();
    }

    @Override
    protected Polling.Request genNfcCommand(Tag tag) {
        Polling.Request polling = new Polling().new Request();
        polling.systemCodeMask = StringUtil.parseToByte(edtSystemCodeMask.getText().toString());
        polling.requestCode = StringUtil.parseToByte(Polling.ENUM_REQUEST_CODE[spRequestCode.getSelectedItemPosition()])[0];
        polling.timeSlot = StringUtil.parseToByte(Polling.ENUM_TIME_SLOT[spTimeSlot.getSelectedItemPosition()])[0];

        return polling;
    }

}
