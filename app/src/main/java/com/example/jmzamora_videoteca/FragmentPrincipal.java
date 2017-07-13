package com.example.jmzamora_videoteca;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentPrincipal extends BrowseFragment {
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    /* Necesario para solocotar Password*/
    private static final String PREF_NAME = "Proteccion";
    private SharedPreferences sharedPreferences;
    private int action = -1;
    // distingo entre guardar
    private final int IS_OK_PWD = 101;
    private final int SAVE_PWD = 102;
    private final int request_code = 1000;
    private String Password;
    /* Necesario para solocotar Password*/

    private Movie pelicula;
    private Presenter.ViewHolder myItemViewHolder;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        leerDatos();
        iniciarInterfazUsuario();
        cargarListas();
        setupEventListeners();
    }

    private void leerDatos() {
        MovieList.list = new ArrayList<com.example.jmzamora_videoteca.Movie>();
        String json = Utils.loadJSONFromResource(getActivity(), R.raw.movies);
        Gson gson = new Gson();
        Type collection = new TypeToken<ArrayList<Movie>>() {
        }.getType();
        MovieList.list = gson.fromJson(json, collection);
    }

    private void iniciarInterfazUsuario() {
        setTitle("Videoteca");
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources()
                .getColor(R.color.fastlane_background));
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void cargarListas() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();
        List<String> categories = getCategories();
        if (categories == null || categories.isEmpty()) return;
        for (String category : categories) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (Movie movie : MovieList.list) {
                if (category.equalsIgnoreCase(movie.getCategory())) listRowAdapter.add(movie);
            }
            if (listRowAdapter.size() > 0) {
                HeaderItem header = new HeaderItem(rowsAdapter.size() - 1, category);
                rowsAdapter.add(new ListRow(header, listRowAdapter));
            }
        }
        listaPreferencias(rowsAdapter);
        setAdapter(rowsAdapter);
    }

    private List<String> getCategories() {
        if (MovieList.list == null)
            return null;
        List<String> categories = new ArrayList<String>();
        for (Movie movie : MovieList.list) {
            if (!categories.contains(movie.getCategory())) {
                categories.add(movie.getCategory());
            }
        }
        return categories;
    }

    private void listaPreferencias(ArrayObjectAdapter adapter) {
        HeaderItem gridHeader = new HeaderItem(adapter.size() - 1, "PREFERENCIAS");
        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add("Vistas");
        gridRowAdapter.add("Errores");
        gridRowAdapter.add("Preferencias");
        gridRowAdapter.add("Protección");
        adapter.add(new ListRow(gridHeader, gridRowAdapter));
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implementar la busqueda aquí", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getContentFromPref() {
        String sRes;
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sRes = sharedPreferences.getString("Password", null);
        Log.wtf("** VIDEOTECA **", "He obtenido de SharedPreferences ... " + sRes);
        return sRes;
    }

    private Boolean isCorrectPwd(String pwd) {
        String savePwd = getContentFromPref();
        Boolean lRes = savePwd.equalsIgnoreCase(pwd);
        return lRes;
        //return getContentFromPref().equalsIgnoreCase(pwd);
    }


    private void savePasswordInPreferences(String pwd) {
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Password", pwd);
        editor.commit();
    }


    private void callDialog(int action) {
        Log.wtf("** VIDEOTECA **", "callDialog - LLamoa a diálogo para pedir Password con action = " + action);
        this.action = action;
        Intent i = new Intent(getContext(), DialogPassword.class);
        i.putExtra("action", action);
        startActivityForResult(i, request_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.wtf("** VIDEOTECA **", "onActivityResult - vuelvo del diálogo");
        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            Password = data.getDataString();
            Log.wtf("** VIDEOTECA **", "onActivityResult - Correcto, y he obtenido " + Password);
        }

        Log.wtf("** VIDEOTECA **", "onActivityResult - Passwor con contenido " + Password);
        switch (this.action) {
            case IS_OK_PWD:
                if (!Password.isEmpty()) {
                    if (isCorrectPwd(Password)) {
                        Log.wtf("** VIDEOTECA **", "onActivityResult - Passwor Correcta, muestro peli");
                        openFilm();
                    } else {
                        Toast.makeText(getContext(), "Contraseña Incorrecta", Toast.LENGTH_LONG).show();
                        Log.wtf("** VIDEOTECA **", "onActivityResult - Passwor INCorrecta, muestro ERROR");
                    }
                } else {
                    Toast.makeText(getContext(), "No escribó contraseña", Toast.LENGTH_LONG).show();
                }
                break;
            case SAVE_PWD:
                Log.wtf("** VIDEOTECA **", "onActivityResult - Guardo Password");
                savePasswordInPreferences(Password);
                break;
        }
    }

    private void myAlertDialog(String title, String message) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage(message)
                .setTitle(title);
        alertBuilder.setPositiveButton("Acptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlgInterface, int id) {
                //   this.
                //     alertBuilder.finalize();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                pelicula = (Movie) item;
                myItemViewHolder = itemViewHolder;
                Log.wtf("** VIDEOTECA **", "itemViewClieckListener  - Me pulsan en una peli ... ");
                String contentPwd = getContentFromPref();
                if (contentPwd != null && !contentPwd.equals("")) {
                    Log.wtf("** VIDEOTECA **", "itemViewClieckListener  - Tengo password ... ");
                    callDialog(IS_OK_PWD);
                } else { //SI NO TENGO GUARDADA PASSWORD, accedo directamente
                    openFilm();
                }

            } else if (item instanceof String) {
                if (((String) item).equalsIgnoreCase("Protección")) {
                    Log.wtf("** VIDEOTECA **", "itemViewClieckListener  - Solicito me esc riban password ... ");
                    callDialog(SAVE_PWD);
                } else
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openFilm() {
        Log.wtf("** VIDEOTECA **", "OpenFilm - Muestro detalle de la película... ");
        Movie movie = pelicula;
        Intent intent = new Intent(getActivity(), ActividadDetalles.class);
        intent.putExtra(ActividadDetalles.MOVIE, movie);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((ImageCardView) myItemViewHolder.view).getMainImageView(), ActividadDetalles.SHARED_ELEMENT_NAME).toBundle();
        getActivity().startActivity(intent, bundle);
    }
}