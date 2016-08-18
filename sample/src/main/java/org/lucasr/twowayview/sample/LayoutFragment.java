/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lucasr.twowayview.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemClickSupport.OnItemClickListener;
import org.lucasr.twowayview.ItemClickSupport.OnItemLongClickListener;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

public class LayoutFragment extends Fragment {


    private Point mStartPoint = new Point(0,0);
    private Point mControlPoint;
    private Point mEndPoint;


    private static final String ARG_LAYOUT_ID = "layout_id";

    private TwoWayView mRecyclerView;
    private TextView mPositionText;
    private TextView mCountText;
    private TextView mStateText;
    private Toast mToast;

    private int mLayoutId;
    private int mWidth;
    private int childHalfPx;

    public static LayoutFragment newInstance(int layoutId) {
        LayoutFragment fragment = new LayoutFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);

        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = getArguments().getInt(ARG_LAYOUT_ID);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        int height = size.y;

        mEndPoint = new Point(mWidth, 0);
        mControlPoint = new Point(mWidth/2, 300);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(mLayoutId, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        childHalfPx = (int) convertDpToPixel(24,activity);

        mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        mRecyclerView = (TwoWayView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);

        mPositionText = (TextView) view.getRootView().findViewById(R.id.position);
        mCountText = (TextView) view.getRootView().findViewById(R.id.count);

        mStateText = (TextView) view.getRootView().findViewById(R.id.state);
        updateState(SCROLL_STATE_IDLE);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                mToast.setText("Item clicked: " + position);
                mToast.show();
            }
        });

        itemClick.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
                mToast.setText("Item long pressed: " + position);
                mToast.show();
                return true;
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {



                //View firstChild  = recyclerView.getChildAt(mRecyclerView.getFirstVisiblePosition());


                //child.layout(0,0,500,200);


                for (int i = mRecyclerView.getFirstVisiblePosition(); i<= mRecyclerView.getLastVisiblePosition(); i++){

                    TextView view = (TextView) recyclerView.getChildAt(i - mRecyclerView.getFirstVisiblePosition());

                    int x=view.getLeft(),y;

                    y = (int) getY(mStartPoint, mControlPoint, mEndPoint, x, 0);

                    //y = (int) Math.sqrt(Math.pow(250.0,2)-Math.pow((1.0*x),2));

                    //if (x>=1400)
                    Log.d("View",((TextView)view.findViewById(R.id.title)).getText().toString()+" Left : "+x + " width "+ mWidth + " valueOf Y: "+ y+" Px Half : "+childHalfPx +" width "+view.getMeasuredWidth());

                    //view.layout(x-childHalfPx, y-childHalfPx, x + view.getMeasuredWidth()-childHalfPx, y + view.getMeasuredWidth()-childHalfPx);
                    view.layout(x-15, y, x + view.getMeasuredWidth()-15, y + view.getMeasuredHeight());
                    //y += 50;
                }

                mPositionText.setText("First: " + mRecyclerView.getFirstVisiblePosition()+" Last "+mRecyclerView.getLastVisiblePosition());
                mCountText.setText("Count: " + mRecyclerView.getChildCount());
            }
        });

        //final Drawable divider = getResources().getDrawable(R.drawable.divider);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        mRecyclerView.setAdapter(new LayoutAdapter(activity, mRecyclerView, mLayoutId));
    }



    double getY(Point startPoint, Point controlPoint, Point endPoint, double t, int extraSpace){

        double result;
        t = t / (double) (mWidth - extraSpace);
        result = Math.pow(1.0 - t, 2.0) * startPoint.y + 2.0 * (1.0-t)* t * controlPoint.y  + Math.pow(t, 2.0) * endPoint.y;
        return result;
    }



    private void updateState(int scrollState) {
        String stateName = "Undefined";
        switch(scrollState) {
            case SCROLL_STATE_IDLE:
                stateName = "Idle";
                break;

            case SCROLL_STATE_DRAGGING:
                stateName = "Dragging";



                break;

            case SCROLL_STATE_SETTLING:
                stateName = "Flinging";
                break;
        }

        mStateText.setText(stateName);
    }

    public int getLayoutId() {
        return getArguments().getInt(ARG_LAYOUT_ID);
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
