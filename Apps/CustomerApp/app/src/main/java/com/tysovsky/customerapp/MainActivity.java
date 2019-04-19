package com.tysovsky.customerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tysovsky.customerapp.Fragments.CartFragment;
import com.tysovsky.customerapp.Fragments.LoginFragment;
import com.tysovsky.customerapp.Fragments.MenuFragment;
import com.tysovsky.customerapp.Fragments.MenuItemFragment;
import com.tysovsky.customerapp.Interfaces.NetworkResponseListener;
import com.tysovsky.customerapp.Models.Cart;
import com.tysovsky.customerapp.Models.OrderItem;
import com.tysovsky.customerapp.Models.User;
import com.tysovsky.customerapp.Network.NetworkManager;
import com.tysovsky.customerapp.Network.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NetworkResponseListener {

    NavigationView navigationView;

    MenuFragment menuFragment = new MenuFragment(this);
    MenuItemFragment menuItemFragment = new MenuItemFragment();
    CartFragment cartFragment = new CartFragment();
    LoginFragment loginFragment = new LoginFragment();
    FragmentManager fragmentManager;


    //Hardcoding this for now
    User user = User.getCurrentUser();//new User("5c967e32d2e79f4afc43fdef");
    Cart cart = new Cart(user);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();

        if(savedInstanceState == null){
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.main_container, menuFragment, MenuFragment.TAG);
            transaction.commit();
        }
        else{
            if(fragmentManager == null){
                fragmentManager = getSupportFragmentManager();
            }
            if(fragmentManager.findFragmentByTag(MenuFragment.TAG) != null){
                menuFragment = (MenuFragment)fragmentManager.findFragmentByTag(MenuFragment.TAG);
            }
            if(fragmentManager.findFragmentByTag(MenuItemFragment.TAG) != null){
                menuItemFragment = (MenuItemFragment) fragmentManager.findFragmentByTag(MenuItemFragment.TAG);
            }
            if(fragmentManager.findFragmentByTag(CartFragment.TAG) != null){
                cartFragment = (CartFragment) fragmentManager.findFragmentByTag(CartFragment.TAG);
            }
            if(fragmentManager.findFragmentByTag(LoginFragment.TAG) != null){
                loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LoginFragment.TAG);
            }
        }

        cartFragment.setCart(cart);

        NetworkManager.getInstance().addListener(this);
    }

    public void setUpViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageButton btnAlert = toolbar.findViewById(R.id.btn_alert);
        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkManager.getInstance().requestAssistance(user);
            }
        });

        ImageButton btnCart = toolbar.findViewById(R.id.btn_cart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, cartFragment, CartFragment.TAG);
                transaction.commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavigationDrawer();
    }

    public void updateNavigationDrawer(){
        //If user currently logged in, hide the login button, otherwise hide the logout button
        if(user == null){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
        }
        else{
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profile).setTitle(user.FirstName + " " + user.LastName);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fragmentManager.findFragmentByTag(MenuFragment.TAG) == null){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, menuFragment, MenuFragment.TAG);
            transaction.commit();
        }
        else {
            super.onBackPressed();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        }
        else if(id == R.id.nav_login){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, loginFragment, LoginFragment.TAG);
            transaction.commit();
        }

        else if(id == R.id.nav_logout){
            NetworkManager.getInstance().logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadMenuItemFragment(com.tysovsky.customerapp.Models.MenuItem item){
        menuItemFragment.setCurrentMenuItem(item);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, menuItemFragment, MenuItemFragment.TAG);
        transaction.commit();
    }

    @Override
    public void OnNetworkResponseReceived(RequestType REQUEST_TYPE, Object result) {
        switch (REQUEST_TYPE){
            case LOGIN:
                JSONObject response = (JSONObject)result;
                try {
                    if(response.getBoolean("success")){
                        user = User.fromJson(response.getString("user"));
                        user.SaveUser();
                        runOnUiThread(() -> updateNavigationDrawer());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case LOGOUT:
                user = null;
                runOnUiThread(() -> {
                    updateNavigationDrawer();
                    onBackPressed();
                });
                break;
        }
    }

    public void addOrderToCart(OrderItem orderItem){
        cart.addOrderItem(orderItem);
        cartFragment.notifyCartUpdate();
    }

    public void clearCart(){
        cart.clean();
        cartFragment.notifyCartUpdate();
    }
}
