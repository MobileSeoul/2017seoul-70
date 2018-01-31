package com.coddect.whyareyouhere.animation;

import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HeightAnimation extends Animation
{
    View view;

    int startHeight;
    final int targetHeight;

    public HeightAnimation(View view, int heightFrom, int heightTo)
    {
        this.view = view;

        this.startHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightFrom, view.getContext().getResources().getDisplayMetrics());
        this.targetHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightTo, view.getContext().getResources().getDisplayMetrics());
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        view.getLayoutParams().height = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
        view.requestLayout();
    }
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }
    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}