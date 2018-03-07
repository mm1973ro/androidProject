package gallery.decode.com.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class PreviewActivity extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        SquareRelativeLayout mSquare = findViewById(R.id.preview_media);
        mSquare.setBackgroundColor(getIntent().getIntExtra("color", 0));
        TextView mText = findViewById(R.id.preview_text);
        mText.setText(getIntent().getStringExtra("text"));

        mSquare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.preview_media) {
            setResult(1);
        }

        finish();
    }
}
