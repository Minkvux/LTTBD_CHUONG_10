package com.example.chuong10;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.core.view.GravityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chuong10.databinding.ActivityMainBinding;

public class git init
        git add .
git commit -m "Initial commit"
git remote add origin https://github.com/Minkvux/LTTBD_CHUONG_10.git
git branch -M main
git push -u origin main
MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Danh sách fragment chính
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_vande,
                R.id.nav_kythuat,
                R.id.nav_ungdung,
                R.id.nav_dulieu,
                R.id.nav_giaotiep
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Ẩn các menu con khi khởi động
        Menu menu = navigationView.getMenu();
        hideSubItems(menu);

        // Xử lý click menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_kythuat) {
                toggleSubItems(menu); // Hiện/ẩn các mục con
                return true;
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) drawer.closeDrawer(GravityCompat.START);
            return handled;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // ===== Menu con ẩn/hiện =====
    private boolean isSubItem(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.nav_ungdung
                || id == R.id.nav_dulieu
                || id == R.id.nav_giaotiep;
    }

    private void hideSubItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (isSubItem(item)) {
                item.setVisible(false);
            }
        }
    }

    private void toggleSubItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (isSubItem(item)) {
                item.setVisible(!item.isVisible());
            }
        }
    }
}