package gallery.decode.com.gallery;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GalleryActivity extends AppCompatActivity implements GalleryFragment.ICallback {

    public static final int PREVIEW_REQUEST_TYPE = 1;
    public static final String TEXT_PHOTO = "PHOTO";
    public static final String TEXT_VIDEO = "VIDEO";

    private android.support.design.widget.TabLayout mTabs;
    private android.support.v4.view.ViewPager mPager;
    private android.support.v7.widget.Toolbar mToolbar;
    private DrawerLayout mDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setTitle("Gallery");

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mTabs = findViewById(R.id.tabs);
        mPager = findViewById(R.id.pager);

        mPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                GalleryFragment f = new GalleryFragment();

                Bundle arguments = new Bundle();
                arguments.putInt("type", position);
                f.setArguments(arguments);

                return f;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                if (position == 0) {
                    return TEXT_PHOTO;
                } else {
                    return TEXT_VIDEO;
                }

            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        mTabs.setupWithViewPager(mPager);

        mDrawer = findViewById(R.id.drawer_layout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_photo:
                Toast.makeText(getApplicationContext(),"Photo Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_video:
                Toast.makeText(getApplicationContext(),"Video Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(),"Settings Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_about:
                Toast.makeText(getApplicationContext(),"About Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_REQUEST_TYPE) {
//            mResultValue = resultCode;
            mPager.setCurrentItem(resultCode - 1);
            //refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("result", mResultValue);
    }

    private void refresh() {
    }

    @Override
    public void preview(int type) {
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivityForResult(intent, PREVIEW_REQUEST_TYPE);
    }
}
