package de.uniulm.bagception.notification.usernotificationservice;

import de.philipphock.android.lib.logging.LOG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		LOG.out(this, "start service bc");
		Intent startServiceIntent = new Intent(arg0, NotificationService.class);
		arg0.startService(startServiceIntent);
//		if (!ServiceUtil.isServiceRunning(arg0, NotificationService.class)){
//			
//		}
		
	}

}
