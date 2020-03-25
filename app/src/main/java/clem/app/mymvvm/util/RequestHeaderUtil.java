package clem.app.mymvvm.util;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import clem.app.mymvvm.App;

import java.lang.reflect.Method;
import java.util.UUID;

public class RequestHeaderUtil {
    public static final String IDENTITY = "identity";

    public static String getSerialNumber() {
        String serialNumber = "";

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (TextUtils.isEmpty(serialNumber))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (TextUtils.isEmpty(serialNumber))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (TextUtils.isEmpty(serialNumber))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (TextUtils.isEmpty(serialNumber))
                serialNumber = Build.SERIAL;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serialNumber;
    }

    public static String getAndroidId() {
        String androidId = Settings.Secure.getString(App.Companion.getCONTEXT().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidId)) {
            androidId = "";
        }
        return androidId;
    }

    public static String getHeaderParam() {
        if (TextUtils.isEmpty(getAndroidId()) && TextUtils.isEmpty(getSerialNumber())) {
            return getIdentity();
        } else {
            return getAndroidId() + "|" + getSerialNumber();
        }
    }

    private static String getIdentity() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(App.Companion.getCONTEXT());
        String identity = preference.getString(IDENTITY, "");
        if (TextUtils.isEmpty(identity)) {
            identity = UUID.randomUUID().toString();
            preference.edit().putString(IDENTITY, identity).apply();
        }
        return identity;
    }
}
