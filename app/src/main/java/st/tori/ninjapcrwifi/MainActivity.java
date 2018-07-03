package st.tori.ninjapcrwifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity implements MDNSResolver.MDNSResolverListener {


    private static final String TAG = "NinjaPCR";

    private WebView mWebView;
    private MDNSResolver mResolver;
    private ImageButton reloadButton;
    private static final String CONSOLE_URL = "http://ninjapcr.tori.st/ja/console/index.html";
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient());
        loadWebUI();

        mResolver = new MDNSResolver(this);
        mResolver.setListener(this);

        reloadButton = (ImageButton)findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener((View v)->{
            loadWebUI();
        });
    }

    private void loadWebUI() {
        mWebView.loadUrl(CONSOLE_URL);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void resolveHost(String host) {
            mResolver.resolve(host);
        }
    }

    /* MDNSResolver.MDNSResolverListener */

    @Override
    public void onHostResolved(String hostName, String hostIP) {

        String url = "javascript:onHostResolved('" + hostIP + "')";
        mWebView.loadUrl(url);
    }

    @Override
    public void onResolveFailed() {
        mWebView.loadUrl("javascript:onResolveFailed()");

    }
}
