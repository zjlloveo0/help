package com.netease.nim.uikit.common.adapter;

public interface TAdapterDelegate {

    int getViewTypeCount();

    Class<? extends TViewHolder> viewHolderAtPosition(int position);

    boolean enabled(int position);
}