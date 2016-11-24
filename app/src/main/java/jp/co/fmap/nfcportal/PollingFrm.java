package jp.co.fmap.nfcportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by z00066 on 2016/11/24.
 */

public class PollingFrm extends MainActivity.PlaceholderFragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.frm_polling, container, false);
    return rootView;
  }
}
