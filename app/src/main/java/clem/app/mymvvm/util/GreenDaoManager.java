package clem.app.mymvvm.util;


import android.database.Cursor;


import com.clem.mymvvm.gen.DaoMaster;
import com.clem.mymvvm.gen.DaoSession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import clem.app.mymvvm.App;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import jonathanfinerty.once.Once;


/**
 * Created by laileon on 2017/2/8.
 */

public class GreenDaoManager {
    private final static String dbName = "covid";
    private static GreenDaoManager mInstance;
    private DaoSession mDaoSession;

    private GreenDaoManager() {
        init();
    }

    /**
     * 对外唯一实例的接口
     *
     * @return
     */
    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void init() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(App.Companion.getCONTEXT(),
                dbName);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getmDaoSession() {
        return mDaoSession;
    }

    public static List<String> listItem(DaoSession session, String rawQuery) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(rawQuery, null);
        try{
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

//    public static void unzipDB() {
//        Observable.create(emitter -> {
//            try {
//                InputStream is = App.Companion.getCONTEXT().getAssets().open("covid.zip");
//
//                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
//
//                while ((zis.getNextEntry()) != null) {
//                    int size;
//                    byte[] buffer = new byte[1024 * 2];
//
//                    OutputStream fos = new FileOutputStream(App.Companion.getCONTEXT().getDatabasePath("covid").getAbsolutePath());
//                    BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
//
//                    while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
//                        bos.write(buffer, 0, size);
//                    }
//                    bos.flush();
//                    bos.close();
//                }
//                zis.close();
//                is.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).subscribeOn(Schedulers.io()).subscribe();
//    }

}
