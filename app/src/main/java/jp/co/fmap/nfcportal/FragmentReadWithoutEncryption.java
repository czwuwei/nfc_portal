package jp.co.fmap.nfcportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import jp.co.fmap.nfc.f.NfcFCommand;
import jp.co.fmap.nfc.f.ReadWithoutEncryption;
import jp.co.fmap.trush.NFCCmdReadWithNoSecurity;
import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/25.
 */

public class FragmentReadWithoutEncryption extends MainActivity.PlaceholderFragment {

  private EditText edtServiceCodeList;
  private SeekBar seekBarBlockCount;
  private SeekBar seekBarServiceCodeOrder;
  private RadioGroup radioGroupBlockLength;
  private RadioGroup radioGroupBlockAccessMode;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.frm_read_without_encryption, container, false);

    edtServiceCodeList = (EditText) rootView.findViewById(R.id.edtServiceCodes);
    seekBarBlockCount = (SeekBar) rootView.findViewById(R.id.seekBar_blockCount);
    seekBarBlockCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getActivity(), "block count : " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
      }
    });
    seekBarServiceCodeOrder = (SeekBar) rootView.findViewById(R.id.seekBar_service_code_order);
    seekBarServiceCodeOrder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getActivity(), "system code index : " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
      }
    });
    radioGroupBlockLength = (RadioGroup) rootView.findViewById(R.id.radioGroup_block_length);
    radioGroupBlockAccessMode = (RadioGroup) rootView.findViewById(R.id.radioGroup_block_access_mode);

    return rootView;
  }


  @Override
  protected NfcFCommand.Request genNfcCommand() {
    ReadWithoutEncryption.Request request = new ReadWithoutEncryption().new Request();

    request.serviceCodeList = StringUtil.parseToByte(edtServiceCodeList.getText().toString().replace(",", ""));
    request.serviceOrderIndex = seekBarServiceCodeOrder.getProgress();
    request.numberOfBlock = seekBarBlockCount.getProgress();

    if (this.radioGroupBlockLength.getCheckedRadioButtonId() == R.id.radioButton_2bytes) {
      request.blockIndexLengthType = NFCCmdReadWithNoSecurity.BLOCK_INDEX_LENGTH_2;
    } else if (this.radioGroupBlockLength.getCheckedRadioButtonId() == R.id.radioButton_3bytes) {
      request.blockIndexLengthType = NFCCmdReadWithNoSecurity.BLOCK_INDEX_LENGTH_3;
    }
    if (this.radioGroupBlockAccessMode.getCheckedRadioButtonId() == R.id.radioButton_withoutCache) {
      request.blockAccessMode = NFCCmdReadWithNoSecurity.BLOCK_ACCESS_MODE_CACHE_EXCLUDE;
    } else if (this.radioGroupBlockAccessMode.getCheckedRadioButtonId() == R.id.radioButton_withCache) {
      request.blockAccessMode = NFCCmdReadWithNoSecurity.BLOCK_ACCESS_MODE_CACHE;
    }

    return request;
  }
}
