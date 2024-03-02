package pm.android.kidsphotoviewer;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;

import pm.android.kidsphotoviewer.databinding.ActivityPhotosBinding;

public class PhotosActivity extends AppCompatActivity {

    private ActivityPhotosBinding binding;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        windowInsetsController.setSystemBarsBehavior(
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        binding = ActivityPhotosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageView = binding.imageView;
        Glide.with(this).load(R.drawable.cat).into(binding.imageView);
    }
}