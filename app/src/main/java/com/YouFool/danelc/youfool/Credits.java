package com.YouFool.danelc.youfool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        Intent intent=getIntent();
        TextView credits=(TextView)findViewById(R.id.textView2);
        if(intent.getBooleanExtra("lowVer",false))
            credits.setText("Developer:\n   Danelc\n Credits:\n   irc.Freenode.com\n   Pircbot(library for IRC)\n   Kahoot and spyfall(for the idea)\nVersion:"+intent.getStringExtra("Version")+"\n WARNING YOU HAVE A LOW VERSION, PLEASE UPDATE!");
        else
            credits.setText("Developer:\n   Danelc\n Credits:\n   irc.Freenode.com\n   Pircbot(library for IRC)\n   Kahoot and spyfall(for the idea)\nVersion:"+intent.getStringExtra("Version"));

    }
}
