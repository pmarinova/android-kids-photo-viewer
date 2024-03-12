package pm.android.kidsphotoviewer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

public class PhotosPagerAdapter extends PagerAdapter {

    private final Context context;
    private final List<Uri> photos;
    private final LayoutInflater layoutInflater;

    public PhotosPagerAdapter(Context context, List<Uri> photos) {
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
        return view == object;
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.photo_item, container, false);
        ImageView imageView = itemView.findViewById(R.id.photo_image_view);
        Glide.with(context).load(photos.get(position)).into(imageView);
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
