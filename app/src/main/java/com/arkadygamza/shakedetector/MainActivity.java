package com.arkadygamza.shakedetector;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {


    Spinner spinner;
    String [] acxios={"Ускорение по оси Х","Ускорение по оси Y", "Ускорение по оси Z "};

    String[] data = {"Ускорение по оси Х","Ускорение по оси Y", "Ускорение по оси Z "};

    private final List<SensorPlotter> mPlotters = new ArrayList<>(3);

    private Observable<?> mShakeObservable;
    private Subscription mShakeSubscription;
   GraphView graphView1;
   GraphView graphView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView1= (GraphView) findViewById(R.id.graph1);
        graphView2= (GraphView) findViewById(R.id.graph2);
        setupPlotters();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

//// заголовок
        spinner.setPrompt("Title");
//// выделяем элемент
        spinner.setSelection(2);
//// устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
//                System.out.println(position +"!!!!!!!!!!!!!!!!");
//                System.out.println(id);
                switch ((int) id){
                    case 0:
                        System.out.println("x");
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ускорение по оси ОХ!", Toast.LENGTH_SHORT);
                        toast.show();
                        graphView2.setVisibility(View.GONE);
                        graphView1.setVisibility(View.VISIBLE);
                        break;
                    case 1: System.out.println("y");
                        Toast toasts = Toast.makeText(getApplicationContext(),
                                "Ускорение по оси ОY!", Toast.LENGTH_SHORT);
                        toasts.show();
                       graphView1.setVisibility(View.GONE);
                       graphView2.setVisibility(View.VISIBLE);
                        break;
                    case 2:  System.out.println("z");
                        Toast toastss = Toast.makeText(getApplicationContext(),
                                "Ускорение по оси ОZ!", Toast.LENGTH_SHORT);
                        toastss.show();
                        graphView1.setVisibility(View.VISIBLE);
                        graphView2.setVisibility(View.VISIBLE);
                        break;
                }
// показываем позиция нажатого элемента
//                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void setupPlotters() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> accSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);


        mPlotters.add(new SensorPlotter("XACC", graphView1, SensorEventObservableFactory.createSensorEventObservable(gravSensors.get(0), sensorManager)));
        mPlotters.add(new SensorPlotter("YACC",  graphView2,SensorEventObservableFactory.createSensorEventObservable(accSensors.get(0), sensorManager)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Observable.from(mPlotters).subscribe(SensorPlotter::onResume);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Observable.from(mPlotters).subscribe(SensorPlotter::onPause);
        mShakeSubscription.unsubscribe();
    }
}
