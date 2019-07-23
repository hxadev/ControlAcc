package com.example.controlacc;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.controlacc.model.Escuela;
import com.example.controlacc.model.MessagesData;
import com.example.controlacc.model.User;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.apache.logging.log4j.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


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

        //System.out.println(">>>>> "+sp.getAll().get("nameStudent"));


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
        Log.e(TAG,"Conexion Establecida");
    }


    /**
     *
     */
    private void setConnection(){
        Log.e(TAG,"user: "+usr+"\npass: "+passwd+"\nServer: "+server);
        Log.e(TAG,"Iniciando Conexion...");
        new Thread(){

            @Override
            public void run() {

                InetAddress addr=null;

                try {
                    addr=InetAddress.getByName(server);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                Log.e(TAG,"**** Addr ***** " +addr);

                HostnameVerifier verifier=new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return false;
                    }
                };

               Log.e(TAG,"**** HOST VERIFIER *****");


                DomainBareJid serviceName=null;

                try {
                    serviceName= JidCreate.domainBareFrom(server);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();

                }

                Log.e(TAG,"**** SERVICE NAME ***** "+serviceName);

                XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration
                        .builder()
                        .setUsernameAndPassword(""+usr,""+passwd)
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName)
                        .setHostnameVerifier(verifier)
                        .setHostAddress(addr)
                        .build();

                Log.e(TAG,"**** CONFIG XMPP *****" + config.getUsername()+" - "+config.getPassword());

                mConnection=new XMPPTCPConnection(config);

                try {
                    mConnection.connect();
                    mConnection.login();

                    Log.e(TAG,"**** CONNECTION XMPP *****");

                    if(mConnection.isAuthenticated() && mConnection.isConnected()){

                        Log.e(TAG,"Conexion XMPP Autenticada");
                        Log.e(TAG,"Usuario "+usr);

                        ChatManager chatManager=ChatManager.getInstanceFor(mConnection);
                        chatManager.addIncomingListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                                Log.e(TAG,"Nuevo mensaje de "+from+": "+message.getBody());


                                SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMMM-yyyy",new Locale("es_ES"));
                                Date date=new Date();
                                String fecha=dateFormat.format(date);

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
