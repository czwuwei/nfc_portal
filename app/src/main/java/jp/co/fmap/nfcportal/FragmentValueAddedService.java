package jp.co.fmap.nfcportal;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jp.co.fmap.nfc.tag4.SelectFile;
import jp.co.fmap.util.StringUtil;

/**
 * Created by wuv1982 on 2016/12/06.
 */

public class FragmentValueAddedService extends MainActivity.PlaceholderFragment {

    private ListView aidListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frm_aid_list, container, false);

        aidListView = (ListView) rootView.findViewById(R.id.list_aid);
        Button btnSearchAid = (Button) rootView.findViewById(R.id.btn_searchAid);
        btnSearchAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAID();
            }
        });
        return rootView;
    }

    private void searchAID() {

        final String GSMA_VAS_RID = "A000000559";

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void[] objects) {
                List<String> validAidList = new ArrayList<>();
                MainActivity context = (MainActivity) getActivity();
                Tag tag = context.getNfcTag();
                SelectFile selectFileCmd = new SelectFile();

                for (int applicationId = 0x0001; applicationId < 0xFFFF; applicationId++) {
                    String aid = GSMA_VAS_RID + StringUtil.prefix(Integer.toHexString(applicationId), "0000");
                    SelectFile.Request request = selectFileCmd.new Request(aid);
                    SelectFile.Response response = request.transceive(tag);
                    if (response != null && response.success()) {
                        Log.i(MainActivity.TAG, "valid AID: " + aid);
                        validAidList.add(aid);
                    }
                }
                return validAidList;
            }

            @Override
            protected void onPostExecute(List<String> validAidList) {
                if (validAidList.size() > 0) {
                    ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, validAidList);
                    aidListView.setAdapter(listAdapter);
                } else {
                    Log.i(MainActivity.TAG, "not found VAS AID");
                }
            }
        }.execute();

    }
}
