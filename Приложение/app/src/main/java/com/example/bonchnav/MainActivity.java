package com.example.bonchnav;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.example.demo.FragmentInfo;
import com.example.demo.Point;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {

    Graph<Point, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    private HashMap<String, Point> hashMap;
    private HashMap<String, Bitmap> fragments = new HashMap<>();
    private Integer firstID = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.getResources().openRawResource(R.raw.toandroid));
            String graph = (String) objectInputStream.readObject();
            objectInputStream.close();
            objectInputStream = new ObjectInputStream(this.getResources().openRawResource(R.raw.toandroidpoints));
            hashMap = (HashMap<String, Point>) objectInputStream.readObject();
            objectInputStream.close();
            objectInputStream = new ObjectInputStream(this.getResources().openRawResource(R.raw.toandroidfragments));
            ArrayList<FragmentInfo> set = (ArrayList<FragmentInfo>) objectInputStream.readObject();
            for (FragmentInfo fragment:
                 set) {
                String name = "p" + fragment.fileName.substring(0, fragment.fileName.length() - 4).replace(".", "");
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(name, "drawable", this.getPackageName()));
                if(bmp != null)
                fragments.put(fragment.ID, bmp);
                if(Integer.parseInt(fragment.ID) < firstID)
                    firstID = Integer.parseInt(fragment.ID);
            }
            objectInputStream.close();
            int i = 0;
            int j = 0;
            do {
                for (; !(Character.isDigit(graph.charAt(i))); i++);
                for (j = i; (Character.isDigit(graph.charAt(j))); j++);
                    String str = graph.substring(i, j);
                    try {
                        g.addVertex(hashMap.get(graph.substring(i, j)));
                    } catch (Exception e){}
                    i = j + 1;
            } while (graph.charAt(j) != ']');
            do {
                for (; !(Character.isDigit(graph.charAt(i))); i++);
                for (j = i; (Character.isDigit(graph.charAt(j))); j++);
                Point first = (hashMap.get(graph.substring(i, j)));
                i = j + 1;
                for (; !(Character.isDigit(graph.charAt(i))); i++);
                for (j = i; (Character.isDigit(graph.charAt(j))); j++);
                Point second = (hashMap.get(graph.substring(i, j)));
                i = j + 1;
                if(first != null && second != null) {
                    DefaultWeightedEdge dwe = new DefaultWeightedEdge();
                    g.addEdge(first, second, dwe);
                    int ID = -1;
                    for (Integer fID :
                            first.positions.keySet()) {
                        for (Integer sID :
                                second.positions.keySet()) {
                            if (fID.equals(sID))
                                ID = fID;
                        }
                    }
                    if (ID != -1)
                        g.setEdgeWeight(dwe, Math.sqrt((first.getPos(ID).getFirst() - second.getPos(ID).getFirst()) * (first.getPos(ID).getFirst() - second.getPos(ID).getFirst()) + (first.getPos(ID).getSecond() - second.getPos(ID).getSecond()) * (first.getPos(ID).getSecond() - second.getPos(ID).getSecond())));
                }
            } while (graph.charAt(i) != ']');
            String str = g.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        ImageView imageView = findViewById(R.id.imageView);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                showFragment();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    int flag = 1;
    LinkedHashSet<Integer> fragmentsList = new LinkedHashSet<>();
    public void OnClickWay(View view){
            if(flag == 1){
                FindPath();
                showDeWay();
            } else
            if(flag == 2){
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(fragments.get(String.valueOf(firstID)));
                ((Button)view).setText("Найти путь");
                flag = 1;
            } else
            if(flag == 3){
                showDeWay();
            }
    }

    ArrayList<TextView> arrayListTextViev = new ArrayList<>();
    public void showFragment(){
        ImageView imageView = findViewById(R.id.imageView);
        for (TextView text:
             arrayListTextViev) {
            ((RelativeLayout) findViewById(R.id.relativeLayout)).removeView(text);
        }
        arrayListTextViev.clear();
        for (Point p:
                hashMap.values()) {
            if(p.getPos(firstID) != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) (p.getPos(firstID).getFirst() * imageView.getWidth());
                params.topMargin = (int) (p.getPos(firstID).getSecond() * imageView.getHeight());
                TextView textView = new TextView(MainActivity.this);
                if(p.getName().length() > 3)
                textView.setText(p.getName());
                textView.setTextColor(Color.RED);
                ((RelativeLayout) findViewById(R.id.relativeLayout)).addView(textView, params);
                arrayListTextViev.add(textView);
            }
        }
        Bitmap bmp = fragments.get(String.valueOf(firstID));
        imageView.setImageBitmap(bmp);
    }

    GraphPath<Point, DefaultWeightedEdge> graphPathList = null;
    public void FindPath() {
        Point from = findPoint((String) ((TextView) findViewById(R.id.cabfrom)).getText().toString());
        Point to = findPoint((String) ((TextView) findViewById(R.id.cabto)).getText().toString());
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(g);
        graphPathList = dijkstraShortestPath.getPath(from, to);
        for (DefaultWeightedEdge defaultEdge :
                graphPathList.getEdgeList()) {
            Point source = g.getEdgeSource(defaultEdge);
            Point target = g.getEdgeTarget(defaultEdge);
            if(source.positions.size() == 1) fragmentsList.add((Integer) (source.positions.keySet().toArray())[0]);
            if(target.positions.size() == 1) fragmentsList.add((Integer) (source.positions.keySet().toArray())[0]);
        }
            System.out.println(graphPathList.getWeight());
        }

    public void showDeWay(){
        firstID = fragmentsList.iterator().next();
        fragmentsList.remove(firstID);
        showFragment();
        ImageView imageView = findViewById(R.id.imageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(bitmap, new Rect(0, 0,bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0,bitmap.getWidth(), bitmap.getHeight()), null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        for (DefaultWeightedEdge defaultEdge :
                graphPathList.getEdgeList()) {
            Point source = g.getEdgeSource(defaultEdge);
            Point target = g.getEdgeTarget(defaultEdge);

            if ((source.getPos(firstID) != null) && (target.getPos(firstID) != null)) {
                canvas.drawLine((float) (source.getPos(firstID).getFirst()*bitmap.getWidth()),(float) (source.getPos(firstID).getSecond()*bitmap.getHeight()), (float) (target.getPos(firstID).getFirst()*bitmap.getWidth()),(float) (target.getPos(firstID).getSecond()*bitmap.getHeight()), paint);
            }
            if(fragmentsList.size() > 0) {
                flag = 3;
                ((Button)findViewById(R.id.PathWay)).setText("Продолжить путь");
            } else {
                flag = 2;
                ((Button)findViewById(R.id.PathWay)).setText("Скрыть путь");
            }
        }
        imageView.setImageBitmap(tempBitmap);



    }

    private Point findPoint(String name) {
        for (Point point :
                hashMap.values()) {
            if (point.getName().equals(name))
                return point;
        }
        return null;
    }

}