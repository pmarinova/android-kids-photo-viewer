package pm.android.kidsphotoviewer.providers;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import pm.android.kidsphotoviewer.App;
import pm.android.kidsphotoviewer.PhotosProvider;

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

        File cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String cameraDirPath = cameraDir.getAbsolutePath();
        Log.d(TAG, "Camera dir: " + cameraDirPath);

        String[] projection = new String[] { MediaStore.MediaColumns._ID };
        String selection = MediaStore.MediaColumns.DATA + " LIKE ?";
        String[] selectionArgs = new String[] { cameraDirPath + "%" };

        Log.d(TAG, "Querying media store for images from camera dir...");

        List<Uri> photos = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {

            Log.d(TAG, "Images found: " + cursor.getCount());

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
