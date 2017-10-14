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
    //private String GetSentence()
    //{
    //    Random rnd = new Random();
    //    switch (rnd.nextInt(45)){
    //        case 0:return "1)A spectator in an Illinois courtroom was sentenced to six months in jail for *YAWNING* during a trial.";
    //        case 1:return "2)In 2007, a woman with a rare disorder that causes her to be sexually attracted to inanimate objects married *THE EIFFEL TOWER*. ";
    //        case 2:return "3)During the 1990s, teachers in North Korea were, oddly enough, required to know how to *PLAY THE ACCORDION*.";
    //        case 3:return "4)A man in Milford, Iowa, got fired from his job after he used a *FORKLIFT* to dislodge his Twix bar from the office vending machine.";
    //        case 4:return "5)Shiro Cosmetics sells an eye shadow called “*NIC CAGE* Raking Leaves on a Brisk October Afternoon.”";
    //        case 5:return "6)In 2008, a couple was killed in South Africa while having sex on a *RAILROAD TRACK*.";
    //        case 6:return "7)Walter Arnold received the world’s first speeding ticket in 1896 for going *8* miles per hour.";
    //        case 7:return "8)Owning 55,000 of them, Ted Turner has the world’s largest private collection of *BISONS*.";
    //        case 8:return "9)A 2013 survey conducted by Playtex revealed that 8% of Canadians have had sex in a *CANOE*.";
    //        case 9:return "10)ROAD TRIP! When on the Hawaiian island of Molokai, be sure to visit the Hoolehua post office where you’re able to mail *COCONUTS* without using a package.";
    //        case 10:return "11)A man from Enniskillen, Northern Ireland was sentenced to three months in prison for a fire he started while trying to turn *HIS POOP* into gold.";
    //        case 11:return "12)In 2013 a zoo in China attempted to pass off a *LARGE DOG* as an African lion.";
    //        case 12:return "13)In 1988, George H. W. Bush celebrated Halloween by dressing up as *HIMSELF*.";
    //        case 13:return "14)Tashirojima is an island off of Japan that is completely overrun by *CATS*.";
    //        case 14:return "15)In 1967, a small town in Ecuador elected an inanimate object mayor. The elected mayor was a *FOOT POWDER*.";
    //        case 15:return "16)Although very unconventional, farmer William von Schneidau feeds his pigs *WEED*.";
    //        case 16:return "17)In a 2013 poll conducted by Public Policy Polling, 27% of voters thought *HIPSTERS* should be subjected to a special tax for being so annoying.";
    //        case 17:return "18)A study published in the journal Anthrozoo reported that cows produce 5% more milk when they are given *NAMES*.";
    //        case 18:return "19)Metrophilia is sexual arousal caused by *POETRY*.";
    //        case 19:return "20)The real name of the Internet’s famous Grumpy Cat is *TARTAR SAUCE*.";
    //        case 20:return "21)In 2013, Dell issued a recall after customers complained their laptops smelled like *CAT URINE*.";
    //        case 21:return "22)Tashirojima is an island off of Japan that is completely overrun by *CATS*.";
    //        case 22:return "23)Anatidaephobia is the fear that somewhere in the world a *DUCK* is watching you.";
    //        case 23:return "24)The Isle of Man flag depicts three *LEGS* interlinked together.";
    //        case 24:return "25)A sequel to Beetlejuice titled Beetlejuice Goes *Hawaiian* was written but never made.";
    //        case 25:return "26)Famed American poet Charles Bukowski’s tombstone is engraved with the words “Don’t *TRY*.”";
    //        case 26:return "27)An eight-foot-tall *LEGO MAN* washed up ashore a Florida beach in 2011.";
    //        case 27:return "28)Advanced Comfort Technology makes waterbeds for *COWS*.";
    //        case 28:return "29)In 1976, boxing legend Muhammad Ali released an educational children’s album titled “Ali and His Gang Vs. Mr. *TOOTH DECAY*.";
    //        case 29:return "30)Edward Smith from Yelm, Washington garnered media attention for his claim that he’s had sex with over 1,000 *CARS*.";
    //        case 30:return "31)In October of 2013, eight sixth-graders from a New York college prep school were hospitalized after someone released *AXE BODY SPRAY* in a classroom.";
    //        case 31:return "32)After an allergic reaction to steroids used to treat asthma, a 28-year-old woman started growing *FINGERNAILS* on her head instead of hair.";
    //        case 32:return "33)In a 1999 interview with US Weekly, actress Lucy Liu revealed that she once had sex with a *GHOST*.";
    //        case 33:return "34)The city of Olney, Illinois organizes an annual event in order to *COUNT* squirrels.";
    //        case 34:return "35)ROAD TRIP! While in Florence, New Jersey be sure to check out the auto repair shop which, according to its owner, has an operational toilet once owned by *HITLER*.";
    //        case 35:return "36)Amerigo Vespucci, the man for whom America was named, was a *PICKLE* dealer.";
    //        case 36:return "37)Edgar Valdez Villarreal, a notorious Mexican drug cartel leader, had the not-so-scary nickname “La *BARBIE*.”";
    //        case 37:return "38)A group known as the “Robin Hooders” in Keene, New Hampshire pay for other people’s *PARKING METERS*.";
    //        case 38:return "39)A man in western Pennsylvania got a “Driving Under Influence” ticket for having an open can of beer while riding a *LAWN MOWER*.";
    //        case 39:return "40)A beaver’s anal secretions can be used as a *VANILLA* substitute in some foods.";
    //        case 40:return "41)According to the International Code of Disease, the code Y92250 is the code for “when a patient is injured in an *ART GALLERY*.”";
    //        case 41:return "42)In 2013, a dad in China hired pro *GAMERS* to kill his son, so the son would start looking for a job and get a life.";
    //        case 42:return "43)In India you can buy a cola named Gau Jal that includes the bizarre ingredient *COW URINE*.";
    //        case 43:return "44)On January 13, 2014, U.S. Secretary of State John Kerry presented to Russian Foreign Minister Sergei Lavrov the odd gift of two very large *POTATOES*.";
    //        case 44:return "45)The inventor of the chocolate chip cookie traded her recipe to Nestlé in exchange for a *LIFE TIME SUPPLY OF CHOCOLATE*.";
    //    }
    //   return "ERROR, Something went wrong sorry.Please tell the developer this message.";}

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
