package com.example.jmzamora_videoteca;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by padres on 05/07/2017.
 */

public class ActividadDetalles extends Activity {
    public static final String MOVIE = "Movie";
    public static final String SHARED_ELEMENT_NAME = "hero";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
    }
}
