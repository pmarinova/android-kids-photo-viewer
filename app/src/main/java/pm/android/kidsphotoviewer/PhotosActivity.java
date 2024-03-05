package pm.android.kidsphotoviewer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PhotosActivity extends AppCompatActivity {

    private static class ViewPagerAdapter extends PagerAdapter {

        private final Context context;
        private final List<String> photos;
        private final LayoutInflater layoutInflater;

        public ViewPagerAdapter(Context context, List<String> photos) {
            this.context = context;
            this.photos = photos;
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout)object);
        }

        @Override
        @NonNull
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.photo_item, container, false);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.photo_image_view);
            Glide.with(context).load(PHOTO_SERVER_URL + photos.get(position)).into(imageView);
            Objects.requireNonNull(container).addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private static String TAG = PhotosActivity.class.getSimpleName();

    private static String PHOTO_SERVER_URL = "http://192.168.1.8:40003/photos/";

    private static String PHOTOS_LIST_URL =  PHOTO_SERVER_URL + "list";

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private VolleyHelper volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        Log.d(TAG, "onCreate()");

        volley = new VolleyHelper(this);

        hideSystemBars();

        loadPhotosList((photos) -> {
            Log.d(TAG, "loaded photos: " + photos);
            viewPager = (ViewPager)findViewById(R.id.photos_view_pager);
            Collections.shuffle(photos);
            viewPagerAdapter = new ViewPagerAdapter(this, photos);
            viewPager.setAdapter(viewPagerAdapter);
        });
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void loadPhotosList(final Consumer<List<String>> callback) {
        volley.getJSONArray(PHOTOS_LIST_URL, (response) -> {
            List<String> photos = jsonArrayToList(response);
            callback.accept(photos);
        });
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) {
        try {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
            return list;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}