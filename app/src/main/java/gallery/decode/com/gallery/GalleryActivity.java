package gallery.decode.com.gallery;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GalleryActivity extends AppCompatActivity implements GalleryFragment.ICallback {

    public static final int PREVIEW_REQUEST_TYPE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    public static final String[] TABS = {"PHOTO", "VIDEO"};
    static final String AUTHORITIES_NAME = "gallery.decode.com.gallery.fileprovider";
    public static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 3;

    private TabLayout mTabs;
    private ViewPager mPager;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ImageView mImage;
    private FloatingActionButton mCamera;
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        if (requestCode == PREVIEW_REQUEST_TYPE) {
//            mResultValue = resultCode;
            mPager.setCurrentItem(resultCode - 1);
            //refresh();
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
//        outState.putInt("result", mResultValue);
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

    @Override
    public void preview(Media media) {
        Intent intent = new Intent(this, PreviewActivity.class);
        //intent.putExtra("text", media.getName());
        startActivityForResult(intent, PREVIEW_REQUEST_TYPE);
    }
}
