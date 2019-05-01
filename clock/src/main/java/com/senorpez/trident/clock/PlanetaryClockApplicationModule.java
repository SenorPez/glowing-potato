package com.senorpez.trident.clock;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
abstract class PlanetaryClockApplicationModule {
    @ContributesAndroidInjector
    abstract PlanetaryClockActivity contributeActivityInjector();

    @Provides
    static TridentAPI providesTridentAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://trident.senorpez.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(TridentAPI.class);
    }
}
