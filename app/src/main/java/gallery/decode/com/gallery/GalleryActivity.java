package gallery.decode.com.gallery;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity implements GalleryFragment.IGallery {

    public static final int PREVIEW_REQUEST_TYPE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    public static final String[] TABS = {"PHOTO", "VIDEO"};
    static final String AUTHORITIES_NAME = "gallery.decode.com.gallery.fileprovider";
    public static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 3;
    private static final String PREFERENCES_VISITS = "pref-visits";

    private TabLayout mTabs;
    private ViewPager mPager;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ImageView mImage;
    private FloatingActionButton mCamera;
    private File photoFile = null;
    private ImageView sharedElementView;
    private Gson mGson;
    private HashMap<String, Integer> mVisits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_gallery);
        setTitle("Gallery");

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigation = findViewById(R.id.drawer_navigation);
        mNavigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem mi) {
                        mi.setChecked(true);
                        mDrawer.closeDrawers();
                        onOptionsItemSelected(mi);
                        return true;
                    }
                }
        );

        final ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // fragment
        mPager = findViewById(R.id.pager);
        loadFragment();

        // tabs
        mTabs = findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mPager);

        // camera
        mImage = findViewById(R.id.camera_image);
        mImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(1);
            }
        });

        mCamera = findViewById(R.id.myFAB);
        mCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Camera Selected", Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            }
        });

//        mGson = new Gson();
        mVisits = new HashMap<String, Integer>();

//        if (savedInstanceState != null) {
//            mVisits = ((HashMap<String, Integer>) savedInstanceState.getSerializable("visits"));
//        } else {
//            SharedPreferences prefs = getSharedPreferences(PREFERENCES_VISITS, MODE_PRIVATE);
//            try {
//                mVisits = mGson.fromJson(prefs.getString("visits", ""), new TypeToken<HashMap<String, Integer>>() {
//                }.getType());
//            } catch (Exception e) {
//            }
//        }

    }

    private void loadFragment() {
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
                return TABS[position];
            }

            @Override
            public int getCount() {
                return TABS.length;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadFragment();
        }
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
                mPager.setCurrentItem(0);
                Toast.makeText(getApplicationContext(), "Photo Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_video:
                mPager.setCurrentItem(1);
                Toast.makeText(getApplicationContext(), "Video Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_about:
                Toast.makeText(getApplicationContext(), "About Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_REQUEST_TYPE && resultCode == RESULT_OK) {
            Media media = data.getParcelableExtra("media");
            int v = mVisits.containsKey(media.getUrl()) ? mVisits.get(media.getUrl()) : 0;
            mVisits.put(media.getUrl(), v + 1);
            for (Fragment f : getSupportFragmentManager().getFragments())
                f.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(photoFile);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("visits", mVisits);
    }

    private void refresh() {
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        AUTHORITIES_NAME,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        Long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = tsLong.toString();
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void preview(View sharedElement, Media media) {
        Intent intent = new Intent(this, PreviewActivity.class);
        sharedElementView = sharedElement.findViewById(R.id.thumb);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, sharedElementView, "thumbnail");
        intent.putExtra("media", media);
        startActivityForResult(intent, PREVIEW_REQUEST_TYPE, options.toBundle());
    }

    @Override
    public View getRoot() {
        return mDrawer;
    }

    @Override
    public int getVisits(Media media) {
        return mVisits.containsKey(media.getUrl()) ? mVisits.get(media.getUrl()) : 0;
    }

}
