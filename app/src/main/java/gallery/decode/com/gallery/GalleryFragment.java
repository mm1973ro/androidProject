package gallery.decode.com.gallery;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import static gallery.decode.com.gallery.GalleryActivity.REQUEST_PERMISSIONS_CODE_WRITE_STORAGE;


/**
 * Created by mircea on 28/02/2018.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    private static final String PERMISSION_SHARED_PREFERENCES = "test";

    public interface ICallback {
        void preview(Media media);
    }

    private RecyclerView mRecyclerView;
    private int mType = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery,
                container, false);

        mRecyclerView = root.findViewById(R.id.recycler_view);

        mType = getArguments() != null ?
                getArguments().getInt("type", 0) : 0;

        loadMedia();

        return root;
    }

    @Override
    public void onClick(View view){
        if (view.getTag() instanceof Media)
        if (getActivity() instanceof ICallback
                && !getActivity().isDestroyed()
                && !getActivity().isFinishing())
            ((ICallback) getActivity()).preview((Media) view.getTag());
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mRecyclerView.setAdapter(new Adapter(mType));
//            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.galleryColumnsCount)));
//        }
//    }

    private void loadMedia () {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        FragmentActivity activity = getActivity();

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            SharedPreferences prefs = activity.getSharedPreferences(
                    PERMISSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            boolean wasRequested = prefs.getBoolean("requested_" + permission, false);

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS_CODE_WRITE_STORAGE);
            } else if (!wasRequested) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("requested_" + permission, true);
                editor.commit();

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS_CODE_WRITE_STORAGE);
            }
        } else {
            mRecyclerView.setAdapter(new Adapter(mType));
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.galleryColumnsCount)));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mLabel;
        private ImageView mThumb;
        public ViewHolder(View v) {
            super(v);
            mLabel = v.findViewById(R.id.grid_text);
            mThumb = itemView.findViewById(R.id.thumb);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Media> mMedia;
        private Picasso mThumbs;

        private Adapter(int type) {
            mMedia = Media.getMedia(getContext(), type);
            mThumbs = new Picasso.Builder(getContext()).addRequestHandler(new VideoRequestHandler()).build();
        }

        @Override
        public ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            LayoutInflater in = LayoutInflater.from(getContext());
            View v = in.inflate(R.layout.item_media,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            vh.mLabel.setText(U.format(mMedia.get(position).getDuration()));
            vh.itemView.setTag(mMedia.get(position));
            vh.itemView.setOnClickListener(GalleryFragment.this);
            mThumbs.load((mType == Media.TYPE_IMAGE ? "file://" : "video:") + mMedia.get(position).getUrl()).fit().centerInside().into(vh.mThumb);
        }

        @Override
        public int getItemCount() {
            return mMedia.size();
        }
    }

}
