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
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.fragments.AbstractFragment;
import nsapp.com.combienjtedois.views.fragments.LoanObjectsFragment;
import nsapp.com.combienjtedois.views.fragments.NavigationDrawerFragment;
import nsapp.com.combienjtedois.views.fragments.PresentFragment;
import nsapp.com.combienjtedois.views.fragments.PersonListForMoneyFragment;

public class LaunchActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int X_ANIMATION = 150;

    private NavigationDrawerFragment navigationDrawerFragment;

    private CharSequence title;

    private boolean isCreatedView = false;
    private boolean listEmpty;

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

        Utils.dbManager = new DBManager(this);
        Utils.dbManager.open();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = PersonListForMoneyFragment.newInstance(position);
                break;
            case 1:
                fragment = LoanObjectsFragment.newInstance(position);
                break;
            case 2:
                fragment = PresentFragment.newInstance(position);
                break;
            case 3:
                fragment = PersonListForMoneyFragment.newInstance(position);
                break;
            default:
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
            case 3:
                title = getString(R.string.title_section4);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                updateActionBarTitle(getTitle().toString());
            }
            super.onBackPressed();
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
            if (getCurrentFragment() instanceof LoanObjectsFragment || listEmpty) {
                getMenuInflater().inflate(R.menu.add_menu, menu);
            } else {
                getMenuInflater().inflate(R.menu.global, menu);
            }
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
                ((AbstractFragment) getCurrentFragment()).addItem(null, null);
                break;
            case R.id.actionEdit:
                ((AbstractFragment) getCurrentFragment()).setEditingView(!((AbstractFragment) getCurrentFragment()).isEditingView());
                otherViewToggle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void otherViewToggle() {
        View view = getCurrentFragment().getView();
        if (view != null) {
            ListView listView = (ListView) getCurrentFragment().getView().findViewById(R.id.listView);
            if (listView != null && listView.getAdapter() != null) {
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {

                    if (listView.getChildAt(i) != null) {
                        final ImageView otherView = (ImageView) listView.getChildAt(i).findViewById(R.id.otherView);

                        if (otherView != null) {

                            TranslateAnimation imageViewTranslation;
                            otherView.setImageResource(R.drawable.dark_edit);

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
                                }, Utils.ANIMATION_DURATION);
                            }
                            imageViewTranslation.setDuration(Utils.ANIMATION_DURATION);
                            otherView.startAnimation(imageViewTranslation);
                        }
                    }
                }
            }
        }
    }

    Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    public void updateActionBarTitle(String title) {
        this.title = title;
        restoreActionBar();
    }

    public void setListEmpty(boolean listEmpty) {
        this.listEmpty = listEmpty;
    }
}
