package pm.android.kidsphotoviewer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class LocalPhotosProvider implements PhotosProvider {

    private static final String TAG = LocalPhotosProvider.class.getName();

    private final ExecutorService executor;

    private final Handler mainThreadHandler;

    private final ContentResolver contentResolver;

    public LocalPhotosProvider(Context context) {
        this.executor = ((App)context.getApplicationContext()).getExecutorService();
        this.mainThreadHandler = ((App)context.getApplicationContext()).getMainThreadHandler();
        this.contentResolver = context.getContentResolver();
    }

    @Override
    public void loadPhotosList(Consumer<List<Uri>> callback) {
        executor.execute(() -> {
            List<Uri> photos = queryMediaStore();
            mainThreadHandler.post(() -> callback.accept(photos));
        });
    }

    private List<Uri> queryMediaStore() {
        Log.d(TAG, "Querying media store...");
        List<Uri> photos = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{ MediaStore.MediaColumns._ID },
                null, null, null
        )) {
            Log.d(TAG, "Media items found: " + cursor.getCount());
            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                photos.add(contentUri);
            }
        }
        return photos;
    }
}
