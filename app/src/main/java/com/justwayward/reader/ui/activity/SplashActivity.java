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
package com.justwayward.reader.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.justwayward.reader.R;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@TargetApi(Build.VERSION_CODES.N)
public class SplashActivity extends RxAppCompatActivity
{

    @Bind(R.id.tvSkip)
    TextView tvSkip;

    private boolean flag = false;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mSubscription = Observable
                .timer(2, TimeUnit.SECONDS)
                .doOnSubscribe(() -> System.out.print("----打印----"))
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .map(l -> null)
                //.subscribeOn(Schedulers.io()) 暂时没有用
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> goHome());
        tvSkip.setOnClickListener(v -> goHome());
    }

    /**
     * 跳转到主页
     */
    private synchronized void goHome()
    {
        if (!flag)
        {
            flag = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        flag = true;
        ButterKnife.unbind(this);
        mSubscription.unsubscribe();
    }
}
