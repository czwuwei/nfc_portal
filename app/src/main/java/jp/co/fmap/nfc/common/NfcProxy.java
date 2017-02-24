package jp.co.fmap.nfc.common;

import android.content.Context;
import android.content.Intent;

/**
 * Created by z00066 on 2017/02/23.
 */

public interface NfcProxy {
    public void onNfcIntent(Context context, Intent intent);
}
