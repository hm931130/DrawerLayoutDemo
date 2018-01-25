package com.hm.drawerlayoutdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 沉浸式状态栏实现思路
 * 大致流程
 * 1>将内容布局延伸至系统状态栏(以下统称statusBar)下面，4.4.x以上不同系统方式不同
 * 2>设置内容跟视图的panddingTop为statusBar的高度
 * 3> 1. 5.x系统以上支持setStatusBarColor属性，可以直接设置状态栏的高度
 * 2. 4.4.x系统以上5.x以下支持设置状态栏为半透明属性，并在decorView添加占位view,设置占位view的颜色
 * <p>
 * <p>
 * 存在DrawerLayout时，可自由化定制
 * 1> 同样根据不同系统版本将布局全屏化
 * 2> 通过属性clipToPandding使侧滑栏内容延伸至statusBar下面
 * 3> 侧滑栏布局保持，修改DrawerLayout中内容视图
 * 1. 新建线性布局，设置垂直模式
 * 2. 添加占位view，并设置颜色
 * 3. 找到contentView，将其从DrawerLayout中删除
 * 4. 添加contentView到线性布局中
 * 5. 添加线性布局到DrawerLayout中
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fullScreen(this);
        fitsSystemWindows(this);
        addStatusViewWithColor(this, Color.parseColor("#00ff00"));
//        addStatusViewWithColor(Color.parseColor("#00ff00"));
    }


    /**
     * 将内容布局延伸到状态栏下面
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 将DrawerLayout的侧滑栏延伸至statusBar的下面
     *
     * @param activity
     */
    private void fitsSystemWindows(Activity activity) {
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 14) {
            parentView.setFitsSystemWindows(true);
            if (parentView instanceof DrawerLayout) {
                DrawerLayout drawer = (DrawerLayout) parentView;
                //将主页面顶部延伸至statusbar ;虽然默认为false，但drawerLayout 需显示设置
                drawer.setClipToPadding(false);
            }
        }
    }

    /**
     * 通过反射获取statusBar的高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 添加占位View到DrawerLayout的content中
     *
     * @param activity
     * @param color
     */
    private void addStatusViewWithColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isDrawerLayout()) {
                //要在内容布局增加状态栏，否则会盖住侧滑菜单栏上的statusbar
                ViewGroup rootView = findViewById(android.R.id.content);
                //Drawerlayout 则需要再第一个子视图即内容视图中添加pandding
                View parentView = rootView.getChildAt(0);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                View statusView = new View(this);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                statusView.setBackgroundColor(color);
                //添加占位view到线性布局中
                linearLayout.addView(statusView, layoutParams);
                //侧滑菜单
                DrawerLayout drawerLayout = (DrawerLayout) parentView;
                //内容视图
                View content = activity.findViewById(R.id.id_content);
                //将内容视图从DrawerLayout中移除
                drawerLayout.removeView(content);
                //添加内容视图到线性布局
                linearLayout.addView(content, content.getLayoutParams());
                //将带有占位视图和内容视图的线性布局添加到drawerlayout中
                drawerLayout.addView(linearLayout, 0);
            } else {
                //设置 paddingTop
                ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setPadding(0, getStatusBarHeight(), 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //直接设置状态栏颜色
                    activity.getWindow().setStatusBarColor(color);
                } else {
                    //增加占位状态栏
                    ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                    View statusBarView = new View(activity);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            getStatusBarHeight());
                    statusBarView.setBackgroundColor(color);
                    decorView.addView(statusBarView, lp);
                }
            }

        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //4.4以上
//            View rootView = findViewById(android.R.id.content);
//            rootView.setPadding(0, getStatusBarHeight(), 0, 0);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0以上
//                Window window = getWindow();
//                window.setStatusBarColor(color);
//            } else {
//                View suspendView = new View(this);
//                suspendView.setBackgroundColor(color);
//                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
//                Window window = getWindow();
//                ViewGroup decorView = (ViewGroup) window.getDecorView();
//                decorView.addView(suspendView, layoutParams);
////                ((ViewGroup) findViewById(android.R.id.content)).addView(suspendView, layoutParams);
//            }
//        }
    }

    protected boolean isDrawerLayout() {
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
