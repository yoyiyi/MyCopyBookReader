/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justwayward.reader.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.justwayward.reader.R;
import com.justwayward.reader.ReaderApplication;
import com.justwayward.reader.component.AppComponent;
import com.justwayward.reader.utils.SharedPreferencesUtil;
import com.justwayward.reader.utils.StatusBarCompat;
import com.justwayward.reader.view.loadding.CustomDialog;

import butterknife.ButterKnife;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity
{

    public Toolbar mCommonToolbar;//公用的Toolbar

    protected Context mContext;
    protected int statusBarColor = 0;//状态栏颜色
    protected View statusBarView = null;//状态栏View
    private boolean mNowMode;//现在的模式 白天和夜间模式切换
    private CustomDialog dialog;//进度条

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置LayoutId
        setContentView(getLayoutId());
        if (statusBarColor == 0)
        {
            //状态栏颜色设置
            statusBarView = StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else if (statusBarColor != -1)
        {
            statusBarView = StatusBarCompat.compat(this, statusBarColor);
        }
        //Api 19 和 Api 20 设置不同 状态栏
        transparent19and20();
        mContext = this;
        ButterKnife.bind(this);
        //获取App组件
        setupActivityComponent(ReaderApplication.getsInstance().getAppComponent());
        //获取公用toolbar
        mCommonToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        if (mCommonToolbar != null)
        {
            //初始化Toolbar
            initToolBar();
            //让组件支持Toolbar
            setSupportActionBar(mCommonToolbar);
        }
        //初始化数据
        initDatas();
        //对各种控件进行设置、适配、填充数据
        configViews();
        //sp 是否为夜间模式
        mNowMode = SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT);
    }

    /**
     * 19和20状态栏颜色
     */
    protected void transparent19and20()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT//4.4
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {//5.0
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false) != mNowMode)
        {
            if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false))
            {
                //设置为夜间模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else
            {
                //设置白天模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            //重新启动
            recreate();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //7.0要销毁 ButterKnife
        ButterKnife.unbind(this);
        //销毁Dialog
        dismissDialog();
    }

    public abstract
    @LayoutRes
    int getLayoutId();

    protected abstract void setupActivityComponent(AppComponent appComponent);

    public abstract void initToolBar();

    public abstract void initDatas();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews();

    protected void gone(final View... views)
    {
        if (views != null && views.length > 0)
        {
            for (View view : views)
            {
                if (view != null)
                {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views)
    {
        if (views != null && views.length > 0)
        {
            for (View view : views)
            {
                if (view != null)
                {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    protected boolean isVisible(View view)
    {
        return view.getVisibility() == View.VISIBLE;
    }



    /**
     * 自定义Dialog
     * @return Dialog
     */
    public CustomDialog getDialog()
    {
        if (dialog == null)
        {
            dialog = CustomDialog.instance(this);
            dialog.setCancelable(true);
        }
        return dialog;
    }

    /**
     * 隐藏Dialog
     */
    public void hideDialog()
    {
        if (dialog != null)
            dialog.hide();
    }

    public void showDialog()
    {
        getDialog().show();
    }

    /**
     * 销毁Dialog
     */
    public void dismissDialog()
    {
        if (dialog != null)
        {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 隐藏状态栏
     */
    protected void hideStatusBar()
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if (statusBarView != null)
        {
            statusBarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 显示状态栏
     */
    protected void showStatusBar()
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if (statusBarView != null)
        {
            statusBarView.setBackgroundColor(statusBarColor);
        }
    }

}
