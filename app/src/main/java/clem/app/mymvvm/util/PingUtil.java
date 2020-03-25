package clem.app.mymvvm.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingUtil {
    private static final String TAG = "PingUtil";

    public static float startPing(String url) {
        float min = 0f;
        BufferedReader in = null;
        Runtime rt = Runtime.getRuntime();
        String pingCommand = "/system/bin/ping -c 3 " + url;
        try {
            Process pro = rt.exec(pingCommand);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));

            final StringBuilder sb = new StringBuilder();
            String line = in.readLine();

            while (line != null) {
                sb.append(line).append("\n");
//                Log.d(TAG, line + "\n");
                line = in.readLine();
            }
            String statics = sb.toString();
            if(!TextUtils.isEmpty(statics) && statics.contains("avg")){
                int index = statics.indexOf("mdev");
                String left = statics.substring(index);
                min = Float.valueOf(left.substring(7, left.indexOf("/")));
//                Log.d(TAG, "startPing: " + min);
            }
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return min;
    }
}
