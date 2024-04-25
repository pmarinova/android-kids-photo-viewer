package pm.android.kidsphotoviewer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import java.util.concurrent.TimeUnit;

public class PhotosActivity extends AppCompatActivity {

    private ViewPager photosPager;

    private ProgressBar loadingIndicator;

    private SharedPreferences prefs;

    private Handler mainThreadHandler;

    private boolean slideshowRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        hideSystemBars();

        photosPager = findViewById(R.id.photos_view_pager);
        loadingIndicator = findViewById(R.id.loading_indicator);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mainThreadHandler = ((App)getApplication()).getMainThreadHandler();

        getPhotosProvider().loadPhotosList((photos) -> {
            loadingIndicator.setVisibility(View.GONE);
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
        if (isSlideshowEnabled()) {
            this.slideshowRunning = true;
            scheduleNextPhoto();
        }
    }

    private void stopSlideshow() {
        mainThreadHandler.removeCallbacks(this::nextPhoto);
        this.slideshowRunning = false;
    }

    private void nextPhoto() {
        if (slideshowRunning) {
            int nextItem = photosPager.getCurrentItem() + 1;
            photosPager.setCurrentItem(nextItem, false);
            scheduleNextPhoto();
        }
    }

    private void scheduleNextPhoto() {
        long delay = TimeUnit.SECONDS.toMillis(getSlideshowInterval());
        mainThreadHandler.postDelayed(this::nextPhoto, delay);
    }

    private PhotosProvider getPhotosProvider() {
        String id = prefs.getString(getString(R.string.pref_key_photos_provider), "");
        return id.equals(getString(R.string.photo_provider_server))
                ? new PhotoServerProvider(this)
                : new LocalPhotosProvider(this);
    }

    private boolean isSlideshowEnabled() {
        return prefs.getBoolean(getString(R.string.pref_key_slideshow_enable), false);
    }

    private int getSlideshowInterval() {
        return prefs.getInt(getString(R.string.pref_key_slideshow_interval), 5);
    }
}