package clem.app.mymvvm.util;

import android.widget.Toast;
import clem.app.mymvvm.App;

public class ToastUtil {
    public static void showShortToast(String message){
        Toast.makeText(App.Companion.getCONTEXT(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message){
        Toast.makeText(App.Companion.getCONTEXT(), message, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(int resId){
        Toast.makeText(App.Companion.getCONTEXT(), App.Companion.getCONTEXT().getText(resId), Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(int resId){
        Toast.makeText(App.Companion.getCONTEXT(), App.Companion.getCONTEXT().getText(resId), Toast.LENGTH_LONG).show();
    }
}
