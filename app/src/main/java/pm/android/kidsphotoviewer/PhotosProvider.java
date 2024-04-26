package pm.android.kidsphotoviewer;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface PhotosProvider {
    void loadPhotosList(
            Consumer<List<Uri>> onSuccess,
            @Nullable Consumer<String> onError);
}
