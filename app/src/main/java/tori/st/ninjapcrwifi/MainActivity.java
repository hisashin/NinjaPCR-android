package tori.st.ninjapcrwifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity implements MDNSResolver.MDNSResolverListener {


    private static final String TAG = "NinjaPCR";

    private WebView mWebView;
    private MDNSResolver mResolver;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.loadUrl("http://ninjapcr.tori.st/ja/console/index.html");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.getSettings().setJavaScriptEnabled(true);

        mResolver = new MDNSResolver(this);
        mResolver.setListener(this);
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
