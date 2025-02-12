package com.bj.gxz.jniapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bj.gxz.jniapp.bitmap.JNIBitmap;
import com.bj.gxz.jniapp.cb.INativeListener;
import com.bj.gxz.jniapp.cb.INativeThreadListener;
import com.bj.gxz.jniapp.cb.JNIThreadCallBack;
import com.bj.gxz.jniapp.crash.JNICrash;
import com.bj.gxz.jniapp.data.JNIData;
import com.bj.gxz.jniapp.exception.JNIException;
import com.bj.gxz.jniapp.methodfield.AppInfo;
import com.bj.gxz.jniapp.methodfield.JNIMethodField;
import com.bj.gxz.jniapp.ref.JNIRef;

/**
 * https://www.jianshu.com/p/8395ddf3df54
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "JNI";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.id_iv);
    }

    public void onMethodDynamic(View view) {
        JNIMethodDynamic jniMethodDynamic = new JNIMethodDynamic();
        Log.d(TAG, "stringFromJNI:" + jniMethodDynamic.stringFromJNI());
        Log.d(TAG, "sum:" + jniMethodDynamic.sum(1, 2));
    }

    public void onMethodCallBack(View view) {
        JNIThreadCallBack jniThreadCallBack = new JNIThreadCallBack();
        jniThreadCallBack.nativeCallBack(new INativeListener() {
            @Override
            public void onCall() {
                Log.d(TAG, "onCall invoked,threadName:" + Thread.currentThread().getName());
            }
        });
        jniThreadCallBack.nativeInThreadCallBack(new INativeThreadListener() {
            @Override
            public void onSuccess(String msg) {
                Log.d(TAG, "onSuccess invoked,msg:" + msg);
                Log.d(TAG, "onSuccess invoked,threadName:" + Thread.currentThread().getName());
            }
        });
    }

    public void onJniException(View view) {
        JNIException jniException = new JNIException();
        jniException.nativeInvokeJavaException();
        try {
            jniException.nativeThrowException();
        } catch (Exception e) {
            //  java.lang.RuntimeException: throw exception from c++ code
            e.printStackTrace();
            Log.e(TAG, "nativeThrowException Exception:", e);
        }
    }

    public void onJniCrash(View view) {
        new JNICrash().crash();
    }

    private JNIRef jniRef = new JNIRef();

    public void onJniRef(View view) {
        String s = jniRef.jnilocalRef();
        Log.e(TAG, "jnilocalRef=" + s);

        String ret1 = jniRef.jniGlobalRef();
        Log.e(TAG, "jniGlobalRef=" + ret1);
        String ret2 = jniRef.jniGlobalRef();
        Log.e(TAG, "jniGlobalRef=" + ret2);
        jniRef.delGlobalRef();

        String ret3 = jniRef.jniWeakGlobalRef();
        Log.e(TAG, "jniWeakGlobalRef=" + ret3);
        String ret4 = jniRef.jniWeakGlobalRef();
        Log.e(TAG, "jniWeakGlobalRef=" + ret4);
        jniRef.delWeakGlobalRef();

        jniRef.localRefOverflow();
        jniRef.refSame();

        String ret5 = jniRef.refCache();
        Log.e(TAG, "refCache=" + ret5);
        String ret6 = jniRef.refCache();
        Log.e(TAG, "refCache=" + ret6);
        jniRef.delRefCache();
    }

    public void onJniMethodField(View view) {
        JNIMethodField jniMethodField = new JNIMethodField();
        //native层获取java对象相关
        AppInfo javaInfo = new AppInfo("com.wg.com", 30);
        javaInfo.setSize(500);
        jniMethodField.getAppInfoFromJava(javaInfo);

        //native层构建java对象
        AppInfo info = jniMethodField.createAppInfoFromJni();
        Log.e(TAG, "info=" + info);
    }

    public void onJniData(View view) {
        new JNIData().data((byte)100, 'A', true, (short)100, 100, 100f, 100, 100,
            new float[] {1.0f, 2.1f, 3.3f});
    }

    public void onJniBitmap(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.normal);
        int color = bitmap.getPixel(0, 0);//Integer.toHexString 将十进制转成十六进制输出
        Log.e(TAG, "java getPixel[0][0] " + color + "=" + Integer.toHexString(color));

        JNIBitmap jniBitmap = new JNIBitmap();
        long start = System.currentTimeMillis();
        if (jniBitmap.negative(bitmap) == 1) {
            Log.e(TAG, "negative cost:" + (System.currentTimeMillis() - start));
            imageView.setImageBitmap(bitmap);
            int color2 = bitmap.getPixel(0, 0);
            Log.e(TAG, "java getPixel[0][0] " + color2 + "=" + Integer.toHexString(color2));
        }
        long start2 = System.currentTimeMillis();
        Bitmap newBitmap = jniBitmap.leftRight(bitmap);
        if (newBitmap != null) {
            Log.e(TAG, "leftRight cost:" + (System.currentTimeMillis() - start2));
            imageView.setImageBitmap(newBitmap);
        }
    }

}
