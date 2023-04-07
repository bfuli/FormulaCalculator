package caculator.bianfl.cn.abccaculator.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import caculator.bianfl.cn.abccaculator.BuildConfig;
import caculator.bianfl.cn.abccaculator.beans.UpdateObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 福利 on 2016/10/1.
 */
public class UpdateUtils {
    private int localVersionCode, versionCode;
    private String updateMessage, downUrl, versionName;
    private boolean mustUpdate,isDebug;
    private Context context;
    private String filepath;
    private final String apkName = "caculator.apk";
    private String objectedId = "72889a1128";
    private long fileSize;
    private String UPDATE_TAG = "BZ";
    private BmobFile bmobFile;
    private boolean notify;

    public UpdateUtils(Context context) {
        this.context = context;
        File dir = context.getExternalFilesDir(null);
        if (dir != null){
            filepath = dir.getPath();
            System.out.println("filepath="+filepath);
        }else{
//            filepath = "/storage/emulated/0/Android/data/caculator.bianfl.cn.abccaculator/files";
            filepath = context.getFilesDir().getPath();
        }

    }

    //检查更新完毕
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //有新版本 且非测试版
            if (localVersionCode < versionCode && !isDebug) {
//            if (true) {
                DecimalFormat format = new DecimalFormat("#.##");
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(context).
                        setTitle("新版本" + versionName + "(" + format.format(fileSize * 1.0 / 1024 / 1024) + "MB)").
                        setMessage(updateMessage).
                        setCancelable(false).
                        setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //判断网络再下载
                                judgeThenDown();
                            }
                        });
                if (mustUpdate) {
                    dialog = builder.create();
                } else {
                    dialog = builder.setNegativeButton("暂不更新", null).create();
                }
                //修改字体风格
                StyleUtil.allpyDialogStyle(context, dialog);

            } else {
                if (notify) {
                    ToastUtil.showToast(context, "暂无更新" + ToastUtil.ENJOY_CUTE);
                }
            }
        }
    };

    //检查更新
    public void checkUpdate(boolean notify) {
        this.notify = notify;
        //当前版本号
        localVersionCode = getVersionCode(context.getApplicationContext());
        new Thread(() -> {
            BmobQuery<UpdateObject> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("perUpdateId", UPDATE_TAG);
            bmobQuery.findObjects(new FindListener<UpdateObject>() {
                @Override
                public void done(List<UpdateObject> list, BmobException e) {
                    if (e == null) {
                        try {
                            UpdateObject object = list.get(0);
                            versionCode = object.getVersionCoad();
                            updateMessage = object.getUpdateMessage();
                            mustUpdate = object.isMustUpdate();
                            isDebug = object.isDebug();
                            bmobFile = object.getFile();
                            versionName = object.getVersionName();
                            fileSize = object.getFileSize();//获取文件大小
                            handler.sendEmptyMessage(0);
                        } catch (Exception es) {
                            if (notify) {
                                notifys();
                            }
                            es.printStackTrace();
                        }
                    } else {
                        if (notify) {
                            notifys();
                        }
                        e.printStackTrace();
                    }
                }
            });
        }).start();
    }


    //下载文件
    private void downFile() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("下载中...");
        progressDialog.show();
        final File file = new File(filepath,apkName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bmobFile.download(file, new DownloadFileListener() {
            @Override
            public void onProgress(Integer integer, long l) {
                progressDialog.setProgress(integer);
            }

            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    //安装文件
                    progressDialog.dismiss();
                    Uri uri;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(context.getApplicationContext(),
                                BuildConfig.APPLICATION_ID, file);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    }else{
                        uri = Uri.fromFile(file);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    }
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    context.startActivity(intent);
                } else {
                    ToastUtil.showToast(context, "下载失败" + ToastUtil.ENJOY_SAD);
                    if (file.exists()) {
                        file.delete();
                    }
                    progressDialog.dismiss();
                }
            }
        });
    }

    //判断网络状态
    private void judgeThenDown() {
        //网络状态判断
        int neti = NetUtils.getNetKind(context.getApplicationContext());
        //WIFI网络
        if (neti == ConnectivityManager.TYPE_WIFI) {
            downFile();
        } else if (neti == ConnectivityManager.TYPE_MOBILE) {
            //数据流量网络
            AlertDialog dialog = new AlertDialog.Builder(context).
                    setMessage("您当前正在使用数据流量，继续下载会消耗流量，是否继续？").
                    setCancelable(false).
                    setNegativeButton("取消", null).
                    setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //下载文件
                            downFile();
                        }
                    }).create();
            //修改字体风格
            StyleUtil.allpyDialogStyle(context, dialog);
        } else {
            ToastUtil.showToast(context, "当前无网络连接，请稍后重试" + ToastUtil.ENJOY_CUTE);
        }
    }

    //获取本地版本号
    private int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private PackageInfo getPackageInfo(Context context) {
        PackageInfo packageInfo = null;

        PackageManager manager = context.getPackageManager();
        try {
            packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;

    }

    private void notifys() {
        ToastUtil.showToast(context, "检查更新失败" + ToastUtil.ENJOY_SAD);
    }
}
