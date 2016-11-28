package jp.co.fmap.nfcportal;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import jp.co.fmap.nfc.f.NfcFCommand;
import jp.co.fmap.nfc.f.Polling;
import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/24.
 */

public class FragmentPolling extends MainActivity.PlaceholderFragment {


  private EditText edtSystemCodeMask;
  private Spinner spRequestCode;
  private Spinner spTimeSlot;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(MainActivity.TAG, "FragmentPolling onCreateView");
    View rootView = inflater.inflate(R.layout.frm_polling, container, false);

    edtSystemCodeMask = (EditText) rootView.findViewById(R.id.edtSystemMask);

    spRequestCode = (Spinner) rootView.findViewById(R.id.spRequestCode);
    ArrayAdapter<String> arrayAdapterRequestCode = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Polling.ENUM_REQUEST_CODE);
    arrayAdapterRequestCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spRequestCode.setAdapter(arrayAdapterRequestCode);

    spTimeSlot = (Spinner) rootView.findViewById(R.id.spTimeSlot);
    ArrayAdapter<String> arrayAdapterTimeSlot = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Polling.ENUM_TIME_SLOT);
    arrayAdapterTimeSlot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spTimeSlot.setAdapter(arrayAdapterTimeSlot);

    return rootView;
  }


  @Override
  protected NfcFCommand.Request genNfcCommand(Tag tag) {
    Polling.Request polling = new Polling().new Request();
    polling.systemCodeMask = StringUtil.parseToByte(edtSystemCodeMask.getText().toString());
    polling.requestCode = StringUtil.parseToByte(Polling.ENUM_REQUEST_CODE[spRequestCode.getSelectedItemPosition()])[0];
    polling.timeSlot = StringUtil.parseToByte(Polling.ENUM_TIME_SLOT[spTimeSlot.getSelectedItemPosition()])[0];

    return polling;
  }
}
