package com.piscesstudio.databasetest.Callback;

import com.piscesstudio.databasetest.VorkObject;

import java.util.List;

public interface FetchCallback<T extends VorkObject> {
    void done(List<T> object, Exception e);
}
