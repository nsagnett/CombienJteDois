package nsapp.com.combienjtedois.views.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Utils;

public class FullScreenImageActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_image);
        String path = getIntent().getStringExtra(Utils.PATH_KEY);
        ImageView imageView = (ImageView) findViewById(R.id.fullScreenImageView);
        Bitmap image = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(image);
        imageView.setOnClickListener(this);
        getSupportActionBar().hide();
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
