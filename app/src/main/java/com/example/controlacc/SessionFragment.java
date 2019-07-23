package com.example.controlacc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.net.URISyntaxException;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Intent.getIntent;


public class SessionFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {

    private RequestQueue rq;
    private JsonRequest jrq;

    private TextView schoolName;
    private EditText boxUser,boxPass;
    private Button btnLogin,btnEscuela;
    private User user;
    private Escuela escuela;
    private Intent in;
    private String ipServidorEscuela;
    private JSONObject jsonObject;
    public SharedPreferences sp;
    public static final String NAMESTUDENT="nameStudent";
    public static final String USER="user";
    public static final String PASSWORD="password";
    private static final String TAG=MainActivity.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        sp=this.getActivity().getSharedPreferences(ConfigSchoolActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        Log.e(TAG,""+sp.getAll());

        user=new User();
        escuela=new Escuela();

        View view=inflater.inflate(R.layout.fragment_session,container,false);

        boxUser=(TextInputEditText)view.findViewById(R.id.user);
        boxPass=(TextInputEditText)view.findViewById(R.id.password);
        btnLogin=(Button)view.findViewById(R.id.btnLogin);
        btnEscuela=(Button)view.findViewById(R.id.btnEscuela);
        schoolName=(TextView)view.findViewById(R.id.schoolName);


        rq= Volley.newRequestQueue(getContext());


        in=getActivity().getIntent();

        final String getSchoolName="Escuela "+in.getExtras().getString("nombre");
        ipServidorEscuela=in.getExtras().getString("ipServidorEscuela");



        schoolName.setText(getSchoolName);

        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                loginSession();
            }
        });
        btnEscuela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog dialogSchool=new SweetAlertDialog(getContext(),SweetAlertDialog.NORMAL_TYPE);
                dialogSchool.setTitle("Informacion de la escuela");
                dialogSchool.setContentText("Nombre: "+getSchoolName);

                if(in.getExtras().getInt("nivelEducativo")==1){
                    dialogSchool.setContentText("Nivel: Primaria");
                }else if(in.getExtras().getInt("nivelEducativo")==2){
                    dialogSchool.setContentText("Nivel: Secundaria");
                }else if(in.getExtras().getInt("nivelEducativo")==3){
                    dialogSchool.setContentText("Nivel: Bachillerato");
                }

                dialogSchool.show();
            }
        });


        return view;

    }



    @Override
    public void onErrorResponse(VolleyError error) {

        final SweetAlertDialog dialogSuccess=new SweetAlertDialog(getContext(),SweetAlertDialog.ERROR_TYPE);
        new CountDownTimer(3000,1){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                dialogSuccess.setTitleText("Usuario no encontrado")
                        .setContentText("Presiona el boton para continuar")
                        .show();
                Log.e(TAG,"Usuario No encontrado");
            }
        }.start();



        Log.e(TAG,"Error "+error.getStackTrace());
        Log.e(TAG, String.valueOf(error.networkResponse));
        Log.e(TAG, String.valueOf(error.getMessage()));

    }

    @Override
    public void onResponse(final JSONObject response) {

        Log.e(TAG,"************************************************");
        Log.e(TAG,"********* RESPUESTA DEL SERVICIO WEB ***********");
        Log.e(TAG,"************************************************");

        final JSONArray jsonarray=response.optJSONArray("datos");

        SharedPreferences.Editor editor=sp.edit();
        jsonObject=null;


        try {

            jsonObject=jsonarray.getJSONObject(0);
            user.setUserApp(jsonObject.getString("nombre"));

            editor.putString(USER,jsonObject.optString("usuarioApp"));
            editor.putString(PASSWORD,jsonObject.optString("passwordApp"));
            editor.putString(NAMESTUDENT,jsonObject.optString("nombre"));
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final SweetAlertDialog dialogSuccess=new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE);

        final Intent mainIntent=new Intent(getContext(),MainActivity.class);

        new CountDownTimer(3000,1){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                startActivity(mainIntent);
            }
        }.start();


    }




    private void loginSession(){

        int i=-1;

        final SweetAlertDialog dialogLoading=new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);

        try{


            new CountDownTimer(3000,1){

                @Override
                public void onTick(long millisUntilFinished) {

                    dialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    dialogLoading.setTitleText("Buscando");
                    dialogLoading.setCancelable(true);
                    dialogLoading.show();

                }

                @Override
                public void onFinish() {
                    dialogLoading.cancel();
                }
            }.start();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        String url="http://"+ipServidorEscuela+"/developer/controlAccesoRepo/servers/WebService_app/session.php?user="+boxUser.getText().toString()+"&passwd="+boxPass.getText().toString();

        jrq=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        rq.add(jrq);


    }
}
