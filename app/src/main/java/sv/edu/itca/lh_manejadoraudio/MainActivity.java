package sv.edu.itca.lh_manejadoraudio;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageButton bGrabar, bDetenerG, bReproducir, bDetenerR;
    private TextView tvRutas;
    private MediaRecorder mRec;
    private MediaPlayer mPlayer;

    private String fichero, strHora;
    Calendar MiCalendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bGrabar = findViewById(R.id.bGrabar);
        bReproducir = findViewById(R.id.bReproducir);
        bDetenerG = findViewById(R.id.bDetenerG);
        bDetenerR = findViewById(R.id.bDetenerR);
        tvRutas = findViewById(R.id.tvRutas);

        ControlarBotones(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);

        VerificarPermisos();
    }

    private void VerificarPermisos() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
        &&
        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1000
            );
        }
    }

    private void ControlarBotones(int b1, int b2, int b3, int b4) {
        bGrabar.setVisibility(b1);
        bDetenerG.setVisibility(b2);
        bReproducir.setVisibility(b3);
        bDetenerR.setVisibility(b4);
    }

    public void SonidoClic(View view) {
        MediaPlayer sonido = MediaPlayer.create(this, R.raw.click);
        sonido.start();
    }

    public void SonidoAlarma(View view) {
        MediaPlayer sonido = MediaPlayer.create(this, R.raw.alarma);
        sonido.start();
    }

    public void SonidoExplosion(View view) {
        MediaPlayer sonido = MediaPlayer.create(this, R.raw.explosion);
        sonido.start();
    }

    private void MessageBox(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    private void generarRutaArchivo() {
        strHora = String.valueOf(MiCalendario.get(Calendar.HOUR_OF_DAY));
        strHora += String.valueOf(MiCalendario.get(Calendar.MINUTE));
        strHora += String.valueOf(MiCalendario.get(Calendar.SECOND));

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File DirectorioMusica = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File MiArchivo = new File(DirectorioMusica, "Grabacion"+strHora+".3gp");
        fichero = MiArchivo.getPath();

        tvRutas.setText("Ruta de Archivo: \n" + fichero);
    }

    public void Grabar(View view) {

        generarRutaArchivo();
        mRec = new MediaRecorder();
        mRec.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRec.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRec.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRec.setOutputFile(fichero);

        try {
            mRec.prepare();
            mRec.start();
            MessageBox("Grabando Audio");
            ControlarBotones(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
        } catch (IOException e) {
            MessageBox("Error al intentar grabar");
        }


    }

    public void DetenerGrabacion(View view) {
        mRec.stop();
        mRec.release();
        mRec = null;
        MessageBox("Audio Grabado Exitosamente");
        ControlarBotones(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);

    }

    public void Reproducir(View view) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fichero);
            mPlayer.prepare();
            mPlayer.start();
            MessageBox("Reproduciendo Grabación");
            ControlarBotones(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
        } catch (IOException e) {
            MessageBox("Error en intentar reproducir la grabación");
        }
    }

    public void DetenerReproduccion(View view) {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        MessageBox("Reproducción Detenida");
    }
}