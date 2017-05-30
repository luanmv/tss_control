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
    private TextView ValorX, ValorY, ValorZ;
    private ImageView ImageTop, ImageDown, ImageLeft, ImageRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ValorX = (TextView) findViewById(R.id.valX);
        ValorY = (TextView) findViewById(R.id.valY);
        ValorZ = (TextView) findViewById(R.id.valZ);
        ImageTop = (ImageView) findViewById(R.id.ImgTop);
        ImageDown = (ImageView) findViewById(R.id.ImgDown);
        ImageLeft = (ImageView) findViewById(R.id.ImgLeft);
        ImageRight = (ImageView) findViewById(R.id.ImgRight);
        ImageTop.setVisibility(View.INVISIBLE);
        ImageDown.setVisibility(View.INVISIBLE);
        ImageLeft.setVisibility(View.INVISIBLE);
        ImageRight.setVisibility(View.INVISIBLE);
        Operator op = new Operator();
        op.execute();
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
        return (Valor >= -1 && Valor <= 1) ? true : false;
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

            if (between(Valores.InicialX) && between(Valores.InicialY)) {
                if (ActualX != AnteriorX || ActualY != AnteriorY || ActualZ != AnteriorZ) {
                    ValorX.setText("Acelerómetro X: " + ActualX);
                    ValorY.setText("Acelerómetro Y: " + ActualY);
                    ValorZ.setText("Acelerómetro Z: " + ActualZ);
                    try {
                        if (ActualX < -1) {
                            Salida.writeUTF("caminar a la derecha");
                            OcultarImagenes("right");
                        }
                        if (ActualX > 1) {
                            Salida.writeUTF("caminar a la izquierda");
                            OcultarImagenes("left");
                        }
                        if (ActualZ < -1) {
                            Salida.writeUTF("agachar");
                            OcultarImagenes("down");
                        }
                        if (ActualZ > 1) {
                            Salida.writeUTF("saltar");
                            OcultarImagenes("top");
                        }
                    } catch (Exception e) {
                        Log.e("Sensor", "Falló el envio al servidor");
                    }

                    AnteriorX = ActualX;
                    AnteriorY = ActualY;
                    AnteriorZ = ActualZ;
                }
            } else if (between(Valores.InicialY) && between(Valores.InicialZ)) {
                if (ActualX != AnteriorX || ActualY != AnteriorY || ActualZ != AnteriorZ) {
                    ValorX.setText("Acelerómetro X: " + ActualX);
                    ValorY.setText("Acelerómetro Y: " + ActualY);
                    ValorZ.setText("Acelerómetro Z: " + ActualZ);

                    try {
                        if (ActualY < -1) {
                            Salida.writeUTF("caminar a la derecha");
                            OcultarImagenes("top");
                        }
                        if (ActualY > 1) {
                            Salida.writeUTF("caminar a la izquierda");
                            OcultarImagenes("down");
                        }
                        if (ActualZ < -1) {
                            Salida.writeUTF("agachar");
                            OcultarImagenes("right");
                        }
                        if (ActualZ > 1) {
                            Salida.writeUTF("saltar");
                            OcultarImagenes("left");
                        }
                    } catch (Exception e) {
                        Log.e("Sensor", "Falló el envio al servidor");
                    }
                }
            }

        }
    }

    private void OcultarImagenes(String orientacion) {
        /* pone las imágenes en invisible */
        ImageRight.setVisibility(View.INVISIBLE);
        ImageTop.setVisibility(View.INVISIBLE);
        ImageDown.setVisibility(View.INVISIBLE);
        ImageLeft.setVisibility(View.INVISIBLE);

        /* dependiendo su orientación, las vuelve a mostrar */
        switch (orientacion) {
            case "top":
                ImageTop.setVisibility(View.VISIBLE);
                break;
            case "down":
                ImageDown.setVisibility(View.VISIBLE);
                break;
            case "left":
                ImageLeft.setVisibility(View.VISIBLE);
                break;
            case "right":
                ImageRight.setVisibility(View.VISIBLE);
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
