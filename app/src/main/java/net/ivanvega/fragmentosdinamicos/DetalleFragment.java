package net.ivanvega.fragmentosdinamicos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import net.ivanvega.fragmentosdinamicos.services.MiServicio;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleFragment extends Fragment
        implements View.OnTouchListener,MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static String ARG_INDEX_LIBRO = "idLibro";
    private TextView lblTitulo;
    private TextView lblAutor;
    private ImageView imvPortada;

    MediaPlayer mediaPlayer;
    MediaController mediaController;

    public DetalleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleFragment newInstance(String param1, String param2) {
        DetalleFragment fragment = new DetalleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =
                inflater.inflate(R.layout.fragment_detalle_layout,
                        container, false);

        layout.setOnTouchListener(this);

        Spinner spinner =
                layout.findViewById(R.id.spnGeneros);

        String[] generos
                =  getResources().getStringArray(R.array.generos);

        ArrayAdapter<String> adapter =
                new ArrayAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, generos
                        );

        spinner.setAdapter(adapter);

          Bundle args = getArguments();

          if(args != null){
               int idLibro =
                       args.getInt(DetalleFragment.ARG_INDEX_LIBRO);
               setInfoLibro(idLibro,layout );
          }else{
              setInfoLibro(0, layout);
          }


        return layout;
    }

    private void setInfoLibro(int idLibro, View layout) {
        Libro libro = Libro.ejemplosLibros().elementAt(idLibro);

        lblTitulo = layout.findViewById(R.id.titulo);
        lblAutor = layout.findViewById(R.id.autor);
        imvPortada = layout.findViewById(R.id.portada);

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        imvPortada.setImageResource(libro.getRecursoImagen());

        Uri audio = Uri.parse(libro.getUrl()); // Uri que maneja la localización de archivos.
        String enviarUri = String.valueOf(audio);

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            Intent serviceIntent = new Intent(getActivity().getApplicationContext(), MiServicio.class);
            serviceIntent.putExtra("inputExtra", enviarUri);
            serviceIntent.putExtra("bookName", libro.getTitulo());
            ContextCompat.startForegroundService(getActivity(), serviceIntent);
        }

        if( servicioLibros.mediaPlayer!= null){
            servicioLibros.mediaPlayer.release();
        }

            servicioLibros.mediaPlayer = new MediaPlayer();
            servicioLibros.mediaPlayer.setOnPreparedListener(this);
            mediaController = new MediaController(getActivity());
            try {
                servicioLibros.mediaPlayer.setDataSource(getActivity(),
                        Uri.parse(libro.getUrl()));
                servicioLibros.mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }



    public void setInfoLibro(int pos) {

        this.setInfoLibro(pos,getView()    );
    }

    MiServicio servicioLibros = new MiServicio();
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        servicioLibros.mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView());
        mediaController.setEnabled(true);
        mediaController.show();


    }

    @Override
    public void start() {
        servicioLibros.mediaPlayer.start();
    }

    @Override
    public void pause() {
        servicioLibros.mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return servicioLibros.mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return servicioLibros.mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        servicioLibros.mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return servicioLibros.mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return servicioLibros.mediaPlayer.getAudioSessionId();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mediaController.show();
        return false;
    }


}