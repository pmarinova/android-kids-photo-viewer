package pm.android.kidsphotoviewer;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import pm.android.kidsphotoviewer.databinding.ActivityPhotosBinding;

public class PhotosActivity extends AppCompatActivity {

    private ActivityPhotosBinding binding;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhotosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageView = binding.imageView;
        Glide.with(this).load(R.drawable.cat).into(binding.imageView);
    }
}