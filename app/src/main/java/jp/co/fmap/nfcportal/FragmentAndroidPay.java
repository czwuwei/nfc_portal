package jp.co.fmap.nfcportal;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jp.co.fmap.nfc.tag4.SelectFile;

/**
 * Created by z00066 on 2017/02/10.
 */

public class FragmentAndroidPay extends MainActivity.PlaceholderFragment implements View.OnClickListener {

    Button btnSelectAID;
    Button btnSelectPPSE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(MainActivity.TAG, "FragmentAndroidPay onCreateView");
        View rootView = inflater.inflate(R.layout.frm_android_pay, container, false);

        btnSelectAID = (Button) rootView.findViewById(R.id.btnSelectAid);
        btnSelectAID.setOnClickListener(this);
        btnSelectPPSE = (Button) rootView.findViewById(R.id.btnSelectPPSE);
        btnSelectPPSE.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {

        String aid = null;
        switch (view.getId()) {
            case R.id.btnSelectAid:
//                <aid-filter android:name="4F53452E5641532E3031" />
//                <aid-filter android:name="A000000476D0000101" />
//                <aid-filter android:name="A000000476D0000111" />
                aid = "4F53452E5641532E3031";
                break;
            case R.id.btnSelectPPSE:
                aid = "325041592E5359532E4444463031";
                break;
            default:
        }

        MainActivity context = (MainActivity) getActivity();
        Tag tag = context.getNfcTag();
        SelectFile cmd = new SelectFile();
        SelectFile.Request request = cmd.new Request(aid);
        try {
            request.keepConnection = false;
            request.transceive(tag);
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "failed to select AID " + aid, e);
        }
    }
}
