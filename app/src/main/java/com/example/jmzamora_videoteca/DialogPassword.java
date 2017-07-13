package com.example.jmzamora_videoteca;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.R.attr.action;
import static com.example.jmzamora_videoteca.R.layout.dialog;

/**
 * Created by padres on 13/07/2017.
 */

public class DialogPassword extends Activity {
    private final int IS_OK_PWD = 101;
    private final int SAVE_PWD = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        int action = getIntent().getExtras().getInt("action");
        Button btnGuardar = (Button) findViewById(R.id.save_password);
        final EditText edtPassword = (EditText) findViewById(R.id.contrasena_input);
        switch (action)
        {
            case IS_OK_PWD:
                btnGuardar.setText("Aceptar");
                break;
            case SAVE_PWD:
                btnGuardar.setText("Guardar");
                break;
        }
        btnGuardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // if (edtPassword.getText().length() > 0) {
                            Intent data = new Intent();
                     //       Uri kk = Uri.parse(null);
                            data.setData(Uri.parse((edtPassword.getText().length() > 0)?edtPassword.getText().toString():""));
                            setResult(RESULT_OK, data);
                            finish();
                       // }
                    }
                });

    }
}
