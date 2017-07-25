package ls.com.paydemo;

import android.util.Log;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by danny on 17/07/2017.
 */

public class SignUtil {

    private static final String TAG = "SignUtil";

    public static String getLsPaySign(Map<String, String> fields, String lsSecret) {
        try {

            List<String> keys = new ArrayList<String>();
            Set<String> keySet = fields.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                if (fields.get(key) != null && !"".equals(fields.get(key).toString())) {
                    keys.add(key);
                }
            }

            String[] keyArr = keys.toArray(new String[keys.size()]);
            Arrays.sort(keyArr);

            StringBuilder sb = new StringBuilder();
            for (String key : keyArr) {
                sb.append(key + "=" + fields.get(key));
                sb.append("&");
            }
            sb.append("key=" + lsSecret);

            String content = sb.toString();

            Log.e(TAG, String.format("-------->content:%s", sb.toString()));

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] digest = md.digest(content.getBytes());
            String rsStr = byteToStr(digest);
            Log.e(TAG, String.format("--------> sign: %s", rsStr.toUpperCase()));
            return rsStr.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
