package com.alimuzaffar.ramadanalarm;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alimuzaffar.ramadanalarm.databinding.ActivityTermsAndConditionsBinding;

public class TermsAndConditionsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_DISPLAY_ONLY = "extra_display_only";

    private ActivityTermsAndConditionsBinding mBinding;
    private boolean displayOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_conditions);
        mBinding.btnAgree.setOnClickListener(this);
        mBinding.scrollView.getSettings().setJavaScriptEnabled(true);
        mBinding.scrollView.loadUrl("https://cdn.rawgit.com/alphamu/PrayTime-Android/a6f942f9/privacypolicy.html");
        mBinding.scrollView.setWebViewClient(new WebViewClient() {
            boolean success = true;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                success = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mBinding.btnAgree.setEnabled(success);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mBinding.btnAgree.setEnabled(success);
            }
        });

        if (getIntent().hasExtra(EXTRA_DISPLAY_ONLY) && getIntent().getBooleanExtra(EXTRA_DISPLAY_ONLY, false)) {
            mBinding.btnAgree.setVisibility(View.GONE);
            displayOnly = true;
        }

        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CharSequence titleChars = mBinding.toolbar.getTitle();
        SpannableStringBuilder sBuilder = new SpannableStringBuilder(titleChars);
        mBinding.toolbar.setTitle(sBuilder);
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.btnAgree) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void finish() {
        if (displayOnly) {
            setResult(RESULT_OK);
            overridePendingTransition(R.anim.no_animation, R.anim.exit_from_bottom);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (displayOnly) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}