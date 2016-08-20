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

package org.lucasr.twowayview.widget;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.lucasr.twowayview.widget.Lanes.LaneInfo;
import org.w3c.dom.Text;

public class ListLayoutManager extends BaseLayoutManager {

    int x = 0;
    int y = 0;

    private static final String LOGTAG = "ListLayoutManager";
    private Point mEndPoint;
    private Point mControlPoint;
    private Point mStartPoint = new Point(0,0);
    private int mWidth;
    private int childHalfPx;

    private Context mContext;

    public ListLayoutManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListLayoutManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public ListLayoutManager(Context context, Orientation orientation) {
        super(orientation);
        mContext = context;
    }

    @Override
    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);



    }

    private void init(Context context) {
        mWidth=getWidth();
        mEndPoint = new Point(mWidth, 0);
        mControlPoint = new Point(mWidth/2, 300);
        childHalfPx = (int) convertDpToPixel(48,context);
    }


    @Override
    int getLaneCount() {
        return 1;
    }

    @Override
    void getLaneForPosition(LaneInfo outInfo, int position, Direction direction) {
        outInfo.set(0, 0);
    }

    @Override
    void moveLayoutToPosition(int position, int offset, Recycler recycler, State state) {
        getLanes().reset(offset);
    }


    @Override
    public void onLayoutChildren(Recycler recycler, State state) {
        super.onLayoutChildren(recycler, state);


        Log.d("TAGS", "child call width: ");
    }

    @Override
    protected void layoutChild(View child, Direction direction) {


       // super.layoutChild(child, direction);

        init(mContext);


        FrameLayout itemView = (FrameLayout) child;
        TextView childItem = (TextView) itemView.getChildAt(0);

        Log.d("TAG_FRAM", "Item "+ childItem.getText() +" left2: " + mChildFrame.left + " top: " + mChildFrame.top + " right: " + mChildFrame.right + " bottom: " + mChildFrame.bottom);

        TwoWayView.LayoutParams params = (TwoWayView.LayoutParams) child.getLayoutParams();

        Log.d("Tag","Left M: "+ params.leftMargin);


        getLaneForChild(mTempLaneInfo, child, direction);

        mLanes.getChildFrame(mChildFrame, getDecoratedMeasuredWidth(child),
                getDecoratedMeasuredHeight(child), mTempLaneInfo, direction);
        final ItemEntry entry = cacheChildFrame(child, mChildFrame);
        //layoutDecorated(child, x, y, x + getDecoratedMeasuredWidth(child), y + getDecoratedMeasuredWidth(child));


        int x = mChildFrame.left  ,y;

        y = (int) getY(mStartPoint, mControlPoint, mEndPoint, mChildFrame.left, 0);

        //y = y-params.topMargin;

        Log.d("TAG_FRAM", "Item "+ childItem.getText() +" left: " + mChildFrame.left + " top: " + mChildFrame.top + " right: " + mChildFrame.right + " bottom: " + mChildFrame.bottom);

        /*layoutDecorated(child,
                x - (mChildFrame.right - mChildFrame.left)/2,
                y - (mChildFrame.right - mChildFrame.left)/2,
                x + (mChildFrame.right - mChildFrame.left)/2,
                y + (mChildFrame.right - mChildFrame.left)/2);*/

        layoutDecorated(child, mChildFrame.left, y - childItem.getMeasuredWidth()/2, mChildFrame.right,
                mChildFrame.bottom - childItem.getMeasuredWidth()/2);

        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();

        if (!lp.isItemRemoved()) {
            pushChildFrame(entry, mChildFrame, mTempLaneInfo.startLane,
                    getLaneSpanForChild(child), direction);
        }

        //layoutDecorated(child, x-getDecoratedMeasuredWidth(child)/2, y-childItem.getMeasuredHeight()/2, x+ getDecoratedMeasuredWidth(child)/2, y+ childItem.getMeasuredHeight()/2);

        Log.d("TAGS", "child call x: "+x+" y: "+y+" width: "+getWidth());
    }



    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }


    double getY(Point startPoint, Point controlPoint, Point endPoint, double t, int extraSpace){

        double result;

        t = t / (double) (mWidth);

        result = Math.pow(1.0 - t, 2.0) * startPoint.y + 2.0 * (1.0-t)* t * controlPoint.y  + Math.pow(t, 2.0) * endPoint.y;
        return result;
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
