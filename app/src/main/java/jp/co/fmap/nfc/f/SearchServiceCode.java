package jp.co.fmap.nfc.f;

/**
 * Created by z00066 on 2016/11/28.
 * @see http://nfcpy.readthedocs.io/en/latest/modules/tag.html
 */

public class SearchServiceCode extends NfcFCommand {
  protected SearchServiceCode() {
    super((byte)0x00, (byte)0x00);
  }
}
