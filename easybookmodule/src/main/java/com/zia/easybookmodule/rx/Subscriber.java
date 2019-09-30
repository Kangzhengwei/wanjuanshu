package com.zia.easybookmodule.rx;


import androidx.annotation.NonNull;

public interface Subscriber<T> {
    void onFinish(@NonNull T t);

    void onError(@NonNull Exception e);

    void onMessage(@NonNull String message);

    void onProgress(int progress);
}