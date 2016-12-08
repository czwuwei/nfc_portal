package jp.co.fmap.nfcportal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.fmap.nfc.tag3.Polling;
import jp.co.fmap.nfc.tag4.SelectFile;
import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/12/08.
 */

public class FragmentSearch extends MainActivity.PlaceholderFragment {

    public static final String PREF_FILED_ITEM_SET = "item_set";
    public static final String PREF_NAME_NFC = "NFC";
    public static final String PREF_FILED_LIST_MODE = "list_mode";
    private ListView listViewSearch;
    private Set<String> itemList;
    private ListMode mode;

    private enum ListMode {
        NONE, AID, SYSTEM, SERVICE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frm_search, container, false);
        listViewSearch = (ListView) rootView.findViewById(R.id.list_search);

        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME_NFC, Activity.MODE_PRIVATE);

        mode = ListMode.values()[prefs.getInt(PREF_FILED_LIST_MODE, ListMode.NONE.ordinal())];
        if (mode != ListMode.NONE) {
            itemList = prefs.getStringSet(PREF_FILED_ITEM_SET, null);
            if (itemList != null) {
                ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(itemList));
                listViewSearch.setAdapter(listAdapter);
                listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView textView = (TextView)view;
                        String text = textView.getText().toString();
                        Log.d(MainActivity.TAG, "on click " + text);

                        switch (mode) {
                            case AID:
                                MainActivity.sharedNfcModel.aid = text;
                                break;
                            case SYSTEM:
                                MainActivity.sharedNfcModel.systemCode = text;
                                break;
                            case SERVICE:
                                MainActivity.sharedNfcModel.serviceCode = text;
                            default:
                        }
                    }
                });
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(MainActivity.TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.action_search, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_aid:
                Log.d(MainActivity.TAG, "search aid");
                mode = ListMode.AID;
                searchAID();
                break;

            case R.id.action_search_services:
                Log.d(MainActivity.TAG, "search services");
                mode = ListMode.SERVICE;
                break;

            case R.id.action_system_code:
                Log.d(MainActivity.TAG, "search system codes");
                mode = ListMode.SYSTEM;
                searchSystemCodes();
                break;

            default:
                Log.d(MainActivity.TAG, "unknown action");

        }
        return true;
    }

    private void searchSystemCodes() {
        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... voids) {
                List<String> validSystemCodeList = new ArrayList<>();

                MainActivity context = (MainActivity) getActivity();
                Tag tag = context.getNfcTag();
                Polling pollingCmd = new Polling();

                List<String> maskList = new ArrayList<>();
                for (int systemMask = 0xFF00; systemMask < 0xFFFF; systemMask+= 1) {
                    maskList.add(StringUtil.prefix(Integer.toHexString(systemMask), "0000"));
                }

                for (String systemMask: maskList) {
                    Polling.Request request = pollingCmd.new Request();
                    Log.d(MainActivity.TAG, "system code mask: " + systemMask);
                    request.systemCodeMask = StringUtil.parseToByte(systemMask);
                    request.requestCode = StringUtil.parseToByte(Polling.ENUM_REQUEST_CODE[1])[0];
                    request.timeSlot = StringUtil.parseToByte(Polling.ENUM_TIME_SLOT[0])[0];
                    request.keepConnection = false;

                    Polling.Response response = request.transceive(tag);

                    if (response != null && response.idm != null) {
                        String foundSystemCode = StringUtil.hexString(response.requestedData);
                        Log.d(MainActivity.TAG, "found systemCode: " + foundSystemCode);
                        validSystemCodeList.add(foundSystemCode);
                    }
                }

                return validSystemCodeList;
            }

            @Override
            protected void onPostExecute(List<String> validSystemCodeList) {
                if (validSystemCodeList.size() > 0) {
                    saveItemList(validSystemCodeList);

                    ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, validSystemCodeList);
                    listViewSearch.setAdapter(listAdapter);
                } else {
                    Log.i(MainActivity.TAG, "not found System code");
                }
            }
        }.execute();
    }


    private void searchAID() {

        final String GSMA_VAS_RID = "A000000559";

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... objects) {
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
                    saveItemList(validAidList);

                    ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, validAidList);
                    listViewSearch.setAdapter(listAdapter);
                } else {
                    Log.i(MainActivity.TAG, "not found VAS AID");
                }
            }
        }.execute();

    }

    private void saveItemList(List<String> list) {
        itemList = new HashSet<String>(list);
        SharedPreferences pres = getActivity().getSharedPreferences(PREF_NAME_NFC, Activity.MODE_PRIVATE);
        pres.edit()
                .putStringSet(PREF_FILED_ITEM_SET, itemList)
                .putInt(PREF_FILED_LIST_MODE, mode.ordinal())
                .apply();

    }
}
