package pm.android.kidsphotoviewer;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;

import pm.android.kidsphotoviewer.databinding.ActivityPhotosBinding;

public class PhotosActivity extends AppCompatActivity {

    private static final int MIN_SWIPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;

    private static int[] IMAGES = {
            R.drawable.bear,
            R.drawable.bird,
            R.drawable.cat,
            R.drawable.flamingo
    };

    private ActivityPhotosBinding binding;

    private ImageView imageView;

    private int currentImageIndex;

    private GestureDetectorCompat gestureDetector;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
            {
                onSwipeLeft();
                return false;
            }
            else if (e2.getX() - e1.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
            {
                onSwipeRight();
                return false;
            }
            return false;
        }
    };

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
        loadImage(0);

        gestureDetector = new GestureDetectorCompat(this, gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onSwipeLeft() {
        nextImage();
    }

    private void onSwipeRight() {
        prevImage();
    }

    private void nextImage() {
        currentImageIndex++;
        if (currentImageIndex == IMAGES.length) {
            currentImageIndex = 0;
        }
        loadImage(currentImageIndex);
    }

    private void prevImage() {
        currentImageIndex--;
        if (currentImageIndex == -1) {
            currentImageIndex = IMAGES.length - 1;
        }
        loadImage(currentImageIndex);
    }

    private void loadImage(int index) {
        Glide.with(this).load(IMAGES[index]).into(binding.imageView);
    }
}