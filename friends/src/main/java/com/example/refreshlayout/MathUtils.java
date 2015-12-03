package com.example.refreshlayout;

/**
 * Created by Administrator on 2015/11/16.
 */
public class MathUtils {

    // can run absurdly slow for such simple functions...
// TODO: profile, see if this just seems to be the case or is actually causing issues...
    public static final float max(float a, float b) {

        return (a > b)?a:b;

    }

    public static final float min(float a, float b) {

        return (a < b)?a:b;

    }

    /** Returns the closest value to 'a' that is in between 'low' and 'high' */
    public static final float clamp(float a, float low, float high) {

        return MathUtils.max(low, MathUtils.min(a, high));

    }


}
