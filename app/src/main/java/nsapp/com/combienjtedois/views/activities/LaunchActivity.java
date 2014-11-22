package nsapp.com.combienjtedois.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Tools;
import nsapp.com.combienjtedois.views.fragments.AbstractFragment;
import nsapp.com.combienjtedois.views.fragments.DetailFragment;
import nsapp.com.combienjtedois.views.fragments.MoneyFragment;
import nsapp.com.combienjtedois.views.fragments.NavigationDrawerFragment;
import nsapp.com.combienjtedois.views.fragments.ObjectFragment;
import nsapp.com.combienjtedois.views.fragments.PresentFragment;

public class LaunchActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int X_ANIMATION = 150;
    public static final int ANIMATION_DURATION = 400;

    private NavigationDrawerFragment navigationDrawerFragment;

    private CharSequence title;

    private boolean isCreatedView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawerFragment);
        navigationDrawerFragment.setUp(
                R.id.navigationDrawerFragment,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        title = getTitle();
        navigationDrawerFragment.closeDrawer();

        Tools.dbManager = new DBManager(this);
        Tools.dbManager.open();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = MoneyFragment.newInstance(position);
                break;
            case 1:
                fragment = ObjectFragment.newInstance(position);
                break;
            case 2:
                fragment = PresentFragment.newInstance(position);
                break;
            default:
                fragment = MoneyFragment.newInstance(position);
                break;
        }

        if (isCreatedView) {
            fragmentTransaction.setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.unzoom_in, R.anim.unzoom_out).replace(R.id.container, fragment).addToBackStack(null);
        } else {
            fragmentTransaction.add(R.id.container, fragment);
            isCreatedView = true;
        }
        fragmentTransaction.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                title = getString(R.string.title_section1);
                break;
            case 1:
                title = getString(R.string.title_section2);
                break;
            case 2:
                title = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                updateActionBarTitle(getTitle().toString());
            }
        }
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionAdd:
                addItem();
                break;
            case R.id.actionDelete:
                if (!((AbstractFragment) getCurrentFragment()).isEditingView()) {
                    ((AbstractFragment) getCurrentFragment()).setDeletingView(!((AbstractFragment) getCurrentFragment()).isDeletingView());
                    otherViewToggle(R.drawable.delete);
                }
                break;
            case R.id.actionEdit:
                if (!((AbstractFragment) getCurrentFragment()).isDeletingView()) {
                    ((AbstractFragment) getCurrentFragment()).setEditingView(!((AbstractFragment) getCurrentFragment()).isEditingView());
                    otherViewToggle(R.drawable.edit);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void otherViewToggle(int resViewID) {
        View view = getCurrentFragment().getView();
        if (view != null) {
            ListView listView = (ListView) getCurrentFragment().getView().findViewById(R.id.listView);
            if (listView != null && listView.getAdapter() != null) {
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {

                    if (listView.getChildAt(i) != null) {
                        final ImageView otherView = (ImageView) listView.getChildAt(i).findViewById(R.id.otherView);

                        if (otherView != null) {

                            TranslateAnimation imageViewTranslation;
                            otherView.setImageResource(resViewID);

                            if (otherView.getVisibility() == View.GONE) {
                                imageViewTranslation = new TranslateAnimation(otherView.getLeft() - X_ANIMATION, otherView.getLeft(), otherView.getTop(), otherView.getTop());
                                otherView.setVisibility(View.VISIBLE);

                            } else {
                                imageViewTranslation = new TranslateAnimation(otherView.getLeft(), otherView.getLeft() - X_ANIMATION, otherView.getTop(), otherView.getTop());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        otherView.setVisibility(View.GONE);
                                    }
                                }, ANIMATION_DURATION);
                            }
                            imageViewTranslation.setDuration(ANIMATION_DURATION);
                            otherView.startAnimation(imageViewTranslation);
                        }
                    }
                }
            }
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    private void addItem() {
        if (getCurrentFragment() instanceof DetailFragment) {
            ((DetailFragment) getCurrentFragment()).addDebt();
        } else {
            ((AbstractFragment) getCurrentFragment()).addPerson();
        }
    }

    public void updateActionBarTitle(String title) {
        this.title = title;
        restoreActionBar();
    }
}
