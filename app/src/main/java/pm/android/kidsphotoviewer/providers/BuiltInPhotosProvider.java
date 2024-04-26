package pm.android.kidsphotoviewer.providers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.AnyRes;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import pm.android.kidsphotoviewer.PhotosProvider;
import pm.android.kidsphotoviewer.R;

public class BuiltInPhotosProvider implements PhotosProvider {

    private static final List<Integer> PHOTOS = Arrays.asList(
            R.drawable.bear,
            R.drawable.bird,
            R.drawable.cat,
            R.drawable.flamingo
    );

    private final Resources res;

    public BuiltInPhotosProvider(Context context) {
        this.res = context.getResources();
    }

    @Override
    public void loadPhotosList(Consumer<List<Uri>> onSuccess, @Nullable Consumer<String> onError) {
        List<Uri> photos = PHOTOS.stream().map(this::getUri).collect(Collectors.toList());
        onSuccess.accept(photos);
    }

    private Uri getUri(@AnyRes int resId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
    }
}
