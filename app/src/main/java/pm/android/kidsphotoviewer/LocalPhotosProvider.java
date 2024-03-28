package pm.android.kidsphotoviewer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.nio.file.Path;
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

        List<Uri> photos = new ArrayList<>();

        Log.d(TAG, "Querying media store...");
        try (Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{ MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA },
                null, null, null
        )) {

            Log.d(TAG, "Media items found: " + cursor.getCount());

            File cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String cameraDirPath = cameraDir.getAbsolutePath();

            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                String path = cursor.getString(dataColumnIndex);

                if (path.startsWith(cameraDirPath)) {
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    photos.add(contentUri);
                }
            }
        }

        return photos;
    }
}
