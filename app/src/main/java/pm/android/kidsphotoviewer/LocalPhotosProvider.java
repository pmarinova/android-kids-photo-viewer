package pm.android.kidsphotoviewer;

import android.net.Uri;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class LocalPhotosProvider implements PhotosProvider {

    @Override
    public void loadPhotosList(Consumer<List<Uri>> callback) {
        callback.accept(Collections.emptyList()); //TODO
    }
}
