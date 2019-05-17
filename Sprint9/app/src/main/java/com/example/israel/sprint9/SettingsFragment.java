package com.example.israel.sprint9;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
    public static final int REQUEST_MARKER_DROP_AUDIO = 1;

    private TextView markerDropAudioPathTextView;

    private OnSelectAudioInterface onSelectAudioInterface;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        markerDropAudioPathTextView = view.findViewById(R.id.fragment_settings_text_marker_drop_audio_path);
        Uri audioUri = MarkerDropAudioSPDAO.getMarkerDropAudio(getContext());
        if (audioUri != null) {
            markerDropAudioPathTextView.setText(audioUri.toString());
        }

        Button setMarkerDropAudio = view.findViewById(R.id.fragment_settings_button_set_marker_drop_audio);
        setMarkerDropAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAudioInterface.onSelectAudio(new OnAudioSelectedListener() {
                    @Override
                    public void onAudioSelected(Uri audioUri) {
                        if (audioUri != null) {
                            MarkerDropAudioSPDAO.setMarkerDropAudio(getContext(), audioUri);
                            markerDropAudioPathTextView.setText(audioUri.toString());
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSelectAudioInterface) {
            onSelectAudioInterface = (OnSelectAudioInterface) context;
        }
    }

    interface OnSelectAudioInterface {
        void onSelectAudio(OnAudioSelectedListener l);
    }

    interface OnAudioSelectedListener {
        void onAudioSelected(Uri audioUri);
    }
}
