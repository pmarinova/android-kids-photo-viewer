package pm.android.kidsphotoviewer;

import android.content.Context;
import android.os.Bundle;
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

import java.util.Objects;

public class PhotosActivity extends AppCompatActivity {

    private static class ViewPagerAdapter extends PagerAdapter {

        private final Context context;
        private final int[] images;
        private final LayoutInflater layoutInflater;

        public ViewPagerAdapter(Context context, int[] images) {
            this.context = context;
            this.images = images;
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
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
            imageView.setImageResource(images[position]);
            Objects.requireNonNull(container).addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private static final int[] IMAGES = {
            R.drawable.bear,
            R.drawable.bird,
            R.drawable.cat,
            R.drawable.flamingo
    };

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        hideSystemBars();

        viewPager = (ViewPager)findViewById(R.id.photos_view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this, IMAGES);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}