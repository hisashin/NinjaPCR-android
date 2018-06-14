package tori.st.ninjapcrwifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity implements NsdManager.DiscoveryListener, NsdManager.ResolveListener {


    private static final String TAG = "NinjaPCR";

    private WebView mWebView;

    private NsdManager mNsdManager;

    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String SERVICE_NAME = "ninjapcr";
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void resolveHost(String host) {
            findDevice(host);
        }
    }
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

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

    }
    private void findDevice (String hostName) {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, this);
        // TODO timeout
    }

    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.d(TAG, "onStartDiscoveryFailed");

    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.d(TAG, "onStopDiscoveryFailed");

    }

    @Override
    public void onDiscoveryStarted(String serviceType) {
        Log.d(TAG, "onDiscoveryStarted");

    }
    NsdServiceInfo serviceInfo;
    @Override
    public void onDiscoveryStopped(String serviceType) {
        Log.d(TAG, "onDiscoveryStopped");
        mNsdManager.resolveService(serviceInfo, this);

    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "onServiceFound " + serviceInfo.getServiceName() + ", " + serviceInfo.getHost());
        this.serviceInfo = serviceInfo;
        mNsdManager.stopServiceDiscovery(this);

    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "onServiceLost");

    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.d(TAG, "onResolveFailed");

    }

    @Override
    public void onServiceResolved(final NsdServiceInfo serviceInfo) {
        Log.d(TAG, "onServiceResolved " + serviceInfo);

        Log.d(TAG, "onServiceResolved " + serviceInfo.getHost().toString().replace("/",""));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String url = "javascript:onHostResolved('" + serviceInfo.getHost().toString().replace("/","") + "')";
                mWebView.loadUrl(url);

            }
        });
    }
}
