package com.example.jmzamora_videoteca;


import android.app.Dialog;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.type;
import static com.example.jmzamora_videoteca.R.layout.dialog;

public class FragmentPrincipal extends BrowseFragment {
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final String PREF_NAME = "Proteccion";
    private SharedPreferences sharedPreferences;
    private final int IS_OK_PWD = 101;
    private final int SAVE_PWD = 102;
    private Boolean isPwdCorrect;
//    final FragmentPrincipal context = this;

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
    }

    private void createMyDialog(int Type) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog);
        Button btnGuardar = (Button) dialog.findViewById(R.id.save_password);
        Button btnAceptar = (Button) dialog.findViewById(R.id.get_password);
        final EditText edtPassword = (EditText) dialog.findViewById(R.id.contrasena_input);

        if (Type == SAVE_PWD){
            btnGuardar.setVisibility(View.VISIBLE);
            btnAceptar.setVisibility(View.GONE);}
        else{
            btnGuardar.setVisibility(View.GONE);
            btnAceptar.setVisibility(View.VISIBLE);}

        btnGuardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (edtPassword.getText().length() > 0) {
                                savePasswordInPreferences(edtPassword.getText().toString());
                                Toast.makeText(getContext(), "Password " + edtPassword.getText().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                savePasswordInPreferences(null);
                                Toast.makeText(getContext(), "SIN Password", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }
        );

        btnAceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Aceptar", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                }
        );
        /*btnAceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isPwdCorrect = isCorrectPwd((edtPassword.getText().length() > 0)?edtPassword.getText().toString():null);
                        dialog.dismiss();
                    }
                }
        );*/
        dialog.show();
    }

    private String getContentFromPref() {
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("Password", null);
    }

    private Boolean isCorrectPwd(String pwd) {
        return getContentFromPref().equalsIgnoreCase(pwd);
    }


    private void savePasswordInPreferences(String pwd) {
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Password", pwd);
        editor.commit();
        //getSharedPreferences()
    }

    private Boolean isLoggedOk() {
        Boolean lRes = true;
        if (getContentFromPref() != null) {
            createMyDialog(IS_OK_PWD);
            lRes = isPwdCorrect;
        }
        return lRes;
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                if (isLoggedOk()) {
                    Movie movie = (Movie) item;
                    Intent intent = new Intent(getActivity(), ActividadDetalles.class);
                    intent.putExtra(ActividadDetalles.MOVIE, movie);
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((ImageCardView) itemViewHolder.view).getMainImageView(), ActividadDetalles.SHARED_ELEMENT_NAME).toBundle();
                    getActivity().startActivity(intent, bundle);
                } else {
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setMessage("Contraseña Incorrecta")
                            .setTitle("¡¡ ATENCINÓN !!");
                    alertBuilder.setPositiveButton("Acptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlgInterface, int id) {
                            //   this.
                            //     alertBuilder.finalize();
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                }
            } else if (item instanceof String) {
                if (((String) item).equalsIgnoreCase("Protección")) {
                    //Toast.makeText(getActivity(), "BINGO Pide Password", Toast.LENGTH_SHORT).show();
                    createMyDialog(SAVE_PWD);
                } else
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
            }
        }
    }
}