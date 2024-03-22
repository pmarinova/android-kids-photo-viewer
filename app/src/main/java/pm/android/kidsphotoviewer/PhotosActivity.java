package pm.android.kidsphotoviewer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.concurrent.TimeUnit;

public class PhotosActivity extends AppCompatActivity {

    private ViewPager photosPager;

    private Handler mainThreadHandler;

    private boolean slideshowRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        hideSystemBars();

        photosPager = findViewById(R.id.photos_view_pager);
        mainThreadHandler = new Handler(Looper.getMainLooper());

        new PhotosProvider(this).loadPhotosList((photos) -> {
            photosPager.setAdapter(new PhotosPagerAdapter(this, photos));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSlideshow();
    }

    @Override
    protected void onPause() {
        stopSlideshow();
        super.onPause();
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void startSlideshow() {
        this.slideshowRunning = true;
        mainThreadHandler.postDelayed(this::switchPhoto, TimeUnit.SECONDS.toMillis(5));
    }

    private void stopSlideshow() {
        mainThreadHandler.removeCallbacks(this::switchPhoto);
        this.slideshowRunning = false;
    }

    private void switchPhoto() {
        if (!slideshowRunning) {
            return;
        }
        photosPager.setCurrentItem(photosPager.getCurrentItem() + 1, false);
        mainThreadHandler.postDelayed(this::switchPhoto, TimeUnit.SECONDS.toMillis(5));
    }
}