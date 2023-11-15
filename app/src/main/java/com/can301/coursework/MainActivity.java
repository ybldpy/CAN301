package com.can301.coursework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.can301.coursework.activity.PlanActivity;
import com.can301.coursework.widget.TickProgressBar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.can301.coursework.activity.InfoActivity;
import com.can301.coursework.activity.ScheduleActivity;
import com.can301.coursework.activity.SettingActivity;
import com.can301.coursework.util.TimeFormatUtil;
import com.can301.coursework.widget.RippleWrapper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TickApplication mApplication;
    private DrawerLayout mDrawerLayout;
    private MenuItem mMenuItemIDLE;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnResume;
    private Button mBtnStop;
    private Button mBtnSkip;
    private TextView mTextCountDown;
    private TextView mTextTimeTile;
    private TickProgressBar mProgressBar;
    private RippleWrapper mRippleWrapper;
    private long mLastClickTime = 0;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

                if (mMenuItemIDLE != null && newState == DrawerLayout.STATE_IDLE) {
                    runNavigationItemSelected(mMenuItemIDLE);
                    mMenuItemIDLE = null;
                }
            }
        };

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mApplication = (TickApplication)getApplication();

        mBtnStart = (Button)findViewById(R.id.btn_start);
        mBtnPause = (Button)findViewById(R.id.btn_pause);
        mBtnResume = (Button)findViewById(R.id.btn_resume);
        mBtnStop = (Button)findViewById(R.id.btn_stop);
        mBtnSkip = (Button)findViewById(R.id.btn_skip);
        mTextCountDown = (TextView)findViewById(R.id.text_count_down);
        mTextTimeTile = (TextView)findViewById(R.id.text_time_title);
        mProgressBar = (TickProgressBar)findViewById(R.id.tick_progress_bar);
        mRippleWrapper = (RippleWrapper)findViewById(R.id.ripple_wrapper);

        initActions();
    }

    private void initActions() {
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = TickService.newIntent(getApplicationContext());
//                i.setAction(TickService.ACTION_START);
//                startService(i);
//
//                mApplication.start();
//                updateButtons();
//                updateTitle();
//                updateRipple();
                Intent i = new Intent();
                i.setClass(MainActivity.this, PlanActivity.class);
                startActivity(i);
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_PAUSE);
                i.putExtra("time_left", (String) mTextCountDown.getText());
                startService(i);

                mApplication.pause();
                updateButtons();
                updateRipple();
            }
        });

        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_RESUME);
                startService(i);

                mApplication.resume();
                updateButtons();
                updateRipple();
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_STOP);
                startService(i);

                mApplication.stop();
                reload();
            }
        });

        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_STOP);
                startService(i);

                mApplication.skip();
                reload();
            }
        });

        mRippleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - mLastClickTime < 500) {
                    boolean isSoundOn = getSharedPreferences()
                            .getBoolean("pref_key_tick_sound", true);

                    // 修改 SharedPreferences
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).edit();

                    if (isSoundOn) {
                        editor.putBoolean("pref_key_tick_sound", false);

                        Intent i = TickService.newIntent(getApplicationContext());
                        i.setAction(TickService.ACTION_TICK_SOUND_OFF);
                        startService(i);

                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_off),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        editor.putBoolean("pref_key_tick_sound", true);

                        Intent i = TickService.newIntent(getApplicationContext());
                        i.setAction(TickService.ACTION_TICK_SOUND_ON);
                        startService(i);

                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_on),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    try {
                        editor.apply();
                    } catch (AbstractMethodError unused) {
                        editor.commit();
                    }

                    updateRipple();
                }

                mLastClickTime = clickTime;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mMenuItemIDLE = item;
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    /**
     * DrawerLayout 关闭卡顿的综合解决方法
     *
     * @link https://stackoverflow.com/questions/18343018/optimizing-drawer-and-activity-launching-speed
     */
    private void runNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

//        switch (id) {
//            case R.id.nav_setting:
//                startActivity(SettingActivity.newIntent(this));
//                break;
//            case R.id.nav_schedule:
//                startActivity(ScheduleActivity.newIntent(this));
//                break;
//            case R.id.nav_info:
//                startActivity(InfoActivity.newIntent(this));
//                break;
//            case R.id.nav_exit:
//                exitApp();
//                break;
//        }

        if(id == R.id.nav_setting){
            startActivity(SettingActivity.newIntent(this));
        }
        else if(id == R.id.nav_schedule){
            startActivity(ScheduleActivity.newIntent(this));
        }
        else if(id == R.id.nav_info){
            startActivity(InfoActivity.newIntent(this));
        }
        else if(id == R.id.nav_exit){
            exitApp();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TickService.ACTION_COUNTDOWN_TIMER);
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void reload() {
        mApplication.reload();

        mProgressBar.setMaxProgress(mApplication.getMillisInTotal() / 1000);
        mProgressBar.setProgress(mApplication.getMillisUntilFinished() / 1000);

        updateText(mApplication.getMillisUntilFinished());
        updateTitle();
        updateButtons();
        updateScene();
        updateRipple();
        updateAmount();

        if (getSharedPreferences().getBoolean("pref_key_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void updateText(long millisUntilFinished) {
        mTextCountDown.setText(TimeFormatUtil.formatTime(millisUntilFinished));
    }

    private void updateTitle() {
        if (mApplication.getState() == TickApplication.STATE_FINISH) {
            String title;

            if (mApplication.getScene() == TickApplication.SCENE_WORK) {
                title = getResources().getString(R.string.scene_title_work);
            } else {
                title = getResources().getString(R.string.scene_title_break);
            }

            mTextTimeTile.setText(title);
            mTextTimeTile.setVisibility(View.VISIBLE);
            mTextCountDown.setVisibility(View.GONE);
        } else {
            mTextTimeTile.setVisibility(View.GONE);
            mTextCountDown.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtons() {
        int state = mApplication.getState();
        int scene = mApplication.getScene();
        boolean isPomodoroMode = getSharedPreferences()
                .getBoolean("pref_key_pomodoro_mode", true);

        // 在番茄模式下不能暂停定时器
        mBtnStart.setVisibility(
                state == TickApplication.STATE_WAIT || state == TickApplication.STATE_FINISH ?
                View.VISIBLE : View.GONE);

        if (isPomodoroMode) {
            mBtnPause.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.GONE);
        } else {
            mBtnPause.setVisibility(state == TickApplication.STATE_RUNNING ?
                    View.VISIBLE : View.GONE);
            mBtnResume.setVisibility(state == TickApplication.STATE_PAUSE ?
                    View.VISIBLE : View.GONE);
        }

        if (scene == TickApplication.SCENE_WORK) {
            mBtnSkip.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnStop.setVisibility(!(state == TickApplication.STATE_WAIT ||
                        state == TickApplication.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnStop.setVisibility(state == TickApplication.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        } else {
            mBtnStop.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnSkip.setVisibility(!(state == TickApplication.STATE_WAIT ||
                        state == TickApplication.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnSkip.setVisibility(state == TickApplication.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        }
    }

    public void updateScene() {
        int scene = mApplication.getScene();

        int workLength = getSharedPreferences()
                .getInt("pref_key_work_length", TickApplication.DEFAULT_WORK_LENGTH);
        int shortBreak = getSharedPreferences()
                .getInt("pref_key_short_break", TickApplication.DEFAULT_SHORT_BREAK);
        int longBreak = getSharedPreferences()
                .getInt("pref_key_long_break", TickApplication.DEFAULT_LONG_BREAK);

        ((TextView)findViewById(R.id.stage_work_value))
                .setText(getResources().getString(R.string.stage_time_unit, workLength));
        ((TextView)findViewById(R.id.stage_short_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, shortBreak));
        ((TextView)findViewById(R.id.stage_long_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, longBreak));

        findViewById(R.id.stage_work).setAlpha(
                scene == TickApplication.SCENE_WORK ? 0.9f : 0.5f);
        findViewById(R.id.stage_short_break).setAlpha(
                scene == TickApplication.SCENE_SHORT_BREAK ? 0.9f : 0.5f);
        findViewById(R.id.stage_long_break).setAlpha(
                scene == TickApplication.SCENE_LONG_BREAK ? 0.9f : 0.5f);
    }

    private void updateRipple() {
        boolean isPlayOn = getSharedPreferences().getBoolean("pref_key_tick_sound", true);

        if (isPlayOn) {
            if (mApplication.getState() == TickApplication.STATE_RUNNING) {
                mRippleWrapper.start();
                return;
            }
        }

        mRippleWrapper.stop();
    }

    private void updateAmount() {
        long amount = getSharedPreferences().getLong("pref_key_amount_durations", 0);
        TextView textView = (TextView)findViewById(R.id.amount_durations);
        textView.setText(getResources().getString(R.string.amount_durations, amount));
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TickService.ACTION_COUNTDOWN_TIMER)) {
                String requestAction = intent.getStringExtra(TickService.REQUEST_ACTION);

                switch (requestAction) {
                    case TickService.ACTION_TICK:
                        long millisUntilFinished = intent.getLongExtra(
                                TickService.MILLIS_UNTIL_FINISHED, 0);
                        mProgressBar.setProgress(millisUntilFinished / 1000);
                        updateText(millisUntilFinished);
                        break;
                    case TickService.ACTION_FINISH:
                    case TickService.ACTION_AUTO_START:
                        reload();
                        break;
                }
            }
        }
    };

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void exitApp() {
        stopService(TickService.newIntent(getApplicationContext()));
        mApplication.exit();
        finish();
    }
}
