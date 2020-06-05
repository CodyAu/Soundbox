package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Themes;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer.WaveformRendererFactory;

public class SettingsActivity extends AppCompatActivity {
    private static final int VOLUME_SAMPLE_RATE = 50;

    private Toolbar toolbar;

    private SwitchCompat themeSwitch;

    private SeekBar volumeSeekBar;
    private TextView volumeContent;

    private Spinner visualizationType;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE); // Get shared preferences
        Themes.changeThemeModeCreate(this, sharedPreferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        volumeSeekBar = (SeekBar) findViewById(R.id.volume_seekbar);
        volumeContent = (TextView) findViewById(R.id.volume_content);

        themeSwitch = (SwitchCompat) findViewById(R.id.theme_switch);

        visualizationType = (Spinner) findViewById(R.id.visualization_type_spinner);

        initToolbar();
        initThemes();
        initPlayback();
        initVisualizations();
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.action_settings));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initThemes() {
        themeSwitch.setChecked(sharedPreferences.getBoolean(getString(R.string.shared_pref_theme), true));
        themeSwitch.setOnCheckedChangeListener((compoundButton, state) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.shared_pref_theme), state);
            editor.apply();

            Themes.changeThemeMode(this, sharedPreferences, !state);
        });
    }

    private void initPlayback() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int minVolume = 0;
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int seekMin = 0;
        int seekMax = 100;

        volumeSeekBar.setMax(seekMax);
        volumeSeekBar.setProgress((int) Calculate.mapRange(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), minVolume, maxVolume, seekMin, seekMax));

        volumeContent.setText(String.valueOf(volumeSeekBar.getProgress()));

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isUser) {
                if (isUser) {
                    int newVolume = (int) Calculate.mapRange(progress, seekMin, seekMax, minVolume, maxVolume);
                    if (newVolume != audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                }

                volumeContent.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pass
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Pass
            }
        });

        //update seek bar on the timer
        final Handler handler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (streamVolume != (int) Calculate.mapRange(volumeSeekBar.getProgress(), seekMin, seekMax, minVolume, maxVolume)) {
                    volumeSeekBar.setProgress((int) Calculate.mapRange(streamVolume, minVolume, maxVolume, seekMin, seekMax));
                }

                handler.postDelayed(this, VOLUME_SAMPLE_RATE);
            }
        });
    }

    private void initVisualizations() {
        WaveformRendererFactory.RendererType[] values = WaveformRendererFactory.RendererType.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            String name = values[i].name();
            name = name.replace('_', ' ');
            names[i] = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visualizationType.setAdapter(adapter);

        visualizationType.setSelection(sharedPreferences.getInt(getString(R.string.shared_pref_visualization_type), 0));
        visualizationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.shared_pref_visualization_type), position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Pass
            }
        });
    }

}
