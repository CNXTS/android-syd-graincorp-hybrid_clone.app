package com.webling.graincorp.ui.decorators;

public abstract class ListItem {
    public static final int TITLE_ITEM = 0;
    public static final int ITEM = 1;

    abstract public int getItemType();
}
