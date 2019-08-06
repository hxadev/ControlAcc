package com.example.controlacc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.controlacc.model.Escuela;
import org.json.JSONArray;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Clase que verifica y controla el primer acceso a la aplicacion, por medio de
 * la dirección o dominio de la institución educativa.
 * @author Alfonso Hernandez Xochipa
 */
public class ConfigSchoolActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    public RequestQueue rq;
    public JsonRequest jrq;
    public TextView textIpSchool;
    public Button btnCheckIp;

    public static final String MyPREFERENCES="user";
    public static final String SERVER="nameServer";
    public static final String SCHOOL="nameSchool";
    private static final String TAG=MainActivity.class.getSimpleName();
    SharedPreferences sp;

    /**
     * Metodo que inicia el activitie de la clase
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_school);

        /**
         * Constuye Animaciones al pasar de activitie
         */
        Explode explode=new Explode();
        explode.setDuration(2000);
        getWindow().setExitTransition(explode);

        /**
         * Almacenamiento de Objetos de la interfaz gráfica
         */
        textIpSchool = (TextInputEditText) findViewById(R.id.textIpSchool);
        btnCheckIp = (Button) findViewById(R.id.btnCheckIp);
        rq= Volley.newRequestQueue(this);

        /**
         * Construimos el Objeto Shared Preferences para persistir los datos de la aplicacion
         */
        sp=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnCheckIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipSchool = textIpSchool.getText().toString();
                Log.i(TAG,"DOMINIO O IP DE LA ESCUELA >>> "+ipSchool);
                checkSchool(ipSchool);
            }
        });

    }

    /**
     * Cuando se REsponde con un error se activa este método
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {

        final SweetAlertDialog dialogSuccess=new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        new CountDownTimer(3000,1){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            /**
             * Metodo que se activa cuando la institución no esta activa dentro del servicio de la aplicacion.
             */
            @Override
            public void onFinish() {

                dialogSuccess.setTitleText("La Institución no esta registrada")
                        .setContentText("Presiona el boton para continuar")
                        .show();

            }
        }.start();

    }

    /**
     * Cuando se encuentra una respuesta se activa  este metodo
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {

        Escuela escuela=new Escuela();

        final JSONArray jsonarray=response.optJSONArray("datos");
        JSONObject jsonObject=null;
        Log.d(TAG,"Contenido del Array >> "+jsonarray.toString());

        /**
         * Creamos una variable la cual permitira editar los datos del SharedPreferences
         */
        SharedPreferences.Editor editor=sp.edit();

        try {
            jsonObject=jsonarray.getJSONObject(0);

            Log.i(TAG,"************* "+jsonObject.optString("nombre"));
            Log.i(TAG,"************* "+jsonObject.optString("ipServidorEscuela"));

            editor.putString(SERVER,jsonObject.optString("ipServidorEscuela"));
            editor.putString(SCHOOL,jsonObject.optString("nombre"));
            editor.commit();

            escuela.setNombre(jsonObject.optString("nombre"));
            escuela.setIpServidorEscuela(jsonObject.optString("ipServidorEscuela"));
            escuela.setNivelEducativo(jsonObject.optInt("idNivelEducativo"));

        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * Enviamos los datos
         * Nombre de la escuela
         * Nivel educativo de la escuela
         * Ip o dominio de la escuela
         * Al siguiente activity
         */
        final Intent newAct;
        newAct = new Intent(this, LoginActivity.class);
        newAct.putExtra("nombre",escuela.getNombre());
        newAct.putExtra("nivelEducativo",escuela.getNivelEducativo());
        newAct.putExtra("ipServidorEscuela",escuela.getIpServidorEscuela());

        new CountDownTimer(2000,1){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(newAct);
            }
        }.start();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Función que permite verificar si la institución esta registrada o tiene
     * contratado el servicio
     * @param ipSchool
     */
    private void checkSchool(final String ipSchool) {
        final SweetAlertDialog dialogLoading = new SweetAlertDialog(ConfigSchoolActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        try {
            new CountDownTimer(3000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {

                    dialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    dialogLoading.setTitleText("Buscando");
                    dialogLoading.setContentText("Escuela "+ipSchool);
                    dialogLoading.setCancelable(true);
                    dialogLoading.show();

                }

                @Override
                public void onFinish() {
                    dialogLoading.cancel();
                }
            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();

        }

        String url="http://"+ipSchool+"/developer/controlAccesoRepo/servers/WebService_app/session.php?school="+ipSchool;

        jrq=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        rq.add(jrq);

    }

}
