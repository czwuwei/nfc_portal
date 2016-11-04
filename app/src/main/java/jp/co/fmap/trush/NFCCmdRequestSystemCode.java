package jp.co.fmap.trush;

import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/02.
 * @see https://github.com/mokemokechicken/Android_NFC_FelicaEdit.git
 */

public class NFCCmdRequestSystemCode {

  private int length;
  private String cmdType = "0c";
  public byte[] idm;


  public byte[] toBytes() {
    StringBuffer cmdBuf = new StringBuffer();
    cmdBuf.append(cmdType);
    cmdBuf.append(StringUtil.hexString(idm));

    length = cmdBuf.length() / 2 + 1;
    cmdBuf.insert(0, StringUtil.hexString(length));

    return StringUtil.parseToByte(cmdBuf.toString());
  }
}
