package pm.android.kidsphotoviewer;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

public class PrefsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference photosProvider;
    private SwitchPreferenceCompat slideshowEnable;
    private SeekBarPreference slideshowInterval;

    private SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        photosProvider = findPreference(R.string.pref_key_photos_provider);
        slideshowEnable = findPreference(R.string.pref_key_slideshow_enable);
        slideshowInterval = findPreference(R.string.pref_key_slideshow_interval);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferencesSummary();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferencesSummary();
    }

    private void updatePreferencesSummary() {
        photosProvider.setSummary(photosProvider.getEntry());
        slideshowInterval.setSummary(slideshowInterval.getValue() + " seconds");
    }

    private <T> T findPreference(int keyResId) {
        return (T)findPreference(getString(keyResId));
    }
}
