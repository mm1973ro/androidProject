package gallery.decode.com.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by mircea on 28/02/2018.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    interface ICallback { void preview(int type);}

    //private Button mPreviewButton;
    private int mType = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery,
                container, false);
        //mPreviewButton = root.findViewById(R.id.button_preview);
        //mPreviewButton.setOnClickListener(this);

        mType = getArguments() != null ?
                getArguments().getInt("type", 0) : 0;

        //mPreviewButton.setText("Preview" + mType);


        return root;
    }

    @Override
    public void onClick(View view){
        if (getActivity() instanceof ICallback
                && !getActivity().isDestroyed()
                && !getActivity().isFinishing())
            ((ICallback) getActivity()).preview(mType);
    }
}
