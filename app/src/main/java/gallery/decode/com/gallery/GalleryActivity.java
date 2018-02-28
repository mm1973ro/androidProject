package gallery.decode.com.gallery;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GalleryActivity extends AppCompatActivity implements GalleryFragment.ICallback {

    public static final int PREVIEW_REQUEST_TYPE = 1;

    private android.support.design.widget.TabLayout mTabs;
    private android.support.v4.view.ViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setTitle("Gallery");



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
                return "Page " + position;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        mTabs.setupWithViewPager(mPager);

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
