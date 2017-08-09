package com.sbai.finance.activity.discovery;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
import com.sbai.finance.fragment.dialog.ShareReportDialogFragment;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Network;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * 乐米日报详情
 */

public class DailyReportDetailActivity extends BaseActivity {
    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";
    public static final String EX_ID = "id";
    public static final String EX_RAW_COOKIE = "rawCookie";
    public static final String EX_FORMAT = "format";
    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.click)
    TextView mClick;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.sourceAndTime)
    TextView mSourceAndTime;
    @BindView(R.id.titleArea)
    LinearLayout mTitleArea;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.refreshButton)
    Button mRefreshButton;
    @BindView(R.id.errorPage)
    LinearLayout mErrorPage;
    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.share)
    TextView mShare;
    @BindView(R.id.bottom)
    LinearLayout mBottom;

    private boolean mLoadSuccess;
    protected String mPageUrl;
    protected String mRawCookie;
    protected String mPureHtml;
    private String mId;
    private int mFormat;

    private BroadcastReceiver mNetworkChangeReceiver;
    private WebViewClient mWebViewClient;


    public String getRawCookie() {
        return mRawCookie;
    }

    public WebView getWebView() {
        return mWebView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report_detail);
        ButterKnife.bind(this);
        mNetworkChangeReceiver = new NetworkReceiver();
        mLoadSuccess = true;
        initData(getIntent());
        initView();
        initWebView();
        requestDailyReportDetail();
    }

    private void requestDailyReportDetail() {
        if (TextUtils.isEmpty(mId)) return;
        Client.getDailyReportDetail(mId).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<DailyReport>, DailyReport>() {
                    @Override
                    protected void onRespSuccessData(DailyReport data) {
                        updateDailyReportData(data);
                    }
                }).fireFree();
    }

    private void updateDailyReportData(DailyReport data) {
        if (data.isHtml()) {
            mTitleArea.setVisibility(View.VISIBLE);
            mClick.setText(getString(R.string.read_count, data.getClicks()));
            Glide.with(getActivity())
                    .load(data.getCoverUrl())
                    .into(mImage);
            mTitle.setText(data.getTitle());
            mSourceAndTime.setText(getString(R.string.source_and_time, data.getSource(), DateUtil.getFormatTime(data.getCreateTime())));
            mPureHtml = data.getContent();
        } else {
            mTitleArea.setVisibility(View.GONE);
            mPageUrl = data.getContent();
        }
        loadPage();
    }

    private void initView() {
        if (mFormat == DailyReport.HTML) {
            mTitleArea.setVisibility(View.GONE);
        }
    }

    protected void initData(Intent intent) {
        mRawCookie = intent.getStringExtra(EX_RAW_COOKIE);
        mId = intent.getStringExtra(EX_ID);
        mFormat = intent.getIntExtra(EX_FORMAT, 0);
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

    @OnClick({R.id.back, R.id.share, R.id.refreshButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.share:
                ShareReportDialogFragment.newInstance().show(getSupportFragmentManager());
                break;
            case R.id.refreshButton:
                mWebView.reload();
                break;
        }
    }

    protected void initWebView() {
        // init cookies
        initCookies(mRawCookie, mPageUrl);

        // init webSettings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString(webSettings.getUserAgentString()
                + " ###" + getString(R.string.android_web_agent) + "/1.0");
        //mWebView.getSettings().setAppCacheEnabled(true);l
        //webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setAllowFileAccess(true);

        // performance improve
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setBackgroundColor(0);
        //硬件加速 有些API19手机不支持
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以下 默认同时加载http和https
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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
//                        showSaveImageDialog(result);
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

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style>" + INFO_HTML_META + "</head>";
        return "<html>" + head + bodyHTML + "</html>";
    }

    protected void initCookies(String rawCookie, String pageUrl) {
        if (!TextUtils.isEmpty(rawCookie) && !TextUtils.isEmpty(pageUrl)) {
            String[] cookies = rawCookie.split("\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().removeSessionCookies(null);
            } else {
                CookieManager.getInstance().removeAllCookie();
            }
            CookieManager.getInstance().setAcceptCookie(true);
            for (String cookie : cookies) {
                CookieManager.getInstance().setCookie(pageUrl, cookie);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }
        }
    }

    protected class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mLoadSuccess = true;
            mPageUrl = url;

            if (!Network.isNetworkAvailable()) {
                mLoadSuccess = false;
                mWebView.stopLoading();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mLoadSuccess) {
                mWebView.setVisibility(View.VISIBLE);
                mErrorPage.setVisibility(View.GONE);
            } else {
                mWebView.setVisibility(View.GONE);
                mErrorPage.setVisibility(View.VISIBLE);
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

    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        return false;
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
