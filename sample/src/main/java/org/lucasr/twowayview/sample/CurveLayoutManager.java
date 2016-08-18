package org.lucasr.twowayview.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.lucasr.twowayview.TwoWayLayoutManager;

/**
 * Created by androidcodehunter on 8/18/16.
 */

public class CurveLayoutManager extends TwoWayLayoutManager {

    public CurveLayoutManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurveLayoutManager(Orientation orientation) {
        super(orientation);
    }

    public CurveLayoutManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void measureChild(View child, Direction direction) {

    }

    @Override
    protected void layoutChild(View child, Direction direction) {

    }

    @Override
    protected boolean canAddMoreViews(Direction direction, int limit) {
        return false;
    }
}
