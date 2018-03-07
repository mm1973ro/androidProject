package gallery.decode.com.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by mircea on 28/02/2018.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    public interface ICallback { void preview(Media media);}

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

        mRecyclerView.setAdapter(new Adapter(mType));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mLabel;
        public ViewHolder(View v) {
            super(v);
            mLabel = v.findViewById(R.id.grid_text);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Media[] mMedia;

        private Adapter(int type) {
            mMedia = Media.getMedia(type);
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
            vh.mLabel.setText(mMedia[position].getName());
            vh.itemView.setBackgroundColor(mMedia[position].getColor());


            vh.itemView.setTag(mMedia[position]);
            vh.itemView.setOnClickListener(GalleryFragment.this);
        }

        @Override
        public int getItemCount() {
            return mMedia.length;
        }
    }


}
