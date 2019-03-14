package com.senorpez.trident.clock;

import android.os.Handler;
import androidx.lifecycle.MutableLiveData;

import java.util.function.Supplier;

class ClockLiveData<T> extends MutableLiveData<T> {
    private final Supplier<T> supplier;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
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
