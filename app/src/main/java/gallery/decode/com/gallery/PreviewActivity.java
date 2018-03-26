package gallery.decode.com.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class PreviewActivity extends AppCompatActivity {

    private Media mMedia;
    private ImageView mThumb;
    private Picasso mThumbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_preview);
        supportPostponeEnterTransition();

        mThumb = findViewById(R.id.thumb);

        mThumbs = new Picasso.Builder(this).addRequestHandler(new VideoRequestHandler()).build();

        mMedia = getIntent().getParcelableExtra("media");

        mThumbs.load((mMedia.getType() == Media.TYPE_IMAGE ? "file://" : "video:") +
                mMedia.getUrl()).fit().centerInside().into(mThumb, new Callback() {
            @Override
            public void onSuccess() {
                scheduleStartPostponedTransition(mThumb);
            }

            @Override
            public void onError() {

            }
        });

    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra("media", mMedia);
        setResult(RESULT_OK, result);
        super.finish();
    }

}
