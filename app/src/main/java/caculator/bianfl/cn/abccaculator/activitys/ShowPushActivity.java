package caculator.bianfl.cn.abccaculator.activitys;

import android.os.Bundle;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import caculator.bianfl.cn.abccaculator.R;
import caculator.bianfl.cn.abccaculator.utils.CommonActions;

/**
 * Created by 福利 on 2017/10/12.
 */

public class ShowPushActivity extends AppCompatActivity {

    private ContentLoadingProgressBar progress;
    private WebView wv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpush);
        CommonActions actions = new CommonActions(this);
        actions.immerse();//沉浸式状态栏
        actions.finish();//点击返回键推出界面
        actions.setTitle("隐私政策");
        wv_message = (WebView) findViewById(R.id.wv_message);
        progress = (ContentLoadingProgressBar) findViewById(R.id.progress);

        wv_message.getSettings().setJavaScriptEnabled(true);
        wv_message.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("msg");
        if (message.startsWith("http")) {  //使用文本文件读取
            openUrl(message);
        } else {
            loadTxt(message);
        }
    }

    private void openUrl(String murl) {
        wv_message.loadUrl(murl);
        wv_message.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wv_message.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(newProgress);
                if (newProgress == 0 || newProgress == 100) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadTxt(String mes) {
        wv_message.loadData(mes, "text/html", "utf-8");
    }

}
