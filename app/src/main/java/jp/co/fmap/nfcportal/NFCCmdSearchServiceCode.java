package jp.co.fmap.nfcportal;

import jp.co.fmap.util.StringUtil;

/**
 * Created by z00066 on 2016/11/02.
 * @see http://nfcpy.readthedocs.io/en/latest/modules/tag.html
 */

public class NFCCmdSearchServiceCode {
  private int length;
  private String cmdType = "0a";
  public byte[] idm;

  public int serviceIndex;

  public byte[] toBytes() {
    StringBuffer cmdBuf = new StringBuffer();
    cmdBuf.append(cmdType);
    cmdBuf.append(StringUtil.hexString(idm));

    cmdBuf.append(StringUtil.hexString(serviceIndex & 0xff));
    cmdBuf.append(StringUtil.hexString(serviceIndex >> 8));


    length = cmdBuf.length() / 2 + 1;
    cmdBuf.insert(0, StringUtil.hexString(length));
    return StringUtil.parseToByte(cmdBuf.toString());
  }
}
