package pm.android.kidsphotoviewer.providers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import pm.android.kidsphotoviewer.PhotosProvider;
import pm.android.kidsphotoviewer.providers.util.NsdServiceResolver;

public class PhotoServerProvider implements PhotosProvider {

    private static final String TAG = PhotoServerProvider.class.getName();

    private static final String PHOTO_SERVER_SERVICE_TYPE = "_photo-server._tcp";

    private static final String PHOTOS_BASE_PATH = "/photos";

    private static final String PHOTOS_LIST_PATH =  PHOTOS_BASE_PATH + "/list";

    private final NsdServiceResolver nsdServiceResolver;

    private final RequestQueue requestQueue;

    public PhotoServerProvider(Context context) {
        this.nsdServiceResolver = new NsdServiceResolver(context);
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void loadPhotosList(Consumer<List<Uri>> onSuccess, @Nullable Consumer<String> onError) {

        Log.d(TAG, "Resolving photo server service...");
        this.nsdServiceResolver.resolveService(
                PHOTO_SERVER_SERVICE_TYPE,
                (photoServerInfo) -> {
                    InetAddress host = photoServerInfo.getHost();
                    int port = photoServerInfo.getPort();

                    String photoServerURL = getPhotoServerURL(host, port);
                    String photosListURL = photoServerURL + PHOTOS_LIST_PATH;

                    Log.d(TAG, "Listing photos from " + photosListURL);
                    this.requestQueue.add(new JsonArrayRequest(
                            photosListURL,
                            (response) -> onSuccess.accept(getPhotoURLs(photoServerURL, jsonArrayToList(response))),
                            (error) -> { if (onError != null) onError.accept(error.getMessage()); }
                    ));
                },
                (error) -> { if (onError != null) onError.accept(error); },
                10000);
    }

    private static String getPhotoServerURL(InetAddress host, int port) {
        return "http://" + host + ":" + port;
    }

    private static List<Uri> getPhotoURLs(String photoServerUrl, List<String> photoPaths) {
        return photoPaths.stream()
                .map((photoPath) -> getPhotoURL(photoServerUrl, photoPath))
                .collect(Collectors.toList());
    }

    private static Uri getPhotoURL(String photoServerUrl, String photoPath) {
        return Uri.parse(photoServerUrl + PHOTOS_BASE_PATH + "/" + photoPath);
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
}
