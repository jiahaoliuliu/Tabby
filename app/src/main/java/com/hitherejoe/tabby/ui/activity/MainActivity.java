package com.hitherejoe.tabby.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import com.hitherejoe.tabby.R;
import com.hitherejoe.tabby.util.CustomTabActivityHelper;
import com.hitherejoe.tabby.util.ImageUtils;
import com.hitherejoe.tabby.util.SnackbarFactory;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private CheckBox mColorToolbarCheck;
    private CheckBox mShowTitleCheck;
    private CheckBox mCloseIconCheck;
    private CheckBox mActionBarIconCheck;
    private CheckBox mMenuItemsCheck;
    private CheckBox mCustomAnimationsCheck;
    private CoordinatorLayout mLayoutMainCoordinator;
    private Toolbar mToolbar;

    private static final String URL_ARGOS = "http://www.emirates.com";

    private Bitmap mActionButtonBitmap;
    private Bitmap mCloseButtonBitmap;
    private CompositeSubscription mSubscriptions;
    private CustomTabActivityHelper mCustomTabActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupCustomTabHelper();
        mSubscriptions = new CompositeSubscription();
        initViews();
        setupToolbar();
        decodeBitmaps();
    }

    private void initViews() {
        mColorToolbarCheck = findViewById(R.id.check_color_toolbar);
        mShowTitleCheck = findViewById(R.id.check_show_title);
        mCloseIconCheck = findViewById(R.id.check_close_icon);
        mActionBarIconCheck = findViewById(R.id.check_action_bar_icon);
        mMenuItemsCheck = findViewById(R.id.check_menu_items);
        mCustomAnimationsCheck = findViewById(R.id.check_custom_animations);
        mLayoutMainCoordinator = findViewById(R.id.layout_main);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.text_launch_site).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openCustomTab();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCustomTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCustomTabActivityHelper.unbindCustomTabsService(this);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    private void setupCustomTabHelper() {
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        mCustomTabActivityHelper.setConnectionCallback(mConnectionCallback);
        mCustomTabActivityHelper.mayLaunchUrl(Uri.parse(URL_ARGOS), null, null);
    }

    private void decodeBitmaps() {
        decodeBitmap(R.drawable.ic_share);
        decodeBitmap(R.drawable.ic_arrow_back);
    }

    private void openCustomTab() {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        if (mColorToolbarCheck.isChecked()) {
            int color = getResources().getColor(R.color.primary);
            intentBuilder.setToolbarColor(color);
        }

        if (mShowTitleCheck.isChecked()) intentBuilder.setShowTitle(true);

        if (mMenuItemsCheck.isChecked()) {
            String menuItemTitle = getString(R.string.menu_title_share);
            PendingIntent menuItemPendingIntent = createPendingShareIntent();
            intentBuilder.addMenuItem(menuItemTitle, menuItemPendingIntent);
            String menuItemEmailTitle = getString(R.string.menu_title_email);
            PendingIntent menuItemPendingIntentTwo = createPendingEmailIntent();
            intentBuilder.addMenuItem(menuItemEmailTitle, menuItemPendingIntentTwo);
        }

        if (mCloseButtonBitmap != null && mCloseIconCheck.isChecked()) {
            intentBuilder.setCloseButtonIcon(mCloseButtonBitmap);
        }

        if (mActionButtonBitmap != null && mActionBarIconCheck.isChecked()) {
            intentBuilder.setActionButton(mActionButtonBitmap, getString(R.string.menu_title_share), createPendingShareIntent());
        }

        if (mCustomAnimationsCheck.isChecked()) {
            intentBuilder.setStartAnimations(this,
                    R.anim.slide_in_right, R.anim.slide_out_left);
            intentBuilder.setExitAnimations(this,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        CustomTabActivityHelper.openCustomTab(
                this, intentBuilder.build(), Uri.parse(URL_ARGOS), new WebviewFallback());
    }

    private void decodeBitmap(final int resource) {
        mSubscriptions.add(ImageUtils.decodeBitmap(this, resource)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("There was a problem decoding the bitmap " + e);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        if (resource == R.drawable.ic_share) {
                            mActionButtonBitmap = bitmap;
                        } else if (resource == R.drawable.ic_arrow_back) {
                            mCloseButtonBitmap = bitmap;
                        }
                    }
                }));
    }

    private PendingIntent createPendingEmailIntent() {
        Intent emailIntent = new Intent(
                Intent.ACTION_SENDTO, Uri.fromParts("mailto", "example@example.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        return PendingIntent.getActivity(getApplicationContext(), 0, emailIntent, 0);
    }

    private PendingIntent createPendingShareIntent() {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, "This is sharing some text");
        return PendingIntent.getActivity(getApplicationContext(), 0, actionIntent, 0);
    }

    // You can use this callback to make UI changes
    private CustomTabActivityHelper.ConnectionCallback mConnectionCallback = new CustomTabActivityHelper.ConnectionCallback() {
        @Override
        public void onCustomTabsConnected() {
            SnackbarFactory.createSnackbar(MainActivity.this, mLayoutMainCoordinator, "Connected to service").show();
        }

        @Override
        public void onCustomTabsDisconnected() {
            SnackbarFactory.createSnackbar(MainActivity.this, mLayoutMainCoordinator, "Disconnected from service").show();
        }
    };

}
