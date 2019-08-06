package com.example.controlacc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.example.controlacc.model.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 *@author Alfonso Hernandez Xochipa
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private ArrayList<MessagesData> mMessageData=new ArrayList<>();
    private AbstractXMPPConnection mConnection;
    private static final String TAG=MainActivity.class.getSimpleName();

    public User user;

    public String usr;
    public String passwd;
    public String server;

    SharedPreferences sp;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG,"***************************************************************");
        Log.e(TAG,"**************** INICIANDO LA APLICACIÖN CLIENTE **************");
        Log.e(TAG,"***************************************************************");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView=findViewById(R.id.rv);
        mAdapter=new Adapter(mMessageData);

        LinearLayoutManager manager=new LinearLayoutManager(this);
        DividerItemDecoration decoration=new DividerItemDecoration(this,manager.getOrientation());

        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bienvenido");
        toolbar.setSubtitle("Padre de Familia");
        toolbar.setLogo(R.drawable.ic_school_black_24dp);

        sp=getSharedPreferences(ConfigSchoolActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Log.e(TAG,"Almacenado el Shared Preferences "+sp.toString());

        user=new User();
        Escuela escuela=new Escuela();

        user.setUserApp(sp.getAll().get("user").toString());
        user.setPassApp(sp.getAll().get("password").toString());
        escuela.setIpServidorEscuela(sp.getAll().get("nameServer").toString());
        escuela.setNombre(sp.getAll().get("nameSchool").toString());

        usr=user.getUserApp();
        passwd=user.getPassApp();
        String school=escuela.getNombre();
        server=escuela.getIpServidorEscuela();

        //System.out.println("user: "+usr+"\npass: "+passwd+"\nServer: "+server);

        setConnection();
        Log.i(TAG,"Conexion Establecida");
    }


    /**
     *
     */
    private void setConnection(){
        Log.i(TAG,"user: "+usr+"\npass: "+passwd+"\nServer: "+server);
        Log.i(TAG,"Iniciando Conexion...");
        new Thread(){

            @Override
            public void run() {

                InetAddress addr=null;

                try {
                    addr=InetAddress.getByName(server);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                Log.i(TAG,"**** Addr ***** " +addr);

                HostnameVerifier verifier=new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return false;
                    }
                };

               Log.i(TAG,"**** HOST VERIFIER *****");


                DomainBareJid serviceName=null;

                try {
                    serviceName= JidCreate.domainBareFrom(server);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();

                }

                Log.i(TAG,"**** SERVICE NAME ***** "+serviceName);

                XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration
                        .builder()
                        .setUsernameAndPassword(""+usr,""+passwd)
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName)
                        .setHostnameVerifier(verifier)
                        .setHostAddress(addr)
                        .build();

                Log.i(TAG,"**** CONFIG XMPP *****" + config.getUsername()+" - "+config.getPassword());

                mConnection=new XMPPTCPConnection(config);

                try {
                    mConnection.connect();
                    mConnection.login();

                    Log.i(TAG,"**** CONNECTION XMPP *****");

                    if(mConnection.isAuthenticated() && mConnection.isConnected()){

                        Log.i(TAG,"Conexion XMPP Autenticada");
                        Log.i(TAG,"Usuario "+usr);

                        ChatManager chatManager=ChatManager.getInstanceFor(mConnection);
                        chatManager.addIncomingListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                                /*********************************************
                                 * Creando las notificaciones en tiempo real *
                                 *********************************************/
                                Log.i(TAG,"¡New Notification! Preparing to send...");
                                NotificationCompat.Builder notificationBuilder;
                                NotificationManager notificatoinManager=(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

                                int iconNotification=R.mipmap.ic_launcher;
                                Intent intentNotification=new Intent(MainActivity.this,MainActivity.class);
                                PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this,0,intentNotification,0);

                                /*********************************************
                                 * Iniciando la Configurando la notificacion *
                                 *********************************************/
                                Uri soundNotification= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                notificationBuilder=new NotificationCompat.Builder(getApplicationContext())
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(iconNotification)
                                        .setContentTitle("Nuevo Mensaje")
                                        .setContentText(message.getBody())
                                        .setVibrate(new long[]{100,250,100,100})
                                        .setSound(soundNotification)
                                        .setAutoCancel(true);

                                /****************************
                                 * Lanzando la Notificación *
                                 ****************************/
                                notificatoinManager.notify(1,notificationBuilder.build());
                                Log.i(TAG,"Show Notification Succesfully!");

                                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd_HHmmss",new Locale("es_ES"));
                                Date date=new Date();
                                String fecha=dateFormat.format(date);

                                Log.i(TAG,"Nuevo mensaje de "+from+": "+message.getBody()+" : "+fecha);

                                /**************************************************
                                 * Añadiendo el Mensaje recibido al recycler view *
                                 **************************************************/
                                MessagesData data=new MessagesData("Recibido "+fecha,""+message.getBody().toString());
                                mMessageData.add(data);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter=new Adapter(mMessageData);
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                });
                            }
                        });

                    }else{
                        Log.e(TAG,"Fallo La Conexión");
                    }

                } catch (SmackException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }

            }
        }.start();

    }
}
