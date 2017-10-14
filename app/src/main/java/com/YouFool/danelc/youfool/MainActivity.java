package com.YouFool.danelc.youfool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jibble.pircbot.IrcException;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String Word="",FixSen="",Channel="YouFoolRoom1",Nick="";
    private Handler ehandler;
    int player=0,Switch,PointWorth=500,show=0,playing=4,version,SenNumber;
    boolean Choose=false,Started=false,doubleBackToExitPressedOnce = false,LowVersion=false, GMFindTheLie=false,NickClock=false;
    String[] Lies={"","","",""};
    int[] Choise={5,5,5,5},Score={0,0,0,0};
    Button BLie1,BLie2,BLie3,BLie4,BLie5;
    EditText LieText;
    RadioButton redLight,greenLight,yellowLight,blueLight;
    MyBot bot;
    Menu menus;
    Spinner spinner;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent cred=new Intent(this,Credits.class);
                if(LowVersion)
                    cred.putExtra("lowVer",true);
                cred.putExtra("Version",BuildConfig.VERSION_NAME);
                startActivity(cred);
                return true;
            case R.id.action_start:
                if(Started)
                {
                    TextView sen = (TextView)findViewById(R.id.textView);
                    if(bot.getLastPlayer()>1){
                        if(sen.getText().equals("Please wait while all player connect."))
                        {
                        MenuItem mitem = menus.findItem(R.id.action_start);
                        mitem.setVisible(false);
                        this.invalidateOptionsMenu();
                        bot.ForceStart();
                        }
                        else Snackbar.make(findViewById(R.id.editText), "game already started", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    else
                        Snackbar.make(findViewById(R.id.editText), "You cannot play alone(Lonely ?)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else
                    Snackbar.make(findViewById(R.id.editText), "cannot ForceStart while not connected", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            case R.id.action_tutorial:
                Intent tut=new Intent(this,HowTo.class);
                startActivity(tut);
                return true;
            case R.id.action_quit:
                if(Started)
                {
                    bot.sendMessage("#"+Channel,"Player"+player+" Disconnected.");
                    ehandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bot.disconnect();
                            bot.dispose();
                            ConnectionReset(0);
                        }
                    },500);

                    //android.os.Process.killProcess(android.os.Process.myPid());
                }
                else
                    Snackbar.make(findViewById(R.id.editText), "You are not connected. can safely exit in other ways.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        version=BuildConfig.VERSION_CODE;
        setContentView(R.layout.activity_main);
        Toolbar tool=(Toolbar)findViewById(R.id.toolbar) ;
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ehandler = new Handler();
        BLie1 = (Button) findViewById(R.id.BLie1);
        BLie2 = (Button) findViewById(R.id.BLie2);
        BLie3 = (Button) findViewById(R.id.BLie3);
        BLie4 = (Button) findViewById(R.id.BLie4);
        BLie5 = (Button) findViewById(R.id.BLie5);
        redLight = (RadioButton) findViewById(R.id.radioButton1);
        greenLight = (RadioButton) findViewById(R.id.radioButton2);
        yellowLight = (RadioButton) findViewById(R.id.radioButton3);
        blueLight = (RadioButton) findViewById(R.id.radioButton4);
        spinner = (Spinner) findViewById(R.id.spinner);
        LieText=(EditText)findViewById(R.id.editText) ;
        FloatingActionButton send=(FloatingActionButton)findViewById(R.id.floatingActionButton);

        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width=(width-420)/3;
        Space space1=(Space)findViewById(R.id.space1);
        Space space2=(Space)findViewById(R.id.space2);
        Space space3=(Space)findViewById(R.id.space3);
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width,16);
        //space1.getLayoutParams().width=width;
        //space2.getLayoutParams().width=width;
        //space3.getLayoutParams().width=width;*/
        ehandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChangeSpaceWidth();
            }
        }, 2000);
        Button LieForMe=(Button) findViewById(R.id.BSuggest);
        LieForMe.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Phrase phrases=new Phrase();
                        Random rnd=new Random();
                        LieText.setText(phrases.getSuggest(SenNumber)[rnd.nextInt(phrases.getSuggest(SenNumber).length)]);
                        //SendLie(LieText.getText().toString());
                    }
                }
        );
        LieText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if(spinner.getVisibility()==View.INVISIBLE)
                        SendLie(LieText.getText().toString());
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view) {
                if(spinner.getVisibility()==View.INVISIBLE)
                {
                    SendLie(LieText.getText().toString());
                    InputMethodManager imm = (InputMethodManager) LieText.getRootView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(LieText.getRootView().getWindowToken(), 0);
                }
            }
        });
        LieText.setText(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("UserNick", ""));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                EditText edit = (EditText)findViewById(R.id.editText) ;
                Nick = edit.getText().toString();
                if(Nick.length()<=12) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Channel = String.valueOf(spinner.getSelectedItem());
                    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    final ProgressBar pro = (ProgressBar) findViewById(R.id.progressBar);
                    ehandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.setVisibility(View.INVISIBLE);
                            pro.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("UserNick", Nick).commit();
                    spinner.setVisibility(View.INVISIBLE);
                    if (!Started) {
                        TextView Sen = (TextView) findViewById(R.id.textView);
                        Sen.setText("Please wait while all player connect.");
                        edit.setText("");
                        edit.setVisibility(View.INVISIBLE);
                        edit.setHint("Enter Lie");
                        Snackbar.make(findViewById(R.id.editText), "connecting to " + Channel, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Started = true;
                                bot = new MyBot(MainActivity.this, "#" + Channel, Nick,version);
                                bot.setVerbose(true);
                                bot.setAutoNickChange(true);
                                try {
                                    bot.connect("irc.freenode.net");
                                    bot.joinChannel("#" + Channel);
                                    bot.SetChannel("#" + Channel);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (IrcException e) {
                                    e.printStackTrace();
                                }
                                while (!bot.isConnected()) {
                                }
                                Snackbar.make(findViewById(R.id.editText), "Connection to server successful!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }).start();
                    } else
                        Snackbar.make(findViewById(R.id.editText), "Already connected, no need to reconnect.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else{
                    Snackbar.make(findViewById(R.id.editText), "That nickname is too long.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menus=menu;
        return true;
    }
    @Override
    public void onBackPressed() {
        if(!Started)
        {
        if (doubleBackToExitPressedOnce) {
            //bot.quitServer("Player"+player+" disconnected");
            //bot.sendMessage(Channel,"Player"+player+" tried disconnect");
            if(Started)
                bot.dispose();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
        }
        else
            Snackbar.make(findViewById(R.id.editText), "please use the Disconnect option in the menu.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
    public void setSentence(final int SenNum){
        final TextView sen = (TextView) findViewById(R.id.textView);
        final TextView show = (TextView) findViewById(R.id.ShowLie);
        final Button LieForMe=(Button)findViewById(R.id.BSuggest);
        final Phrase phrases=new Phrase();
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                LieForMe.setVisibility(View.VISIBLE);
                SenNumber=SenNum-1;
                //SenNumber=7;//FIX DIS RIGHT NOWWWWW
                ehandler.removeCallbacks(mStatusChecker);
                show.setVisibility(View.INVISIBLE);
                String message=phrases.getSen(SenNum-1);
                redLight.setChecked(false);
                greenLight.setChecked(false);
                yellowLight.setChecked(false);
                blueLight.setChecked(false);
                BLie1.setVisibility(View.INVISIBLE);
                BLie2.setVisibility(View.INVISIBLE);
                BLie3.setVisibility(View.INVISIBLE);
                BLie4.setVisibility(View.INVISIBLE);
                BLie5.setVisibility(View.INVISIBLE);
                Word=message.substring(message.indexOf("*")+1,message.lastIndexOf("*"));
                FixSen=message.replace("*"+Word+"*","______");
                if(FixSen.indexOf("Sen:")!=-1)
                    FixSen=FixSen.substring(message.indexOf("Sen:")+4);
                sen.setText(FixSen);
                EditText lier=(EditText)findViewById(R.id.editText);
                lier.setVisibility(View.VISIBLE);
            }
        });
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                StartShow(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(show<6)
                    ehandler.postDelayed(mStatusChecker, 5000);
                else
                {
                    TextView show=(TextView)findViewById(R.id.ShowLie);
                    show.setVisibility(View.INVISIBLE);
                    ehandler.removeCallbacks(mStatusChecker);
                }
            }
        }
    };
    public void setLie(final String message){


        Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                switch(playing){
                    case 2:yellowLight.setChecked(true);blueLight.setChecked(true);break;
                    case 3:blueLight.setChecked(true);break;
                }
                if(message.contains("acti"))
                    Choose=true;
                if (message.contains("Player1: Lie:")) {
                    CheckLight(1);
                    Lies[0] = message.substring(message.indexOf("Lie:") + 4).trim();
                }
                if (message.contains("Player2: Lie:")) {
                    CheckLight(2);
                    Lies[1] = message.substring(message.indexOf("Lie:") + 4).trim();
                }
                if (message.contains("Player3: Lie:")) {
                    CheckLight(3);
                    Lies[2] = message.substring(message.indexOf("Lie:") + 4).trim();
                }
                if (message.contains("Player4: Lie:")) {
                    CheckLight(4);
                    Lies[3] = message.substring(message.indexOf("Lie:") + 4).trim();
                }
                if (redLight.isChecked() && greenLight.isChecked() && yellowLight.isChecked() && blueLight.isChecked()||Choose) {
                    StartChoose();
                    Choose=false;
                }

            }
        });
    }
    public void SendLie(String lied){
        TextView text =(TextView)findViewById(R.id.editText) ;
        String lie=lied.toLowerCase();
        Phrase phrases=new Phrase();
        String[] AltSpells=phrases.getAltSpell(SenNumber);
        boolean isLieOk=true;
        for(int i=0;i<AltSpells.length;i++){
            if(lie.equals(AltSpells[i].toLowerCase()))
                isLieOk=false;
        }
        for(int i=0; i<Lies.length;i++){
            if(lie.equals(Lies[i].toLowerCase()))
                isLieOk=false;
        }
        if(lie==Word.toLowerCase())
            isLieOk=false;
        if(isLieOk){
            bot.SendLie(lied);
            final Drawable Dv = getResources().getDrawable(android.R.drawable.checkbox_on_background);
            text.setCompoundDrawablesWithIntrinsicBounds(null,null,Dv,null);
            setLie("Player"+player+": Lie:"+LieText.getText().toString());
            Snackbar.make(findViewById(R.id.editText), "Lie sent!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        else{
            text.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            Snackbar.make(findViewById(R.id.editText), "Lie was already used.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    public void StartChoose(){
        String temp,word=this.Word;
        Random rnd = new Random();
        FloatingActionButton send = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        send.setVisibility(View.GONE);
        Button LieForMe=(Button)findViewById(R.id.BSuggest);
        LieForMe.setVisibility(View.GONE);
        LieText.setVisibility(View.INVISIBLE);
        final int change = rnd.nextInt(5);
        switch (change) {
            case 0:
                temp = Lies[0];
                Lies[0] = word;
                word = temp;
                break;
            case 1:
                temp = Lies[1];
                Lies[1] = word;
                word = temp;
                break;
            case 2:
                temp = Lies[2];
                Lies[2] = word;
                word = temp;
                break;
            case 3:
                temp = Lies[3];
                Lies[3] = word;
                word = temp;
                break;
            case 4:break;
        }
        this.Switch=change;
        redLight.setChecked(false);
        greenLight.setChecked(false);
        yellowLight.setChecked(false);
        blueLight.setChecked(false);
        EditText inlie =(EditText)findViewById(R.id.editText) ;
        inlie.setText("");
        if(Lies[0].length()>10)
            BLie1.setTextSize((float)10);
        if(Lies[1].length()>10)
            BLie2.setTextSize((float)10);
        if(word.length()>10)
            BLie3.setTextSize((float)10);
        if(Lies[2].length()>10)
            BLie4.setTextSize((float)10);
        if(Lies[3].length()>10)
            BLie5.setTextSize((float)10);
        BLie1.setText(Lies[0]);
        BLie2.setText(Lies[1]);
        BLie3.setText(word);
        BLie4.setText(Lies[2]);
        BLie5.setText(Lies[3]);
        BLie1.setVisibility(View.VISIBLE);
        BLie2.setVisibility(View.VISIBLE);
        BLie3.setVisibility(View.VISIBLE);
        BLie4.setVisibility(View.VISIBLE);
        BLie5.setVisibility(View.VISIBLE);
        final Drawable don = getResources().getDrawable(android.R.drawable.button_onoff_indicator_on);
        final Drawable doff = getResources().getDrawable(android.R.drawable.button_onoff_indicator_off);
        BLie1.setClickable(true);
        BLie2.setClickable(true);
        BLie3.setClickable(true);
        BLie4.setClickable(true);
        BLie5.setClickable(true);
        BLie1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLight(player);
                BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, don);
                if (change == 0){
                    bot.SendChoise(0);
                    Choise[player-1]=0;
                }
                else{
                    bot.SendChoise(1);
                    Choise[player-1]=1;
                }
                if(Choise[0]!=5&&Choise[1]!=5&&Choise[2]!=5&&Choise[3]!=5) {
                    mStatusChecker.run();
                }
            }
        });
        BLie2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLight(player);
                BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, don);
                if (change == 1) {
                    bot.SendChoise(0);
                    Choise[player-1]=0;
                }
                else {
                    bot.SendChoise(2);
                    Choise[player-1]=2;
                }
                if(Choise[0]!=5&&Choise[1]!=5&&Choise[2]!=5&&Choise[3]!=5) {
                    mStatusChecker.run();
                }
            }
        });
        BLie3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLight(player);
                BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, don);
                if(change==4) {
                    bot.SendChoise(0);
                    Choise[player-1]=0;
                }
                else {
                    bot.SendChoise(change + 1);
                    Choise[player-1]=change+1;
                }
                if(Choise[0]!=5&&Choise[1]!=5&&Choise[2]!=5&&Choise[3]!=5) {
                    mStatusChecker.run();
                }
            }
        });
        BLie4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLight(player);
                BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, don);
                if (change == 2) {
                    bot.SendChoise(0);
                    Choise[player-1]=0;
                }
                else {
                    bot.SendChoise(3);
                    Choise[player-1]=3;
                }if(Choise[0]!=5&&Choise[1]!=5&&Choise[2]!=5&&Choise[3]!=5) {
                    mStatusChecker.run();
                }
            }
        });
        BLie5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLight(player);
                BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
                BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, don);
                if (change == 3) {
                    bot.SendChoise(0);
                    Choise[player-1]=0;
                }
                else {
                    bot.SendChoise(4);
                    Choise[player-1]=4;
                }
                if(Choise[0]!=5&&Choise[1]!=5&&Choise[2]!=5&&Choise[3]!=5) {
                    mStatusChecker.run();
                }
            }
        });
        if(BLie1.getText().equals(""))
            BLie1.setVisibility(View.INVISIBLE);
        if(BLie2.getText().equals(""))
            BLie2.setVisibility(View.INVISIBLE);
        if(BLie3.getText().equals(""))
            BLie3.setVisibility(View.INVISIBLE);
        if(BLie4.getText().equals(""))
            BLie4.setVisibility(View.INVISIBLE);
        if(BLie5.getText().equals(""))
            BLie5.setVisibility(View.INVISIBLE);
        switch (change) {
            case 0:
                temp = Lies[0];
                Lies[0] = word;
                word = temp;
                break;
            case 1:
                temp = Lies[1];
                Lies[1] = word;
                word = temp;
                break;
            case 2:
                temp = Lies[2];
                Lies[2] = word;
                word = temp;
                break;
            case 3:
                temp = Lies[3];
                Lies[3] = word;
                word = temp;
                break;
            case 4:break;
        }

    }
    public void setChoose(final String s) {
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                switch (playing){
                    case 2:CheckLight(4);CheckLight(3);Choise[3]=6;Choise[2]=6;break;
                    case 3:CheckLight(4);Choise[3]=6;break;
                }
                if (s.contains("Player1:")) {
                    CheckLight(1);
                    if (s.contains("Choose: 0")) Choise[0] = 0;
                    if (s.contains("Choose: 1")) Choise[0] = 1;
                    if (s.contains("Choose: 2")) Choise[0] = 2;
                    if (s.contains("Choose: 3")) Choise[0] = 3;
                    if (s.contains("Choose: 4")) Choise[0] = 4;
                }
                if (s.contains("Player2:")) {
                    CheckLight(2);
                    if (s.contains("Choose: 0")) Choise[1] = 0;
                    if (s.contains("Choose: 1")) Choise[1] = 1;
                    if (s.contains("Choose: 2")) Choise[1] = 2;
                    if (s.contains("Choose: 3")) Choise[1] = 3;
                    if (s.contains("Choose: 4")) Choise[1] = 4;
                }
                if (s.contains("Player3:")) {
                    CheckLight(3);
                    if (s.contains("Choose: 0")) Choise[2] = 0;
                    if (s.contains("Choose: 1")) Choise[2] = 1;
                    if (s.contains("Choose: 2")) Choise[2] = 2;
                    if (s.contains("Choose: 3")) Choise[2] = 3;
                    if (s.contains("Choose: 4")) Choise[2] = 4;
                }
                if (s.contains("Player4:")) {
                    CheckLight(4);
                    if (s.contains("Choose: 0")) Choise[3] = 0;
                    if (s.contains("Choose: 1")) Choise[3] = 1;
                    if (s.contains("Choose: 2")) Choise[3] = 2;
                    if (s.contains("Choose: 3")) Choise[3] = 3;
                    if (s.contains("Choose: 4")) Choise[3] = 4;
                }
                if(Choise[0]!=5&&Choise[1]!=5&&Choise[2]!=5&&Choise[3]!=5) {
                    mStatusChecker.run();
                    BLie1.setTextSize((float)14);
                    BLie2.setTextSize((float)14);
                    BLie3.setTextSize((float)14);
                    BLie4.setTextSize((float)14);
                    BLie5.setTextSize((float)14);
                }
            }
        });
    }
    private void StartShow() {
        ShowResults(show);
        final Drawable doff = getResources().getDrawable(android.R.drawable.button_onoff_indicator_off);
        BLie1.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
        BLie2.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
        BLie3.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
        BLie4.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
        BLie5.setCompoundDrawablesWithIntrinsicBounds(null,null,null, doff);
        if(show>=playing-1&&!(show>=4))
            show=4;
        else
            show++;
        if(show==4) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.drum_roll_sound);
            ehandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mp.start();
                }
            }, 1300);

        }

    }
    public void ShowResults(final int li){
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView ShowLie=(TextView)findViewById(R.id.ShowLie);
                TextView Rpoints=(TextView)findViewById(R.id.Rpoints);
                TextView Gpoints=(TextView)findViewById(R.id.Gpoints);
                TextView Ypoints=(TextView)findViewById(R.id.Ypoints);
                TextView Bpoints=(TextView)findViewById(R.id.Bpoints);
                Rpoints.setVisibility(View.INVISIBLE);
                Gpoints.setVisibility(View.INVISIBLE);
                Ypoints.setVisibility(View.INVISIBLE);
                Bpoints.setVisibility(View.INVISIBLE);
                ShowLie.setVisibility(View.VISIBLE);
                redLight.setChecked(false);
                greenLight.setChecked(false);
                yellowLight.setChecked(false);
                blueLight.setChecked(false);
                switch (li){
                    case 0:ShowLie.setTextColor(Color.RED);break;
                    case 1:ShowLie.setTextColor(Color.GREEN);break;
                    case 2:ShowLie.setTextColor(Color.YELLOW);break;
                    case 3:ShowLie.setTextColor(Color.BLUE);break;
                }
                BLie1.setText("Lie1");
                BLie2.setText("Lie2");
                BLie3.setText("Lie3");
                BLie4.setText("Lie4");
                BLie5.setText("Lie5");
                BLie1.setVisibility(View.INVISIBLE);
                BLie2.setVisibility(View.INVISIBLE);
                BLie3.setVisibility(View.INVISIBLE);
                BLie4.setVisibility(View.INVISIBLE);
                BLie5.setVisibility(View.INVISIBLE);
                if (li != 5) {
                    if (li == 4) ShowLie.setText(Word);
                    else ShowLie.setText(Lies[li]);
                    if(!GMFindTheLie){
                        if(li!=4)
                        {
                            if (Choise[0] == li + 1) {
                                redLight.setChecked(true);
                                Rpoints.setVisibility(View.VISIBLE);
                                if(li!=0)
                                {
                                    Rpoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Rpoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                            if (Choise[1] == li + 1) {
                                greenLight.setChecked(true);
                                Gpoints.setVisibility(View.VISIBLE);
                                if(li!=1)
                                {
                                    Gpoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Gpoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                            if (Choise[2] == li + 1) {
                                yellowLight.setChecked(true);
                                Ypoints.setVisibility(View.VISIBLE);
                                if(li!=2)
                                {
                                    Ypoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Ypoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                            if (Choise[3] == li + 1) {
                                blueLight.setChecked(true);
                                Bpoints.setVisibility(View.VISIBLE);
                                if(li!=3)
                                {
                                    Bpoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Bpoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                        }
                        else
                        {
                            ShowLie.setTextColor(Color.GRAY);
                            if (Choise[0] == 0) {
                                redLight.setChecked(true);
                                Rpoints.setText("+"+2*PointWorth);
                                Rpoints.setVisibility(View.VISIBLE);
                                Score[0] += 2*PointWorth;
                            }
                            if (Choise[1] == 0) {
                                greenLight.setChecked(true);
                                Gpoints.setText("+"+2*PointWorth);
                                Gpoints.setVisibility(View.VISIBLE);
                                Score[1] += 2*PointWorth;
                            }
                            if (Choise[2] == 0) {
                                yellowLight.setChecked(true);
                                Ypoints.setText("+"+2*PointWorth);
                                Ypoints.setVisibility(View.VISIBLE);
                                Score[2] += 2*PointWorth;
                            }
                            if (Choise[3] == 0) {
                                blueLight.setChecked(true);
                                Bpoints.setText("+"+2*PointWorth);
                                Bpoints.setVisibility(View.VISIBLE);
                                Score[3] += 2*PointWorth;
                            }
                        }
                    }
                    else {
                        if(li!=4)
                        {
                            if (Choise[0] == li + 1) {
                                redLight.setChecked(true);
                                Rpoints.setVisibility(View.VISIBLE);
                                if(li!=0)
                                {
                                    Rpoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Rpoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                            if (Choise[1] == li + 1) {
                                greenLight.setChecked(true);
                                Gpoints.setVisibility(View.VISIBLE);
                                if(li!=1)
                                {
                                    Gpoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Gpoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                            if (Choise[2] == li + 1) {
                                yellowLight.setChecked(true);
                                Ypoints.setVisibility(View.VISIBLE);
                                if(li!=2)
                                {
                                    Ypoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Ypoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                            if (Choise[3] == li + 1) {
                                blueLight.setChecked(true);
                                Bpoints.setVisibility(View.VISIBLE);
                                if(li!=3)
                                {
                                    Bpoints.setText("+"+PointWorth);
                                    Score[li] += PointWorth;
                                }
                                else{
                                    Bpoints.setText("+"+PointWorth/2);
                                    Score[li] += PointWorth/2;
                                }
                            }
                        }
                        else
                        {
                            ShowLie.setTextColor(Color.GRAY);
                            if (Choise[0] == 0) {
                                redLight.setChecked(true);
                                Rpoints.setText("+"+2*PointWorth);
                                Rpoints.setVisibility(View.VISIBLE);
                                Score[0] += 2*PointWorth;
                            }
                            if (Choise[1] == 0) {
                                greenLight.setChecked(true);
                                Gpoints.setText("+"+2*PointWorth);
                                Gpoints.setVisibility(View.VISIBLE);
                                Score[1] += 2*PointWorth;
                            }
                            if (Choise[2] == 0) {
                                yellowLight.setChecked(true);
                                Ypoints.setText("+"+2*PointWorth);
                                Ypoints.setVisibility(View.VISIBLE);
                                Score[2] += 2*PointWorth;
                            }
                            if (Choise[3] == 0) {
                                blueLight.setChecked(true);
                                Bpoints.setText("+"+2*PointWorth);
                                Bpoints.setVisibility(View.VISIBLE);
                                Score[3] += 2*PointWorth;
                            }
                        }
                    }
                    TextView rScore = (TextView) findViewById(R.id.ScoreRed);
                    TextView gScore = (TextView) findViewById(R.id.ScoreGreen);
                    TextView yScore = (TextView) findViewById(R.id.ScoreYellow);
                    TextView bScore = (TextView) findViewById(R.id.ScoreBlue);
                    rScore.setText(Score[0]+"");
                    gScore.setText(Score[1]+"");
                    yScore.setText(Score[2]+"");
                    bScore.setText(Score[3]+"");
                }
                else
                    NewRound();
            }
        });
    }
    public void sCheckLight(final int p){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                switch (p) {
                    case -1:
                        redLight.setChecked(false);
                        break;
                    case -2:
                        greenLight.setChecked(false);
                        break;
                    case -3:
                        yellowLight.setChecked(false);
                        break;
                    case -4:
                        blueLight.setChecked(false);
                        break;
                    case 1:
                        redLight.setChecked(true);
                        break;
                    case 2:
                        greenLight.setChecked(true);
                        break;
                    case 3:
                        yellowLight.setChecked(true);
                        break;
                    case 4:
                        blueLight.setChecked(true);
                        break;
               }
            }
        });
    }
    private void CheckLight(int p){
        switch (p) {
            case -1:
                redLight.setChecked(false);
                break;
            case -2:
                greenLight.setChecked(false);
                break;
            case -3:
                yellowLight.setChecked(false);
                break;
            case -4:
                blueLight.setChecked(false);
                break;
            case 1:
                redLight.setChecked(true);
                break;
            case 2:
                greenLight.setChecked(true);
                break;
            case 3:
                yellowLight.setChecked(true);
                break;
            case 4:
                blueLight.setChecked(true);
                break;
        }
    }
    public void forceNewRound(){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                NewRound();
            }
        });
    }
    public void NewRound(){
        EditText InLie=(EditText)findViewById(R.id.editText);
        InLie.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        FloatingActionButton send = (FloatingActionButton)findViewById(R.id.floatingActionButton) ;
        send.setVisibility(View.VISIBLE);
        TextView shower=(TextView)findViewById(R.id.ShowLie);
        TextView Rpoints=(TextView)findViewById(R.id.Rpoints);//making the scoring of each player invisible just in case
        Rpoints.setVisibility(View.INVISIBLE);
        TextView Gpoints=(TextView)findViewById(R.id.Gpoints);
        Gpoints.setVisibility(View.INVISIBLE);
        TextView Ypoints=(TextView)findViewById(R.id.Ypoints);
        Ypoints.setVisibility(View.INVISIBLE);
        TextView Bpoints=(TextView)findViewById(R.id.Bpoints);
        Bpoints.setVisibility(View.INVISIBLE);
        Button LieForMe=(Button)findViewById(R.id.BSuggest);
        InLie.setVisibility(View.VISIBLE);
        LieForMe.setVisibility(View.VISIBLE);
        show=0;
        shower.setVisibility(View.INVISIBLE);
        for(int i=0;i<4;i++)
        {
            Lies[i]="";
            Choise[i]=5;
        }
        if(player==1)
            bot.SendNewSen();
    }
    public void SetPlayer(final int p){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                ProgressBar prog=(ProgressBar)findViewById(R.id.progressBar);
                prog.setVisibility(View.GONE);
                TextView Rshow=(TextView)findViewById(R.id.Rshow) ;
                TextView Gshow=(TextView)findViewById(R.id.Gshow) ;
                TextView Yshow=(TextView)findViewById(R.id.Yshow) ;
                TextView Bshow=(TextView)findViewById(R.id.Bshow) ;
                Rshow.setVisibility(View.INVISIBLE);
                Gshow.setVisibility(View.INVISIBLE);
                Yshow.setVisibility(View.INVISIBLE);
                Bshow.setVisibility(View.INVISIBLE);
                player=p;
                switch(p){
                    case 1:Rshow.setVisibility(View.VISIBLE);redLight.setChecked(true);break;
                    case 2:Gshow.setVisibility(View.VISIBLE);greenLight.setChecked(true);break;
                    case 3:Yshow.setVisibility(View.VISIBLE);yellowLight.setChecked(true);break;
                    case 4:Bshow.setVisibility(View.VISIBLE);blueLight.setChecked(true);break;
                }
            }
        });
    }
    public void SetNicks(final String nick){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                //NickClock=true;
                //if(!NickClock)
                //    Nicker.run();
                String newnick=nick.substring(15);
                if(nick.contains("Player1")){
                    TextView Rnick=(TextView)findViewById(R.id.Rnick);
                    Rnick.setText(newnick);
                    Rnick.setVisibility(View.VISIBLE);
                }
                if(nick.contains("Player2")){
                    TextView Gnick=(TextView)findViewById(R.id.Gnick);
                    Gnick.setText(newnick);
                    Gnick.setVisibility(View.VISIBLE);
                }
                if(nick.contains("Player3")){
                    TextView Ynick=(TextView)findViewById(R.id.Ynick);
                    Ynick.setText(newnick);
                    Ynick.setVisibility(View.VISIBLE);
                }
                if(nick.contains("Player4")){
                    TextView Bnick=(TextView)findViewById(R.id.Bnick);
                    Bnick.setText(newnick);
                    Bnick.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void ConnectionReset(final int extra){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                Started = false;
                //NickClock=false;
                //ehandler.removeCallbacks(Nicker);
                TextView Sen=(TextView)findViewById(R.id.textView);
                switch (extra){
                    case 0:Sen.setText("YOU FOOL \nMade by Danelc\nPlease choose a room to join:");break;
                    case 1:Sen.setText("looks like that room was full.\ntry another:");break;
                    case 2:Sen.setText("looks like that room was closing.\ntry another:");break;
                    case 3:Sen.setText("looks like that room was taken.\ntry another:");break;
                    case 4:Sen.setText("Not enough players left to continue game.\nthanks for playing!");break;
                }
                EditText text = (EditText)findViewById(R.id.editText) ;
                text.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                Button suggest= (Button)findViewById(R.id.BSuggest) ;
                suggest.setVisibility(View.GONE);
                FloatingActionButton send = (FloatingActionButton)findViewById(R.id.floatingActionButton);
                send.setVisibility(View.GONE);
                TextView Rnick =(TextView)findViewById(R.id.Rnick);
                TextView Gnick =(TextView)findViewById(R.id.Gnick);
                TextView Ynick =(TextView)findViewById(R.id.Ynick);
                TextView Bnick =(TextView)findViewById(R.id.Bnick);
                Rnick.setText("");
                Gnick.setText("");
                Ynick.setText("");
                Bnick.setText("");
                CheckLight(-1);
                CheckLight(-2);
                CheckLight(-3);
                CheckLight(-4);
                TextView Rpoints = (TextView)findViewById(R.id.Rshow);
                TextView Gpoints = (TextView)findViewById(R.id.Gshow);
                TextView Ypoints = (TextView)findViewById(R.id.Yshow);
                TextView Bpoints = (TextView)findViewById(R.id.Bshow);
                Rpoints.setVisibility(View.INVISIBLE);
                Gpoints.setVisibility(View.INVISIBLE);
                Ypoints.setVisibility(View.INVISIBLE);
                Bpoints.setVisibility(View.INVISIBLE);
                TextView RScore = (TextView)findViewById(R.id.ScoreRed);
                TextView GScore = (TextView)findViewById(R.id.ScoreGreen);
                TextView YScore = (TextView)findViewById(R.id.ScoreYellow);
                TextView BScore = (TextView)findViewById(R.id.ScoreBlue);//making the scores show 0 just in case
                RScore.setText("0");
                GScore.setText("0");
                YScore.setText("0");
                BScore.setText("0");
                for(int count=0;count<4;count++)//reseting the scores
                    Score[count]=0;
                Button b1 =(Button)findViewById(R.id.BLie1);
                Button b2 =(Button)findViewById(R.id.BLie2);
                Button b3 =(Button)findViewById(R.id.BLie3);
                Button b4 =(Button)findViewById(R.id.BLie4);
                Button b5 =(Button)findViewById(R.id.BLie5);
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.INVISIBLE);
                b3.setVisibility(View.INVISIBLE);
                b4.setVisibility(View.INVISIBLE);
                b5.setVisibility(View.INVISIBLE);
                FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab) ;
                fab.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                EditText edit =(EditText)findViewById(R.id.editText);
                edit.setVisibility(View.VISIBLE);
                edit.setText(Nick);
                edit.setHint("Enter Nickname(MAX 12 characters)");
                MenuItem mitem = menus.findItem(R.id.action_start);
                mitem.setVisible(true);
                LowVersion=false;
                invalidateOptionsMenu();
            }
        });
    }
    public void setPlaying(final String mess){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
                String ms;
                TextView bscore =(TextView)findViewById(R.id.ScoreBlue);
                TextView yscore =(TextView)findViewById(R.id.ScoreYellow);
                ms=mess.substring(8);
                switch (ms){
                    case "-1":playing--;setPlaying("Playing:"+playing);break;
                    case "1":playing=1;ConnectionReset(2);break;
                    case "2":playing=2;yellowLight.setVisibility(View.INVISIBLE);blueLight.setVisibility(View.INVISIBLE);bscore.setVisibility(View.INVISIBLE);yscore.setVisibility(View.INVISIBLE);CheckLight(3);CheckLight(4);break;
                    case "3":playing=3;blueLight.setVisibility(View.INVISIBLE);yellowLight.setVisibility(View.VISIBLE);bscore.setVisibility(View.GONE);yscore.setVisibility(View.VISIBLE);CheckLight(4);break;
                    case "4":playing=4;yellowLight.setVisibility(View.VISIBLE);blueLight.setVisibility(View.VISIBLE);bscore.setVisibility(View.VISIBLE);yscore.setVisibility(View.VISIBLE);break;

                }
            }
        });
    }
    public void setLowVersion(){
        final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
            LowVersion=true;
            }
        });
    }
    @Override
    protected void onDestroy()
    {
        if(Started)
        {
            bot.sendMessage("#"+Channel,"player"+player+" Disconnected.");
            bot.dispose();
        }
        super.onDestroy();
    }
    private void ChangeSpaceWidth()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        View rel = findViewById(R.id.content_main);
        width=(width-(redLight.getWidth()*4+rel.getPaddingRight()*2))/3;
        Space space1=(Space)findViewById(R.id.space1);
        Space space2=(Space)findViewById(R.id.space2);
        Space space3=(Space)findViewById(R.id.space3);
        space1.requestLayout();
        space2.requestLayout();
        space3.requestLayout();
        space1.getLayoutParams().width=width;
        space2.getLayoutParams().width=width;
        space3.getLayoutParams().width=width;
        FloatingActionButton send=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        send.requestLayout();
        send.getLayoutParams().width=send.getHeight();

        //space1.setLayoutParams(new RelativeLayout.LayoutParams(width,16));
        //space2.setLayoutParams(new RelativeLayout.LayoutParams(width,16));
        //space3.setLayoutParams(new RelativeLayout.LayoutParams(width,16));
    }
    public void addSendButton()
    { final Handler handler = new Handler(getMainLooper());
        boolean post = handler.post(new Runnable() {
            @Override
            public void run() {
        FloatingActionButton send = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        send.setVisibility(View.VISIBLE);
            }
        });
    }
}

