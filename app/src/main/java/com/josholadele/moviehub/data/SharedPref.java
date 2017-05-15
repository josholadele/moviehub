package com.josholadele.moviehub.data;

import android.content.Context;

/**
 * Created by Oladele on 5/15/17.
 */

public class SharedPref {
    Context mContext;

    public SharedPref(Context context) {
        mContext = context;
    }

    public void setSortType(String sortType) {
        mContext.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE).edit().putString(getClass().getSimpleName() + ".sortType", sortType).commit();
    }

    public String getSortType() {
        return mContext.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE).getString(getClass().getSimpleName() + ".sortType", "popular");
    }
}
