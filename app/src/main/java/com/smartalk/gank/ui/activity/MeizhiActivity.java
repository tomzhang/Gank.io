package com.smartalk.gank.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.smartalk.gank.PanConfig;
import com.smartalk.gank.R;
import com.smartalk.gank.ShareElement;
import com.smartalk.gank.model.entity.Meizi;
import com.smartalk.gank.presenter.MeizhiPresenter;
import com.smartalk.gank.ui.base.BaseActivity;
import com.smartalk.gank.utils.DateUtil;
import com.smartalk.gank.utils.FileUtil;
import com.smartalk.gank.utils.TipsUtil;
import com.smartalk.gank.view.IMeizhiView;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MeizhiActivity extends BaseActivity implements IMeizhiView {

    public static final String TRANSLATE_VIEW = "meizhi";

    Meizi meizi;
    PhotoViewAttacher attacher;
    MeizhiPresenter presenter;
    boolean isToolBarHiding = false;
    Bitmap girl;

    @Bind(R.id.iv_meizhi)
    ImageView ivMeizhi;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meizhi);
        ButterKnife.bind(this);
        presenter = new MeizhiPresenter(this, this);
        presenter.initView();
    }

    private void getIntentData() {
        meizi = (Meizi) getIntent().getSerializableExtra(PanConfig.MEIZI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareElement.shareDrawable = null;
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        appBar.setAlpha(0.6f);
        getIntentData();
        setTitle(DateUtil.toDateTimeStr(meizi.publishedAt));
        ViewCompat.setTransitionName(ivMeizhi, TRANSLATE_VIEW);
        ivMeizhi.setImageDrawable(ShareElement.shareDrawable);
        attacher = new PhotoViewAttacher(ivMeizhi);
        Glide.with(this).load(meizi.url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ivMeizhi.setImageBitmap(resource);
                attacher.update();
                girl = resource;
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                ivMeizhi.setImageDrawable(errorDrawable);
            }
        });
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                hideOrShowToolBar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_girl, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!FileUtil.isSDCardEnable() || girl == null) {
                    TipsUtil.showSnackTip(ivMeizhi, "保存失败!");
                } else {
                    presenter.saveMeizhiImage(girl, DateUtil.toDateString(meizi.publishedAt).toString());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideOrShowToolBar() {
        appBar.animate()
                .translationY(isToolBarHiding ? 0 : -appBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        isToolBarHiding = !isToolBarHiding;
    }


    @Override
    public void showSaveGirlResult(String result) {
        TipsUtil.showSnackTip(ivMeizhi, result);
    }
}
