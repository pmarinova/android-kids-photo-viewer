package pm.android.kidsphotoviewer.providers;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import pm.android.kidsphotoviewer.App;
import pm.android.kidsphotoviewer.PhotosProvider;
import pm.android.kidsphotoviewer.R;

public class LocalPhotosProvider implements PhotosProvider {

    private static final String TAG = LocalPhotosProvider.class.getName();

    private final ExecutorService executor;

    private final Handler mainThreadHandler;

    private final Context context;

    public LocalPhotosProvider(Context context) {
        this.executor = ((App)context.getApplicationContext()).getExecutorService();
        this.mainThreadHandler = ((App)context.getApplicationContext()).getMainThreadHandler();
        this.context = context;
    }

    @Override
    public void loadPhotosList(Consumer<List<Uri>> onSuccess, @Nullable Consumer<String> onError) {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            executor.execute(() -> {
                List<Uri> photos = queryMediaStore();
                mainThreadHandler.post(() -> onSuccess.accept(photos));
            });
        } else {
            if (onError != null) {
                String error = getString(R.string.error_local_photos_no_permission);
                onError.accept(error);
            }
        }
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
        try (Cursor cursor = context.getContentResolver().query(
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

    private String getString(int resId) {
        return context.getString(resId);
    }
}
