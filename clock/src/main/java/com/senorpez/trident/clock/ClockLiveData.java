package com.senorpez.trident.clock;

import android.os.Handler;
import androidx.lifecycle.MutableLiveData;

import java.util.function.Supplier;

public class ClockLiveData<T> extends MutableLiveData<T> {
    private final Supplier<T> supplier;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ClockLiveData.this.setValue(supplier.get());
            handler.postDelayed(this, 500);
        }
    };

    ClockLiveData(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected void onActive() {
        super.onActive();
        handler.post(runnable);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        handler.removeCallbacks(runnable);
    }
}
