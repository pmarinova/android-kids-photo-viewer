package pm.android.kidsphotoviewer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PhotoServerProvider implements PhotosProvider {

    private static final String TAG = PhotoServerProvider.class.getName();

    private static final String PHOTO_SERVER_URL = "http://192.168.1.8:40003";

    private static final String PHOTOS_BASE_URL = PHOTO_SERVER_URL + "/photos";

    private static final String PHOTOS_LIST_URL =  PHOTOS_BASE_URL + "/list";

    private final RequestQueue requestQueue;

    public PhotoServerProvider(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void loadPhotosList(final Consumer<List<Uri>> callback) {
        this.requestQueue.add(new JsonArrayRequest(
                PHOTOS_LIST_URL,
                (response) -> {
                    List<Uri> photos = getPhotoURLs(jsonArrayToList(response));
                    Collections.shuffle(photos);
                    callback.accept(photos);
                },
                (error) -> Log.e(TAG, "request failed: " + error)
        ));
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) {
        try {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
            return list;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Uri> getPhotoURLs(List<String> photoPaths) {
        return photoPaths.stream()
                .map(PhotoServerProvider::getPhotoURL)
                .collect(Collectors.toList());
    }

    private static Uri getPhotoURL(String photo) {
        return Uri.parse(PHOTOS_BASE_URL + "/" + photo);
    }
}
