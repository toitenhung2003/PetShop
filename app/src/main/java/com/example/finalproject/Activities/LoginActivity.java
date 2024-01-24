package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.finalproject.Adapters.LoginAdapter;
import com.example.finalproject.Fragments.LoginFragment;
import com.example.finalproject.Fragments.OnChangeTabListener;
import com.example.finalproject.Fragments.SignUpFragment;
import com.example.finalproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity implements OnChangeTabListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton fabGoogle, fabFb, fabTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initData();
    }

    private void initData() {
        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fabGoogle = findViewById(R.id.fab_google);
        fabFb = findViewById(R.id.fab_fb);
        fabTwitter = findViewById(R.id.fab_twitter);

        adapter.addTab(new LoginFragment(), "Login");
        adapter.addTab(new SignUpFragment(this), "Sign Up");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        animationView();
    }

    private void animationView() {
        fabFb.setTranslationY(300);
        fabTwitter.setTranslationY(300);
        fabGoogle.setTranslationY(300);
        tabLayout.setTranslationX(300);

        fabFb.setAlpha(0);
        fabTwitter.setAlpha(0);
        fabGoogle.setAlpha(0);
        tabLayout.setAlpha(0);

        fabFb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        fabTwitter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        fabGoogle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        tabLayout.animate().translationX(0).alpha(1).setDuration(1000).start();
    }

    public void registerBySocialNetwork(View view) {
    }

    @Override
    public void onChangeTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        viewPager.setCurrentItem(index);
        tab.select();
    }
}