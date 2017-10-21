package io.paperplane.rajb.nutrifacts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Graphing extends AppCompatActivity {

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);

         mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        GraphView graph = (GraphView) findViewById(R.id.graph);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1567),
                new DataPoint(2, 3956),
                new DataPoint(3, 2358),
                new DataPoint(4, 3281)
        });

        graph.setTitle("Your Calorie Breakdown");

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setMinY(0.0);
        graph.getViewport().setMaxY(8000.0);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*5/4, (int) Math.abs(data.getY()*2553/6), 100);
            }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        graph.addSeries(series);

        GraphView graph1 = (GraphView) findViewById(R.id.graph1);
        graph1.setTitle("Fats Breakdown");


        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1567),
                new DataPoint(2, 3956),
                new DataPoint(3, 2358),
                new DataPoint(4, 3281)
        });

        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(5);
        graph1.getViewport().setMinY(0.0);
        graph1.getViewport().setMaxY(8000.0);

        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setXAxisBoundsManual(true);

        // styling
        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series1.setSpacing(50);

// draw values on top
        series1.setDrawValuesOnTop(true);
        series1.setValuesOnTopColor(Color.RED);
        graph1.addSeries(series1);



        //--------------

        GraphView graph2 = (GraphView) findViewById(R.id.graph2);
        graph2.setTitle("Protein Breakdown");


        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1567),
                new DataPoint(2, 3956),
                new DataPoint(3, 2358),
                new DataPoint(4, 3281)
        });

        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(5);
        graph2.getViewport().setMinY(0.0);
        graph2.getViewport().setMaxY(8000.0);

        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setXAxisBoundsManual(true);


        // styling
        series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series2.setSpacing(50);

// draw values on top
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.RED);
        graph2.addSeries(series2);


        //--------------

        GraphView graph3 = (GraphView) findViewById(R.id.graph3);
        graph3.setTitle("Carbohydrate Breakdown");


        BarGraphSeries<DataPoint> series3 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1567),
                new DataPoint(2, 3956),
                new DataPoint(3, 2358),
                new DataPoint(4, 3281)
        });

        graph3.getViewport().setMinX(0);
        graph3.getViewport().setMaxX(5);
        graph3.getViewport().setMinY(0.0);
        graph3.getViewport().setMaxY(8000.0);

        graph3.getViewport().setYAxisBoundsManual(true);
        graph3.getViewport().setXAxisBoundsManual(true);


        // styling
        series3.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series3.setSpacing(50);

// draw values on top
        series3.setDrawValuesOnTop(true);
        series3.setValuesOnTopColor(Color.RED);
        graph3.addSeries(series3);

    }
}
