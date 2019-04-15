package com.hitherejoe.tabby.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hitherejoe.tabby.R;

public class WebViewActivity extends BaseActivity {

    private WebView mWebView;

    public static final String EXTRA_URL =
            "com.hitherejoe.tabby.ui.activity.WebViewActivity.EXTRA_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = findViewById(R.id.web_view);
        String url = getIntent().getStringExtra(EXTRA_URL);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        setupActionBar(url);
    }

    private void setupActionBar(String url) {
        setTitle(url);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }
}