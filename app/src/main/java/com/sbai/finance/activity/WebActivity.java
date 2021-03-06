package com.sbai.finance.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sbai.finance.AppJs;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.system.JsModel;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.image.ImageUtils;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.httplib.CookieManger;
import com.umeng.analytics.MobclickAgent;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

public class WebActivity extends BaseActivity {
    public static final String TAG = "WebActivity";

    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";

    public static final String EX_URL = "url";
    public static final String EX_TITLE = "title";
    public static final String EX_HTML = "html";
    public static final String TITLE_BAR_BACKGROUND = "title_bar_background";
    public static final String TITLE_BAR_HAS_BOTTOM_SPLIT_LINE = "title_bar_has_bottom_split_line";
    public static final String TITLE_BAR_BACK_ICON = "title_bar_back_icon";
    public static final String TITLE_BAR_CENTER_TITLE_COLOR = "title_bar_center_title_color";


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.errorPage)
    LinearLayout mErrorPage;

    private boolean mLoadSuccess;
    protected String mPageUrl;
    protected String mRootUrl;
    protected String mTitle;
    protected String mPureHtml;
    private Set<String> mUrlSet;

    private BroadcastReceiver mNetworkChangeReceiver;
    private WebViewClient mWebViewClient;
    private AppJs mAppJs;
    private JsModel mJsModel;

    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    public WebView getWebView() {
        return mWebView;
    }

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            syncCookies(mPageUrl);
            loadPage();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        mNetworkChangeReceiver = new NetworkReceiver();
        mLoadSuccess = true;

        mUrlSet = new HashSet<>();

        initData(getIntent());
        initLoginReceiver();

        initWebView();

        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.scrollTo(0, 0);
            }
        });

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mJsModel != null) {
                    mAppJs.openShareDialog(mJsModel.getTitle(), mJsModel.getDescription(), mJsModel.getShareUrl(), mJsModel.getShareThumbnailUrl(), "", getString(R.string.share_to));
                }
            }
        });
    }

    private void initLoginReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.reload();    //粗暴的处理方式。。。。。。。
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearHistory();
        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
        mAppJs = null;
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mLoginReceiver);
    }

    @Override
    public void onBackPressed() {
        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        try {
            if (!TextUtils.isEmpty(mRootUrl) && mRootUrl.equalsIgnoreCase(mPageUrl)) {
                super.onBackPressed();

            } else if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        } catch (IllegalStateException e) {
            finish();
        }

    }


    protected void initData(Intent intent) {
        mTitle = intent.getStringExtra(EX_TITLE);
        mPageUrl = intent.getStringExtra(EX_URL);
        mPureHtml = intent.getStringExtra(EX_HTML);
        tryToFixPageUrl();
        mRootUrl = mPageUrl;

        int titleBarBackground = intent.getIntExtra(TITLE_BAR_BACKGROUND, Color.WHITE);
        boolean titleBarHasBottomSplitLine = intent.getBooleanExtra(TITLE_BAR_HAS_BOTTOM_SPLIT_LINE, true);
        int backIcon = intent.getIntExtra(TITLE_BAR_BACK_ICON, -1);
        ColorStateList colorStateList = intent.getParcelableExtra(TITLE_BAR_CENTER_TITLE_COLOR);
        if (colorStateList != null) {
            mTitleBar.setTitleColor(colorStateList);
        }
        if (backIcon != -1) {
            mTitleBar.setBackButtonIcon(backIcon);
        }
        mTitleBar.setHasBottomSplitLine(titleBarHasBottomSplitLine);
        mTitleBar.setBackgroundColor(titleBarBackground);
    }

    private void tryToFixPageUrl() {
        if (!TextUtils.isEmpty(mPageUrl)) {
            if (!mPageUrl.startsWith("http")) { // http or https
                mPageUrl = "http://" + mPageUrl;
            }
        }
    }

    protected void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // init cookies
        syncCookies(mPageUrl);

        // init webSettings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString(webSettings.getUserAgentString()
                + " ###" + getString(R.string.android_web_agent) + "/1.0");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //mWebView.getSettings().setAppCacheEnabled(true);l
        //webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setAllowFileAccess(true);

        // performance improve
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);
        mAppJs = new AppJs(this);
        mWebView.addJavascriptInterface(mAppJs, "AppJs");

        // 5.0以下的手机就不要开硬件加速了。部分19手机开了硬件加速会有问题
        if (!isFlyme()) {     //魅族max 一旦打开web页面 应用的动画就会出问题  并且max4使用
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
        mWebViewClient = new WebViewClient();
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgress.setVisibility(View.GONE);
                } else {
                    if (mProgress.getVisibility() == View.GONE) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                    mProgress.setProgress(newProgress);
                }
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = mWebView.getHitTestResult();
                if (result != null) {
                    int type = result.getType();
                    if (type == WebView.HitTestResult.IMAGE_TYPE) {
                        showSaveImageDialog(result);
                    }
                }
                return false;
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        loadPage();
    }

    @Override
    public boolean shouldUpRecreateTask(Intent targetIntent) {
        return super.shouldUpRecreateTask(targetIntent);
    }

    protected void syncCookies(String pageUrl) {
        String rawCookie = CookieManger.getInstance().getRawCookie();
        Log.d(TAG, "syncCookies: " + rawCookie + ", " + pageUrl);

        if (!TextUtils.isEmpty(rawCookie) && !TextUtils.isEmpty(pageUrl)) {
            CookieManager.getInstance().setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().acceptThirdPartyCookies(mWebView);
            }
            String[] cookies = rawCookie.split("\n");
            for (String cookie : cookies) {
                CookieManager.getInstance().setCookie(pageUrl, cookie);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            }
            Log.d(TAG, "getCookies: " + CookieManager.getInstance().getCookie(pageUrl));
            boolean sync = !TextUtils.isEmpty(CookieManager.getInstance().getCookie(pageUrl));
            Log.d(TAG, "syncCookies: " + sync);
        }
    }

    protected void loadPage() {
        if (!TextUtils.isEmpty(mPageUrl)) {
            mWebView.loadUrl(mPageUrl);
        } else if (!TextUtils.isEmpty(mPureHtml)) {
            openWebView(mPureHtml);
        }
    }

    private void openWebView(String urlData) {
        String content;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = INFO_HTML_META + "<body>" + mPureHtml + "</body>";
        } else {
            getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = getHtmlData(urlData);
        }
        getWebView().loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    public void showRightView(String text, final String url, final boolean isNeedLogin) {
        mUrlSet.add(mPageUrl);
        mTitleBar.setRightVisible(true);
        mTitleBar.setRightViewEnable(true);
        mTitleBar.setRightText(text);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNeedLogin) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, url)
                            .execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
    }

    public void hideRightView() {
        mTitleBar.setRightVisible(false);
        mTitleBar.setRightViewEnable(false);
        mTitleBar.setRightImageViewVisible(false);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style>" + INFO_HTML_META + "</head>";
        return "<html>" + head + bodyHTML + "</html>";
    }

    public void controlTitleBarRightView(boolean rightViewIsShow, int type, String rightViewContent, JsModel content) {
        Log.d(TAG, "controlTitleBarRightView: " + rightViewIsShow + " " + mPageUrl);
        mUrlSet.add(mPageUrl);
        mJsModel = content;
        mTitleBar.setRightVisible(rightViewIsShow);
        mTitleBar.setRightViewEnable(rightViewIsShow);
        mTitleBar.setRightImageViewVisible(rightViewIsShow);
        switch (type) {
            case AppJs.MULTIPART_TEXT:
                mTitleBar.setRightText(rightViewContent);
                break;
            case AppJs.MULTIPART_IMAGE:
                mTitleBar.setRightTextRightImage(rightViewContent);
                break;
        }
    }

    public void updateTitleText(String titleContent) {
        if (isNeedViewTitle()) {
            mTitle = titleContent;
            mTitleBar.setTitle(mTitle);
        }
    }

    protected class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mLoadSuccess = true;
            mPageUrl = url;

            if (!Network.isNetworkAvailable() && TextUtils.isEmpty(mPureHtml)) {
                mLoadSuccess = false;
                mWebView.stopLoading();
            }
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            if (sslError.getPrimaryError() == android.net.http.SslError.SSL_INVALID) {// 个别6.0 7.0手机ssl校验过程遇到了bug
                sslErrorHandler.proceed();
            } else {
                sslErrorHandler.cancel();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //友盟统计
            MobclickAgent.onPageStart(url);
            if (mLoadSuccess) {
                mWebView.setVisibility(View.VISIBLE);
                mErrorPage.setVisibility(View.GONE);
            } else {
                mWebView.setVisibility(View.GONE);
                mErrorPage.setVisibility(View.VISIBLE);
            }
            if (isNeedViewTitle()) {
                String titleText = view.getTitle();
                if (!TextUtils.isEmpty(titleText) && !url.contains(titleText)) {
                    mTitle = titleText;
                }
                mTitleBar.setTitle(mTitle);
            } else {
                mTitleBar.setTitle(mTitle);
            }
            if (!mUrlSet.contains(url)) {
                hideRightView();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String requestUrl = request.getUrl().toString();
                if (mPageUrl.equalsIgnoreCase(requestUrl) && error.getErrorCode() <= ERROR_UNKNOWN) {
                    mLoadSuccess = false;
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mPageUrl.equalsIgnoreCase(failingUrl) && errorCode <= ERROR_UNKNOWN) {
                mLoadSuccess = false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (onShouldOverrideUrlLoading(view, url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    protected boolean isNeedViewTitle() {
        return true;
    }

    public void screenShot() {
        mWebView.buildDrawingCache();
        Bitmap bitmap = mWebView.getDrawingCache();
        if (bitmap == null) {
            Picture snapShot = mWebView.capturePicture();
            bitmap = Bitmap.createBitmap(mWebView.getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            snapShot.draw(canvas);
        }
        if (bitmap == null) {
            Log.d(TAG, "获取图片失败");
            return;
        }
        ShareDialog.with(getActivity())
                .setTitle(getString(R.string.share_to))
                .hasFeedback(false)
                .hasWeiBo(false)
                .setBitmap(bitmap)
                .setShareImageOnly(true)
                .show();
        ImageUtils.saveImageToGallery(getApplicationContext(), bitmap);
    }

    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        mWebView.onPause();
    }

    private void showSaveImageDialog(WebView.HitTestResult result) {

    }

    @OnClick(R.id.refreshButton)
    public void onClick() {
        mWebView.reload();
    }

    private class NetworkReceiver extends Network.NetworkChangeReceiver {

        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE && !mLoadSuccess) {
                if (mWebView != null) {
                    mWebView.reload();
                }
            }
        }
    }
}
