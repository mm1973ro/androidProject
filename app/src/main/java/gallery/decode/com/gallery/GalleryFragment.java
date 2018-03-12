package gallery.decode.com.gallery;

//import android.content.CursorLoader;
import android.database.Cursor;
//import android.net.Uri;
import android.os.Bundle;
//import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by mircea on 28/02/2018.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    public interface ICallback { void preview(Media media);}

    private RecyclerView mRecyclerView;
    private int mType = 0;
    //private Cursor mMediaCursor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery,
                container, false);

        mRecyclerView = root.findViewById(R.id.recycler_view);

        mType = getArguments() != null ?
                getArguments().getInt("type", 0) : 0;

        mRecyclerView.setAdapter(new Adapter(mType));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.galleryColumnsCount)));

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

//    private void loadMediaCursor() {
//        if (mMediaCursor != null && !mMediaCursor.isClosed())
//            mMediaCursor.close();
//
//            // Get relevant columns for use later.
//            String[] projection = {
//                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_ADDED,
//                MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE,
//                MediaStore.Video.Media.DURATION};
//
//            // Return only video and image metadata.
//            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + (mType == Media.TYPE_IMAGE ? MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE : MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
//            Uri queryUri = MediaStore.Files.getContentUri("external");
//
//            CursorLoader cursorLoader = new CursorLoader(getContext(), queryUri, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
//            mMediaCursor = cursorLoader.loadInBackground();
//        }

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
