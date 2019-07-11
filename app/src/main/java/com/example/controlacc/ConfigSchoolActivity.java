package com.example.controlacc;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import android.support.design.button.MaterialButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.controlacc.model.Escuela;
import com.example.controlacc.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfigSchoolActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    public RequestQueue rq;
    public JsonRequest jrq;
    public TextView textIpSchool;
    public Button btnCheckIp;

    public static final String MyPREFERENCES="user";
    public static final String SERVER="nameServer";
    public static final String SCHOOL="nameSchool";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_school);


        Explode explode=new Explode();
        explode.setDuration(2000);
        getWindow().setExitTransition(explode);


        textIpSchool = (TextInputEditText) findViewById(R.id.textIpSchool);
        btnCheckIp = (Button) findViewById(R.id.btnCheckIp);
        rq= Volley.newRequestQueue(this);

        sp=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnCheckIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ipSchool = textIpSchool.getText().toString();
                System.out.println("*************************** >> "+ipSchool);
                checkSchool(ipSchool);


            }
        });

    }

    @Override
    public void onErrorResponse(VolleyError error) {

        final SweetAlertDialog dialogSuccess=new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        new CountDownTimer(3000,1){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                dialogSuccess.setTitleText("La Institución no esta registrada")
                        .setContentText("Presiona el boton para continuar")
                        .show();
            }
        }.start();

    }

    @Override
    public void onResponse(JSONObject response) {
        User user=new User();
        Escuela escuela=new Escuela();

        final JSONArray jsonarray=response.optJSONArray("datos");
        JSONObject jsonObject=null;


        SharedPreferences.Editor editor=sp.edit();



        try {
            jsonObject=jsonarray.getJSONObject(0);

            System.out.println("************* "+jsonObject.optString("nombre"));
            System.out.println("************* "+jsonObject.optString("ipServidorEscuela"));

            editor.putString(SERVER,jsonObject.optString("ipServidorEscuela"));
            editor.putString(SCHOOL,jsonObject.optString("nombre"));
            editor.commit();
            /*user.setNombreEscuela(jsonObject.optString("nombre"));
            user.setIpServidorEscuela(jsonObject.optString("ipServidorEscuela"));*/

            escuela.setNombre(jsonObject.optString("nombre"));
            escuela.setIpServidorEscuela(jsonObject.optString("ipServidorEscuela"));
            escuela.setNivelEducativo(jsonObject.optInt("idNivelEducativo"));


        } catch (Exception e) {
            e.printStackTrace();
        }

        final SweetAlertDialog dialogSuccess=new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE);


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
