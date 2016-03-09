package com.piscesstudio.databasetest.Callback;

import com.piscesstudio.databasetest.VorkObject;

public interface GetCallback<T extends VorkObject> {
    void done(T object, Exception e);
}
