package com.YouFool.danelc.youfool;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import org.jibble.pircbot.PircBot;

import java.util.Random;

/**
 * Created by bist on 08/01/2017.
 */
public class MyBot extends PircBot  {
    private final MainActivity m;
    private String Nick="";
    int version;
    public MyBot(MainActivity m,String channel,String nick,int ver){
        this.setName("FoolPlayer_");
        this.m = m;
        Channel=channel;
        this.Nick=nick;
        this.version=ver;
    }
    String Channel;
    boolean Ready=false;
    int player=0,LastPlayer=1,SenNum;
    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        super.onMessage(channel, sender, login, hostname, message);
        Log.e("recived messege",  message);
        //sendMessage(Channel,"hey "+sender+" please stop with thos "+message+"stupid messges");
        if (message.equals("Player"+player+" connected"))
        {
            if(Ready)
                sendMessage(Channel,"Game is on");
            sendMessage(Channel,"Player"+player+" used");
            sendMessage(Channel,"Player"+player+": Nick: "+Nick);
            //Handler ehandler=new Handler();
            //ehandler.postDelayed(new Runnable() {
                //@Override
                //public void run() {
                    //m.SetNicks("Player"+player+": Nick: "+Nick);
              //  }
            //}, 1000);

        }
        if(message.contains("Player2 connected")&&LastPlayer<2)
            LastPlayer=2;
        if(message.contains("Player3 connected")&&LastPlayer<3)
            LastPlayer=3;
        if(message.contains("Player4 connected")&&LastPlayer<4)
            LastPlayer=4;

        if (message.equals("Player"+player+" used"))
        {
            if (message.equals("Player4 used")){
                m.ConnectionReset(1);
                dispose();
            }
            else
            {
                player++;
                sendMessage(Channel,"Player"+player+" connected");
                m.SetPlayer(player);
                LastPlayer=player;
                sendMessage(Channel,"Player"+player+": Nick: "+Nick);
                m.SetNicks("Player"+player+": Nick: "+Nick);
                //sendMessage(Channel,"Player"+player+": Nick: "+Nick);
                //m.SetNicks("Player"+player+": Nick: "+Nick);
            }
        }
        if(player==4&&message.contains("Player3 used"))
        {
            sendMessage(Channel,"Player"+player+": Game ready");
            Ready=true;
            m.addSendButton();
            m.setPlaying("Playing:"+LastPlayer);
            sendMessage(Channel,"Player"+player+": Nick: "+Nick);
            m.SetNicks("Player"+player+": Nick: "+Nick);
            sendMessage(Channel,"Player"+player+": Ver: "+version);
        }
        if (message.contains("Game ready"))
        {
            Ready=true;
            m.addSendButton();
            if (player==1)
            {
                SendNewSen();
            }
            m.setPlaying("Playing:"+LastPlayer);
            sendMessage(Channel,"Player"+player+": Nick: "+Nick);
            m.SetNicks("Player"+player+": Nick: "+Nick);
            sendMessage(Channel,"Player"+player+": Ver: "+version);

        }
        if (message.contains("Nick:"))
            if(message.startsWith("Player"+player))
                sendMessage(Channel,"Player"+player+": Nick: "+Nick);
                //m.SetNicks("Player"+player+": Nick: "+Nick);
            else
                m.SetNicks(message);
        if (message.contains("Sen:")) m.setSentence(Integer.parseInt(message.substring(14)));
        if (message.contains("Lie:")) m.setLie(message);
        if (message.contains("Choose:")) m.setChoose(message);
        if(message.contains("Change acti"))m.setLie("acti");
        if(message.contains("Playing:"))m.setPlaying(message);
        if(message.contains("Ver:"))
        {
            String var=message.substring(14);
            if(Integer.parseInt(var)>version)
                m.setLowVersion();

        }
        if(message.contains("connected"))
            m.sCheckLight(LastPlayer);
        if(message.contains("Force newRound"))
            m.mStatusChecker.run();
        if(message.contains("Disconnected."))
        {
            LastPlayer--;
            if(Ready)
                m.sCheckLight(-Integer.parseInt(message.substring(6,7)));
            else{
                //m.sCheckLight(-1);
                m.sCheckLight(-2);
                m.sCheckLight(-3);
                m.sCheckLight(-4);
            }
            if(player==LastPlayer+1&&Integer.parseInt(message.substring(6,7))<4)
            {
                //player=Integer.parseInt(reason.substring(7,8));
                //sendMessage(Channel,"Player");

                //sendMessage(Channel,"Player"+player+" Disconnected.");
                if(player!=2)
                {
                    sendMessage(Channel,"Player1 connected");
                    sendMessage(Channel,"Player"+player+": Nick: ");
                    m.SetNicks("Player"+player+": Nick: ");
                    player=1;
                    m.SetPlayer(player);
                    //sendMessage(Channel,"Player"+player+": Nick: "+Nick);
                    m.SetNicks("Player"+player+": Nick: "+Nick);
                }
                else//only one player left game has no enough players!
                {
                    m.ConnectionReset(4);
                    dispose();
                }
            }
        }
        if(message.equals("Game is on")&&!Ready){
            m.ConnectionReset(3);
            dispose();
        }

    }
    @Override
    protected void onConnect()
    {
        player=1;
        sendMessage(Channel,"Player"+player+": Nick: "+Nick);
        sendMessage(Channel,"Player1 connected");


    }
    @Override
    protected void onJoin(String s1,String sender,String login, String s4){
        //if (login==getLogin())
        m.SetPlayer(player);

        m.SetNicks("Player"+player+": Nick: "+Nick);
        MediaPlayer mp =MediaPlayer.create(m, R.raw.welcome_sound);
        mp.start();
    }
    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason){
    if (reason.contains("Disconnected.")){
        /*if(reason.contains("Player1"))
            m.setDisconnected(1);
        if(reason.contains("Player2"))
            m.setDisconnected(2);
        if(reason.contains("Player3"))
            m.setDisconnected(3);
        if(reason.contains("Player4"))
            m.setDisconnected(4);*/
        if(player==LastPlayer&&Integer.parseInt(reason.substring(7,8))<4)
        {
            //player=Integer.parseInt(reason.substring(7,8));
            //sendMessage(Channel,"Player");
            sendMessage(Channel,"Player"+player+" Disconnected.");
            sendMessage(Channel,"Player1 connected");
            player=1;
        }
        }
    }

    public void SendLie(String s)
    {
        if(Ready)
            sendMessage(Channel,"Player"+player+": Lie: "+s);
    }
    public void SendChoise(int i){
        sendMessage(Channel,"Player"+player+": Choose: "+i);
    }
    public int getPlayer(){return player;}
    //public void StartShow(int li){sendMessage(Channel,"Player"+player+": Start Show:"+li);}
    public void SendNewSen(){
        //String s=GetSentence();
        Random rnd=new Random();
        SenNum=rnd.nextInt(51);
        //SenNum=8;//FIX DIS NOWWWW
        sendMessage(Channel,"Player"+player+": Sen: "+SenNum);
        m.setSentence(SenNum);
    }
    public void SetChannel(String ch){
        Channel=ch;
    }
    public void ForceStart(){
        sendMessage(Channel,"Player"+player+": Game ready");
        Ready=true;
        m.addSendButton();
        if (player==1)
        {
            SendNewSen();
        }
        m.setPlaying("Playing:"+LastPlayer);
        sendMessage(Channel,"Playing:"+LastPlayer);
        sendMessage(Channel,"Player"+player+": Nick: "+Nick);
        m.SetNicks("Player"+player+": Nick: "+Nick);
        sendMessage(Channel,"Player"+player+": Ver: "+version);

    }
    public int getLastPlayer(){return LastPlayer;}


}
