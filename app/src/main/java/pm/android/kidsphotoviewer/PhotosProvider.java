package pm.android.kidsphotoviewer;

import android.net.Uri;

import java.util.List;
import java.util.function.Consumer;

public interface PhotosProvider {
    void loadPhotosList(Consumer<List<Uri>> callback);
}
