package de.uniulm.bagception.notification.usernotificationservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import de.uniulm.bagception.notification.usernotificationservice.fragments.BaseResponseAnswerFragment;
import de.uniulm.bagception.notification.usernotificationservice.fragments.impl.ConfirmConnectionFragment;
import de.uniulm.bagception.protocol.bundle.constants.Response;
import de.uniulm.bagception.protocol.bundle.constants.ResponseAnswerListener;

public class NotificationReceiver extends Activity implements ResponseAnswerListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_receiver);
		
		//this method is called by a touch on the notificationbar
		//the notification knows which content to display
		
		//first we need to create the correct fragment
		
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        
        // if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        //find out which fragment to display
        BaseResponseAnswerFragment f = getAppropriateFragment(getIntent().getExtras());
        
        // pass the payload to the fragment (e.g. BluetoothDevice)
        //TODO
        f.setArguments(getIntent().getExtras());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, f).commit();
        
		
	}

	/**
	 * Finds out which Response was given
	 * @param b the ResponseBundle
	 * @return the appropriate Fragment
	 */
	private BaseResponseAnswerFragment getAppropriateFragment(Bundle b){
		//TODO
		Response r = Response.getResponse(b);
		BaseResponseAnswerFragment ret = null;
		switch(r){
		case Ask_For_Specific_Device:
			ret  = new ConfirmConnectionFragment(); //TODO change
			break;
			
		case Confirm_Established_Connection:
			ret  = new ConfirmConnectionFragment(); 
			break;
		}
		
        
		return ret;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_receiver, menu);
		return true;
	}

	@Override
	public void onResponseAnswerGiven(Bundle responseAnswer) {
		Intent serviceIntent = new Intent(this,NotificationService.class);
		serviceIntent.putExtras(responseAnswer);
		startService(serviceIntent);
		finish();
	}

	
}
