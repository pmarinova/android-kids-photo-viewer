package pm.android.kidsphotoviewer;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

public class App extends Application {

    private final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    public Handler getMainThreadHandler() {
        return this.mainThreadHandler;
    }
}
