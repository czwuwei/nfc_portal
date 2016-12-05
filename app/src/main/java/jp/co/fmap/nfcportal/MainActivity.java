package jp.co.fmap.nfcportal;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import jp.co.fmap.nfc.tag3.NfcFCommand;

import static jp.co.fmap.util.CollectionUtil.mkString;
import static jp.co.fmap.util.StringUtil.hexString;
import static jp.co.fmap.util.StringUtil.utfString;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

  public static String TAG = "WNFC";
  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private NavigationDrawerFragment mNavigationDrawerFragment;
  /**
   * Used to store the last screen title. For use in {@link #restoreActionBar()}.
   */
  private CharSequence mTitle;
  private PendingIntent pendingIntent;
  private NfcAdapter mAdapter;
  private Tag tag;
  private NfcF nfcF;
  private PlaceholderFragment fragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

    mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
    mTitle = getTitle();

    // Set up the drawer.
    mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    mAdapter = NfcAdapter.getDefaultAdapter(this);
    Log.d(TAG, "onCreate");
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mAdapter != null) mAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
  }

  public Tag getNfcTag() {
    return tag;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(TAG, "onNewIntent");
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
      Log.d(TAG, "found ACTION_NDEF_DISCOVERED");
      for (Parcelable rawMsg : intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
        NdefMessage msg = (NdefMessage) rawMsg;
        for (NdefRecord record : msg.getRecords()) {
          Log.d(TAG, "type: " + hexString(record.getType()));
          Log.d(TAG, "id: " + hexString(record.getId()));
          Log.d(TAG, "payload: " + utfString(record.getPayload()));
        }
      }
    } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
      tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
      Log.d(TAG, "id: " + hexString(tag.getId()));
      Log.d(TAG, "techList: " + mkString(tag.getTechList()));
      nfcF = NfcF.get(tag);
      Log.d(TAG, "manufacturer: " + hexString(nfcF.getManufacturer()));
      Log.d(TAG, "systemCode: " + hexString(nfcF.getSystemCode()));
      Log.d(TAG, "length: " + nfcF.getMaxTransceiveLength());
      Log.d(TAG, "timeout: " + nfcF.getTimeout());
      Toast.makeText(this, "NFC enabled!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onNavigationDrawerItemSelected(int position) {
    // update the main content by replacing fragments
    FragmentManager fragmentManager = getFragmentManager();

    switch (position) {
      case 0:
        fragment = new FragmentPolling();
        break;
      case 1:
        fragment = new FragmentReadWithoutEncryption();
        break;
      default:
        fragment = new PlaceholderFragment();
    }

    Bundle args = new Bundle();
    args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, position + 1);
    fragment.setArguments(args);

    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
  }

  public void onSectionAttached(int number) {
    switch (number) {
      case 1:
        mTitle = getString(R.string.title_section1);
        break;
      case 2:
        mTitle = getString(R.string.title_section2);
        break;
      case 3:
        mTitle = getString(R.string.title_section3);
        break;
    }
    restoreActionBar();
  }

  public void restoreActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setTitle(mTitle);
  }

  public void onBtnTransceive(View view) {
    Log.d(MainActivity.TAG, "onBtnTransceive");

    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) {

        NfcFCommand.Request req = fragment.genNfcCommand(tag);
        if (tag != null) {
          NfcFCommand.Response response = req.transceive(tag);
          Log.d(MainActivity.TAG, response.toString());
        }
        return null;
      }
    }.execute();
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
      return rootView;
    }

    @Override
    public void onAttach(Context activity) {
      super.onAttach(activity);
      ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    protected NfcFCommand.Request genNfcCommand(Tag tag) {
      return null;
    }
  }

}
