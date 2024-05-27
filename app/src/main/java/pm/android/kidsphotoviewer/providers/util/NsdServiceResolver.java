package pm.android.kidsphotoviewer.providers.util;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.function.Consumer;

import pm.android.kidsphotoviewer.App;

public class NsdServiceResolver {

    private static final String TAG = NsdServiceResolver.class.getName();

    private final NsdManager nsdManager;

    private final Handler mainThreadHandler;

    private NsdServiceInfo foundService;

    public NsdServiceResolver(Context context) {
        this.nsdManager = (NsdManager)context.getSystemService(Context.NSD_SERVICE);
        this.mainThreadHandler = ((App)context.getApplicationContext()).getMainThreadHandler();
    }

    public void resolveService(
            String serviceType,
            Consumer<NsdServiceInfo> onSuccess,
            @Nullable Consumer<String> onError,
            int timeout) {

        Consumer<NsdServiceInfo> successHandler = (resolvedService) -> {
            mainThreadHandler.post(() -> {
                onSuccess.accept(resolvedService);
            });
        };

        Consumer<String> errorHandler = (error) -> {
            mainThreadHandler.post(() -> {
               if (onError != null) {
                   onError.accept(error);
               }
            });
        };

        NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "Service discovery start failed with error code " + errorCode);
                errorHandler.accept("Failed to start service discovery");
            }


            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service found: " + serviceInfo.getServiceName());
                foundService = serviceInfo;
                nsdManager.stopServiceDiscovery(this);
                nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        Log.d(TAG, "Service resolved: " + serviceInfo);
                        successHandler.accept(serviceInfo);
                    }
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Log.d(TAG, "Service resolve failed with error code " + errorCode);
                        errorHandler.accept("Failed to resolve service: " + serviceType);
                    }
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service lost: " + serviceInfo.getServiceName());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Service discovery stopped");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "Service discovery stop failed with error code " + errorCode);
            }
        };

        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener);

        mainThreadHandler.postDelayed(() -> {
            if (foundService == null) {
                nsdManager.stopServiceDiscovery(discoveryListener);
                errorHandler.accept("Timed out while resolving service: " + serviceType);
            }
        }, timeout);
    }
}
