package jp.co.fmap.nfcportal;

import android.app.Fragment;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jp.co.fmap.nfc.tag4.SelectFile;
import jp.co.fmap.util.StringUtil;

/**
 * Created by wuv1982 on 2016/12/06.
 */

public class FragmentValueAddedService extends Fragment {

    private List<String> validAidList;

    private void searchAID() {

        final String rid = "A000000559";

        validAidList = new ArrayList<>();
        MainActivity context = (MainActivity) getActivity();
        Tag tag = context.getNfcTag();
        SelectFile selectFileCmd = new SelectFile();

        for (int applicationId = 0x0001; applicationId < 0xFFFF; applicationId ++) {
            String aid = rid + Integer.toHexString(applicationId);
            SelectFile.Request request = selectFileCmd.new Request(aid);
            SelectFile.Response response = request.transeive(tag);
            if (response != null && response.success()) {
                Log.i(MainActivity.TAG, "valid AID: " + aid);
                validAidList.add(aid);
                break;
            }
        }

    }
}
