package pm.android.kidsphotoviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStart = findViewById(R.id.btn_start);
        Button buttonSettings = findViewById(R.id.btn_settings);

        buttonStart.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, PhotosActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
        });
    }
}
