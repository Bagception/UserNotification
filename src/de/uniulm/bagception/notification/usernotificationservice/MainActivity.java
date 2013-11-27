package de.uniulm.bagception.notification.usernotificationservice;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.philipphock.android.lib.services.ServiceUtil;
import de.uniulm.bagception.bluetoothclientmessengercommunication.service.BundleMessageHelper;
import de.uniulm.bagception.bluetoothservermessengercommunication.activities.ServiceObserverActivity;
import de.uniulm.bagception.protocol.bundle.constants.Command;

public class MainActivity extends ServiceObserverActivity {



	

	@Override
	protected String getServiceName() {
		return "de.uniulm.bagception.notification.usernotificationservice.NotificationService";
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		BundleMessageHelper msgH = new BundleMessageHelper(this);
		msgH.sendCommandBundle(Command.POLL_ALL_RESPONSES.toBundle());
		finish();
		
	}
	
	
	public void onRequestNotificationsClicked(View v) {
		BundleMessageHelper msgH = new BundleMessageHelper(this);
		msgH.sendCommandBundle(Command.POLL_ALL_RESPONSES.toBundle());

	}
	
	
	@Override
	protected void onServiceStarted() {
		Button b =(Button) findViewById(R.id.startS);
		TextView status = (TextView) findViewById(R.id.connStatus);
		status.setText("connected");
		status.setTextColor(Color.GREEN);
		b.setText("stop service");
	}

	
	@Override
	public void onServiceStopped(String serviceName) {
		Button b =(Button) findViewById(R.id.startS);
		TextView status = (TextView) findViewById(R.id.connStatus);
		b.setText("start service");
		status.setText("disconnected");
		status.setTextColor(Color.RED);
		
	}

	@Override
	protected void on_service_not_installed() {
		Button b =(Button) findViewById(R.id.startS);
		TextView status = (TextView) findViewById(R.id.connStatus);
		status.setText("not installed");
		status.setTextColor(Color.BLUE);
		b.setText("start service");		
	}

	
	public void onStartSClicked(View v){
		
		on_service_start_stop_toggle_clicked();
		
	}



	public void showSClicked(View v) {
		
		ServiceUtil.logRunningServices(this, "###");

	}
	


	


}
