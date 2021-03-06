package com.example.luan.controlmario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

public class Controles extends Activity implements SensorEventListener {

    private DataOutputStream Salida;
    private int AnteriorX = 0, AnteriorY = 0, AnteriorZ = 0;
    private TextView Etiqueta;
    private ImageView ImageTop, ImageDown, ImageLeft, ImageRight;
    private ImageView ImageTop2, ImageDown2, ImageLeft2, ImageRight2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Etiqueta = (TextView) findViewById(R.id.Etiqueta);
        ImageTop = (ImageView) findViewById(R.id.ImgTop);
        ImageDown = (ImageView) findViewById(R.id.ImgDown);
        ImageLeft = (ImageView) findViewById(R.id.ImgLeft);
        ImageRight = (ImageView) findViewById(R.id.ImgRight);
        ImageTop2 = (ImageView) findViewById(R.id.ImgTop2);
        ImageDown2 = (ImageView) findViewById(R.id.ImgDown2);
        ImageLeft2 = (ImageView) findViewById(R.id.ImgLeft2);
        ImageRight2 = (ImageView) findViewById(R.id.ImgRight2);
        makeInvisible(1);
        makeInvisible(2);
        Operator op = new Operator();
        op.execute();
    }

    private void makeInvisible(int option) {
        switch (option) {
            case 1:
                ImageTop.setVisibility(View.INVISIBLE);
                ImageDown.setVisibility(View.INVISIBLE);
                ImageLeft.setVisibility(View.INVISIBLE);
                ImageRight.setVisibility(View.INVISIBLE);
                break;
            case 2:
                ImageTop2.setVisibility(View.INVISIBLE);
                ImageDown2.setVisibility(View.INVISIBLE);
                ImageLeft2.setVisibility(View.INVISIBLE);
                ImageRight2.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorManager Manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> Sensores = Manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (Sensores.size() > 0) {
            Manager.registerListener(this, Sensores.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SensorManager Manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Manager.unregisterListener(this);
    }

    private boolean between(int Valor) {
        return (Valor >= -1 && Valor <= 1);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {

            int ActualX = (int) sensorEvent.values[0];
            int ActualY = (int) sensorEvent.values[1];
            int ActualZ = (int) sensorEvent.values[2];

            if (Valores.InicialX == 0 && Valores.InicialY == 0 && Valores.InicialZ == 0) {
                Valores.InicialX = ActualX;
                Valores.InicialY = ActualY;
                Valores.InicialZ = ActualZ;
            }

            /* cuando el celular esta en vertical */
          //  if (between(Valores.InicialX) || between(Valores.InicialY)) {
                if (ActualX != AnteriorX || ActualY != AnteriorY || ActualZ != AnteriorZ) {

                    try {
                        if (ActualX < -1) {
                            Salida.writeUTF("caminar a la derecha");
                            Etiqueta.setText("caminar a la derecha");
                            OcultarImagenes("right", 1);
                        }
                        if (ActualX > 1) {
                            Salida.writeUTF("caminar a la izquierda");
                            Etiqueta.setText("caminar a la izquierda");
                            OcultarImagenes("left", 1);
                        }
                        if (ActualZ < -1) {
                            Salida.writeUTF("agachar");
                            Etiqueta.setText("agachar");
                            OcultarImagenes("down", 1);
                        }
                        if (ActualZ > 1) {
                            Salida.writeUTF("saltar");
                            Etiqueta.setText("saltar");
                            OcultarImagenes("top", 1);
                        }
                    } catch (Exception e) {
                        Log.e("Sensor", "Falló el envio al servidor");
                    }

                    AnteriorX = ActualX;
                    AnteriorY = ActualY;
                    AnteriorZ = ActualZ;
                }
            //}
            /* cuando el celular esta en horizontal */
            /*else if (between(Valores.InicialY) && between(Valores.InicialZ)) {
                if (ActualX != AnteriorX || ActualY != AnteriorY || ActualZ != AnteriorZ) {
                    try {
                        if (ActualY < -1) {
                            Salida.writeUTF("caminar a la derecha");
                            Etiqueta.setText("caminar a la derecha");
                            OcultarImagenes("top", 2);
                        }
                        if (ActualY > 1) {
                            Salida.writeUTF("caminar a la izquierda");
                            Etiqueta.setText("caminar a la izquierda");
                            OcultarImagenes("down", 2);
                        }
                        if (ActualZ < -1) {
                            Salida.writeUTF("agachar");
                            Etiqueta.setText("agachar");
                            OcultarImagenes("right", 2);
                        }
                        if (ActualZ > 1) {
                            Salida.writeUTF("saltar");
                            Etiqueta.setText("saltar");
                            OcultarImagenes("left", 2);
                        }
                    } catch (Exception e) {
                        Log.e("Sensor", "Falló el envio al servidor");
                    }
                }
            } */

        }
    }

    private void OcultarImagenes(String orientacion, int option) {
        /* pone las imágenes en invisible */
        makeInvisible(option);

        /* dependiendo su orientación, las vuelve a mostrar */
        switch (orientacion) {
            case "top":
                (option == 1 ? ImageTop : ImageTop2).setVisibility(View.VISIBLE);
                break;
            case "down":
                (option == 1 ? ImageDown : ImageDown2).setVisibility(View.VISIBLE);
                break;
            case "left":
                (option == 1 ? ImageLeft : ImageLeft2).setVisibility(View.VISIBLE);
                break;
            case "right":
                (option == 1 ? ImageRight : ImageRight2).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class Operator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket cliente = new Socket("192.168.137.1", 10999);
                Salida = new DataOutputStream(cliente.getOutputStream());
            } catch (Exception e) {
                Log.e("Operator", "Falló la conexión");
            }
            return null;
        }
    }
}
