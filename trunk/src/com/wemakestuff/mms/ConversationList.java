package com.wemakestuff.mms;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ConversationList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_list_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_list_screen, menu);
        return true;
    }
    
}
