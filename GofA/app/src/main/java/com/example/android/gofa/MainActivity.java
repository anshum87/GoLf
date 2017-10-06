package com.example.android.gofa;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.MenuPopupWindow;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.Button;

import android.util.Log;

import java.lang.Thread;
import java.lang.Runnable;
import java.lang.StrictMath;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomAdapter customadapter = new CustomAdapter(getApplicationContext(),
                30, 30);

        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Timer myTimer;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    final CustomAdapter customadapter = (CustomAdapter) gridview.getAdapter();
                    myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    customadapter.ReloadNext();
                                }
                            });
                        };
                    }, 100, 200); // initial delay 1 second, interval 1 second
                }
                else {
                    myTimer.cancel();
                }
            }

        });

        Button NextButton = (Button) findViewById(R.id.nextbutton);
        NextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                GridView gridview = (GridView) findViewById(R.id.gridview);
                ((CustomAdapter)gridview.getAdapter()).ReloadNext();
                //checkbox.setEnabled(false);
            }
        });

        Button InitButton = (Button) findViewById(R.id.initbutton);
        InitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        checkbox.setEnabled(true);
                    }
                });
            }
        });

        final MyPatternRegistry mypattreg =  new MyPatternRegistry();

        android.widget.Spinner spinner= (android.widget.Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> patt_list_adaptor = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        for(String patt_name: mypattreg.pattern_name_map.keySet()) {
            patt_list_adaptor.add(patt_name);
        }
        spinner.setAdapter(patt_list_adaptor);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkbox.setEnabled(true);

                StartPattern patt=
                        mypattreg.GetPatternFromString(parent.
                                getItemAtPosition(position).toString());

                GridView gridview = (GridView) findViewById(R.id.gridview);
                System.out.println("adigupta");
                final CustomAdapter customadapter = new CustomAdapter(getApplicationContext(), patt.gridX, patt.gridY);
                gridview.setAdapter(customadapter);
                gridview.setNumColumns(patt.gridY);

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        /*if(position%5==0 || position%7==0) {
                            Toast.makeText(getApplication().getApplicationContext(), "" + position,
                                    Toast.LENGTH_SHORT).show();
                        }*/

                        CheckBox checkbox2 = (CheckBox) findViewById(R.id.checkbox2);

                        if(checkbox2.isChecked()) {
                            customadapter.EditAtPosition(position);
                        }

                    }
                });

                customadapter.Init(patt);
            };

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        /*Button EmptyButton = (Button) findViewById(R.id.apc);
        EmptyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                Toast mytoast = Toast.makeText(getApplication().getApplicationContext(),getResources().getText(R.string.gof_description),
                        Toast.LENGTH_SHORT);

                mytoast.getView().setBackgroundColor(Color.WHITE);
                mytoast.setGravity(Gravity.TOP|Gravity.LEFT, 30, 170);
                mytoast.show();

            }
        });
        */

        //android.widget.Spinner myspinner = (android.widget.Spinner) findViewById(R.id.spinner);
        //long it=myspinner.getSelectedItemId();
    }
}

class StartPattern {
    class Mask {
        int offX;
        int offY;
        int [][] mask;
        Mask(int[][] pmask, int poffX, int poffY) {
            mask=pmask;
            offX=poffX;
            offY=poffY;
        }
    }

    int gridX;
    int gridY;

    ArrayList<Mask> mask_list;

    StartPattern(int pgridX, int pgridY) {
        gridX = pgridX;
        gridY = pgridY;
        mask_list= new ArrayList<Mask>();
    }

    void AddMask(int offX, int offY, int[][] mask) {
        mask_list.add(new Mask(mask, offX, offY));
    }
}

class MyPatternRegistry {
    Map<String, StartPattern> pattern_name_map;
    MyPatternRegistry() {
        pattern_name_map = new java.util.LinkedHashMap<String, StartPattern>();
        InitBlank();
        InitGlider();
        InitGliderGun();
        InitLoaf();
        InitLWspaceship();
        InitPentadecathlon();
        InitToad();
    }
    StartPattern GetPatternFromString(String pattname) {
        return pattern_name_map.get(pattname);
    }

    void InitBlank() {
        StartPattern sp=new StartPattern(20, 20);

        int[][] mask1 = new int[][] {
        };

        sp.AddMask(3, 3, mask1);

        pattern_name_map.put("Blank", sp);

    }

    void InitGlider() {
        StartPattern sp=new StartPattern(20, 20);

        int[][] mask1 = new int[][] {
                {1, 0, 0},
                {0, 1, 1},
                {1, 1, 0}
        };

        sp.AddMask(3, 3, mask1);

        pattern_name_map.put("Glider", sp);

    }

    public void InitGliderGun() {
        StartPattern sp= new StartPattern(24, 38);

        int[][] mask1 = new int[][] {
                {1,1},
                {1,1},
        };

        sp.AddMask(5, 1, mask1);
        sp.AddMask(3, 35, mask1);

        int[][] mask2 = new int[][]{
                {0,0,1,1},
                {0,1,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,1}
        };

        sp.AddMask(3, 11, mask2);

        int[][] mask3 = new int[][]{
                {0,1,0,0},
                {0,0,1,0},
                {1,0,1,1},
                {0,0,1,0},
                {0,1,0,0},
        };

        sp.AddMask(4, 15, mask3);

        int[][] mask4 = new int[][]{
                {0,0,1},
                {1,1,0},
                {1,1,0},
                {1,1,0},
                {0,0,1},
        };

        sp.AddMask(2, 21, mask4);

        int[][] mask5 = new int[][]{
                {1},
                {1}
        };

        sp.AddMask(1, 25, mask5);
        sp.AddMask(6, 25, mask5);

        pattern_name_map.put("Glider Gun", sp);
    }

    public void InitLoaf() {
        StartPattern sp= new StartPattern(25, 25);
        int[][] mask = new int[][]{
                {0,1,1,0},
                {1,0,0,1},
                {0,1,0,1},
                {0,0,1,0}
        };

        sp.AddMask(3,3, mask);

        pattern_name_map.put("Loaf", sp);
    }

    public void InitPentadecathlon() {
        StartPattern sp= new StartPattern(30, 30);
        int[][] mask = new int[][]{
                {0,0,1,0,0,0,0,1,0,0},
                {1,1,0,1,1,1,1,0,1,1},
                {0,0,1,0,0,0,0,1,0,0}
        };

        sp.AddMask(5,8, mask);

        pattern_name_map.put("Pentadecathlon (period 15)", sp);
    }

    public void InitLWspaceship() {
        StartPattern sp= new StartPattern(30, 30);
        int[][] mask = new int[][]{
                {0,1,1,0,0},
                {1,1,1,1,0},
                {1,1,0,1,1},
                {0,0,1,1,0}
        };

        sp.AddMask(3,3, mask);

        pattern_name_map.put("Lightweight Spaceship (LWSS)", sp);
    }

    public void InitToad() {
        StartPattern sp= new StartPattern(20, 20);
        int[][] mask = new int[][]{
                {0,1,1,1},
                {1,1,1,0},
        };

        sp.AddMask(3,3, mask);

        pattern_name_map.put("Toad (period 2)", sp);
    }
}


class CustomAdapter extends BaseAdapter {
    private MyGridData mMyGridData;
    private Context mContext;

    int mX;
    int mY;

    public CustomAdapter(Context context, int sx, int sy) {
        mContext = context;
        System.out.println("adigupta: Running CustomAdapter");

        mX=sx;
        mY=sy;
    }

    public void Init(MyGridData myGridData) {
        mMyGridData = myGridData;
    }

    @Override
    public int getCount() {
        return mX*mY; //returns total of items in the list
    }

    public void Init(StartPattern patt) {
        mMyGridData = new MyGridData(this);
        mMyGridData.Init(patt);
        notifyDataSetChanged();
    }

    public void ReloadNext() {
        mMyGridData.NextIteration();
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    void EditAtPosition(int position) {
        mMyGridData.SetAtPosition(position, 1);
        notifyDataSetChanged();

    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(parent.getWidth()/mY, parent.getWidth()/mY));
            //textView.setPadding(1, 1, 1, 1);
        } else {
            textView = (TextView) convertView;
        }


        Log.v("getView2 function", "pos: " + mMyGridData.GetAtPosition(position));

        textView.setBackgroundColor(kBkGroundIds[(mMyGridData.GetAtPosition(position)+3) % 3]);
        if(mMyGridData.GetAtPosition(position) >=9 ) {
            textView.setText("A");
            textView.setAlpha(0.3f);
        }
        else {
            textView.setText("");
            textView.setAlpha(1.0f);
        }

        return textView;
        //textView.setBackgroundColor(kBkGroundIds[mMyGridData.GetAtPosition(position) % 3]);
        //textView.setBackgroundResource(mContext.getResources().getColor(kBkGroundIds[mMyGridData.GetAtPosition(position) % 3]));

    }

    // references to our images
    private Integer[] kThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };

    private Integer[] kBkGroundIds = {
            Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK, Color.GRAY,
            Color.MAGENTA, Color.TRANSPARENT
    };
}

class MyGridData {
    CustomAdapter mCustomAdapter;

    int[][] mGrid;
    int mX;
    int mY;

    MyGridData(CustomAdapter adaptor) {
        mCustomAdapter = adaptor;
        mGrid = new int[adaptor.mX][adaptor.mY];
    }

    public void Init(StartPattern patt) {
        mGrid= new int[patt.gridX][patt.gridY];
        mX = patt.gridX;
        mY = patt.gridY;
        ZeroGrid();
        for(int i=0; i<patt.mask_list.size(); i++) {
           AddMask(patt.mask_list.get(i).offX, patt.mask_list.get(i).offY, patt.mask_list.get(i).mask);
        }
        Sink();
    }

    public void ZeroGrid() {

        for(int i=0; i<mX; i++) {
            for(int j=0; j<mY; j++) {
                mGrid[i][j]=0;
            }
        }
    }

    public void AddMask(int offX, int offY,int[][] mask) {
        for(int i=0; i<mask.length && i+offX<mX; i++) {
            for(int j=0; j<mask[i].length && j+offY<mY; j++) {
                mGrid[i+offX][j+offY]=mask[i][j];
            }
        }
    }

    public void CheckAndAdd(Queue<Pair<Integer, Integer>> q, int[][] grid, int i, int j) {
        if(i>=0 && i<mX && j>=0 && j<mY && grid[i][j] > 0) {
            q.add(new Pair<Integer, Integer>(i, j));
            grid[i][j]=-1 * grid[i][j];
        }
    }

    /* 3,2
    {0,1,0},
    {1,0,1},
    {0,0,0},
    {1,1,1}
     */

    public void Sink() {
        Log.v("Sinking", "A");


        /*int[][] connected_blocks = mGrid;


        Map<Integer, ArrayList<Pair<Integer, Integer>>> comp_map =
                new HashMap<Integer, ArrayList<Pair<Integer, Integer>>>();

        int comp_num=-1;
        for(int i=0; i<mX; i++) {
            for(int j=0; j<mY; j++) {
                if(mGrid[i][j] > 0) {
                    Queue<Pair<Integer, Integer>> q = new LinkedBlockingQueue<Pair<Integer, Integer>>();
                    CheckAndAdd(q, mGrid, i, j);

                    if(!q.isEmpty()) {
                        comp_num++;
                    }
                    while(!q.isEmpty()) {
                        Pair<Integer, Integer> t=q.remove();
                        int t1=t.first; int t2=t.second;

                        ArrayList<Pair<Integer, Integer>> val_list = comp_map.getOrDefault(comp_num,
                                new ArrayList<Pair<Integer, Integer>>());

                        val_list.add(new Pair<Integer, Integer>(t1, t2));

                        comp_map.put(comp_num, val_list);

                        CheckAndAdd(q, mGrid, t1+1, t2);
                        CheckAndAdd(q, mGrid, t1-1, t2);

                        CheckAndAdd(q, mGrid, t1, t2+1);
                        CheckAndAdd(q, mGrid, t1, t2-1);

                        CheckAndAdd(q, mGrid, t1+1, t2+1);
                        CheckAndAdd(q, mGrid, t1-1, t2+1);

                        CheckAndAdd(q, mGrid, t1+1, t2-1);
                        CheckAndAdd(q, mGrid, t1-1, t2-1);
                    }
                }
                Log.v("Sinking", "comp_num: i_j k" + i + "_" + j + " " + comp_num);
            }
        }

        for(int i=0; i<mX; i++) {
            for(int j=0; j<mY; j++) {
                mGrid[i][j]=-1 * mGrid[i][j];
            }
        }

        for(int i=0; i<comp_map.size(); i++) {
            int minX=9999;
            int maxX=-9999;
            int minY=9999;
            int maxY=-9999;
            int num_filled=0;
            for(int j=0; j<comp_map.get(i).size(); j++) {
                minX=java.lang.StrictMath.min(minX, comp_map.get(i).get(j).first);
                maxX=java.lang.StrictMath.max(maxX, comp_map.get(i).get(j).first);
                minY=java.lang.StrictMath.min(minY, comp_map.get(i).get(j).second);
                maxY=java.lang.StrictMath.max(maxY, comp_map.get(i).get(j).second);
                num_filled++;
            }
            if(num_filled>0) {
                if((maxX-minX)*(maxY-minY)* 0.80 < num_filled*1.0) {
                    for(int j=0; j<comp_map.get(i).size(); j++) {
                        mGrid[comp_map.get(i).get(j).first][comp_map.get(i).get(j).second] = 9 +
                                mGrid[comp_map.get(i).get(j).first][comp_map.get(i).get(j).second];
                    }
                }
            }
        }
        */
    }



    public int GetValueAtPos(int i, int j) {
        if((i>=0 && i<mX && j>=0 && j<mY) && mGrid[(i+mX)%mX][(j+mY)%mY]>0) {
            return 1;
        }
        return 0;
    }

    public boolean NextIteration() {
        boolean isDiff = false;
        int[][] mGrid2 = new int[mX][mY];
        for(int i=0; i<mX; i++) {
            for(int j=0; j<mY; j++) {
                int sumNeighbours = GetValueAtPos(i+1,j) + GetValueAtPos(i,j+1) +
                        GetValueAtPos(i-1,j) + GetValueAtPos(i,j-1) +
                        GetValueAtPos(i+1,j+1) + GetValueAtPos(i+1,j-1) + GetValueAtPos(i-1,j+1) +
                        GetValueAtPos(i-1,j-1);

                if(mGrid[i][j] >= 1) {
                    mGrid2[i][j] = ((sumNeighbours==2) || (sumNeighbours==3)) ? 1 : 0;
                } else {
                    mGrid2[i][j] = (sumNeighbours==3) ? 1 : 0;
                }

                if(mGrid[i][j]!=mGrid2[i][j]) {
                    isDiff = true;
                }
            }
        }

        if(isDiff) {
            int[][] temp = mGrid;
            mGrid = mGrid2;
            mGrid2 = mGrid;
        }

        Sink();

        return isDiff;
    }

    void SetAtPosition(int pos, int val) {
        mGrid[pos/mY][pos%mY]=val;
    }

    int GetAtPosition(int pos) {
        //return 0;
        try {
            return mGrid[pos/mY][pos%mY];
        }
        catch(Exception e) {
            Log.v("GetAtPosition exception pos mX", "" + pos + " " + mX);
            return -1;
        }
    }
}

/*class ImageAdapter extends BaseAdapter {
    private MyGridData mMyGridData;
    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
        System.out.println("adigupta: Running ImageAdapter");
        mMyGridData = new MyGridData(this, 10, 10);
        mMyGridData.Init();
        *//*new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isDiff = true;
                int max_iter=5;

                ImageAdapter.this.notifyDataSetChanged();

                while (isDiff && max_iter>0) {
                    isDiff = mMyGridData.NextIteration();
                    System.out.println("Iter " + max_iter);
                    max_iter--;
                    //ImageAdapter.this.notifyDataSetChanged();
                }
            }}).start();*//*
    }

    public void ReloadNext() {
        mMyGridData.NextIteration();
        notifyDataSetChanged();
    }

    public int getCount() {
        return kThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        System.out.println("pos: " + position);
        //imageView.setImageDrawable(mContext.getDrawable(0));
        imageView.setImageDrawable(mContext.getDrawable(kThumbIds[mMyGridData.GetAtPosition(position)]));

        return imageView;
    }

    // references to our images
    private Integer[] kThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };
}*/
