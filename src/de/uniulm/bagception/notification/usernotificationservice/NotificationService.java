package de.uniulm.bagception.notification.usernotificationservice;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import de.philipphock.android.lib.logging.LOG;
import de.philipphock.android.lib.services.observation.ConstantFactory;
import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.bluetoothclientmessengercommunication.actor.BundleMessageActor;
import de.uniulm.bagception.bluetoothclientmessengercommunication.actor.BundleMessageReactor;
import de.uniulm.bagception.bluetoothclientmessengercommunication.service.BundleMessengerService;
import de.uniulm.bagception.protocol.bundle.constants.Command;
import de.uniulm.bagception.protocol.bundle.constants.Response;
import de.uniulm.bagception.protocol.bundle.constants.ResponseAnswer;
import de.uniulm.bagception.services.ServiceNames;

public class NotificationService extends BundleMessengerService implements
		ServiceObservationReactor,BundleMessageReactor {

	private ServiceObservationActor soActor;
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent != null  && intent.getExtras() != null){
			ResponseAnswer answer = ResponseAnswer.getResponseAnswer(intent.getExtras());
			if (answer != ResponseAnswer.NOT_AN_RESPONSE_ANSWER){
				//startService called from notificationReceiver
				//pass through middleware
				//TODO DELETE ALL
			}
		}
		 
		 
		return super.onStartCommand(intent, flags, Service.START_NOT_STICKY);
	}
	

	
	@Override
	public void onFirstInit(){		
		super.onFirstInit();
		//serviceObservation, with this, we can detect if the middleware is running
		onServiceStopped(null); //here we pretend that the service has stopped. If it has getForceResendStatusString will have to effect and the service is stopped, if it has getForceResendStatusString will trigger onServiceStarted
		
		
		soActor = new ServiceObservationActor(this,ServiceNames.BLUETOOTH_CLIENT_SERVICE);
		soActor.register(this);
		Intent broadcastRequest = new Intent();
		//broadcast answer is handled by ServiceObservationReactor
		//with this, we foce the BluetoothMiddleware to resent if it is alive
		broadcastRequest
				.setAction(ConstantFactory 
						.getForceResendStatusString(ServiceNames.BLUETOOTH_CLIENT_SERVICE));  
		sendBroadcast(broadcastRequest);
		//when we reconnect with the bluetoothMiddleware, we ask if the btclient is connected
		bmHelper.sendCommandBundle(Command.getCommandBundle(Command.RESEND_STATUS));
		bmHelper.sendCommandBundle(Command.POLL_ALL_RESPONSES.toBundle());

	}
	
		
	@Override
	public void onDestroy() {
		if (soActor!=null)
			soActor.unregister(this);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}



	@Override
	public void onResponseMessage(Bundle b) {
		
		
		if (isDead()){
			return;
		}
		Response r = Response.getResponse(b);
		LOG.out(this, "response message:"+r.getResponseCode()+ " "+r.name());
		Bundle ack=null;
		switch (r) {
		case Ask_For_Specific_Device:
			ack = ResponseAnswer.Ask_For_Specific_Device.getACK();
			ArrayList<BluetoothDevice> ds =  b.getParcelableArrayList(Response.EXTRA_KEYS.PAYLOAD);
			showNotification(ds,b);
			break;

		case Confirm_Established_Connection:
			ack = ResponseAnswer.Confirm_Established_Connection.getACK();
			BluetoothDevice d = (BluetoothDevice) b.getParcelable(Response.EXTRA_KEYS.PAYLOAD);
			showNotification(d,b);

			break;
		default:
			break;
		}
		
		//send ack = tells the service that we handled the message and it does not have to retransmit it ever again
		if (ack != null)
			bmHelper.sendResponseAnswerBundle(ack);
		

	}

	
	private void showNotification(final List<BluetoothDevice> d,Bundle response) {
		 showNotification("Bagception", "Es wurden " +d.size()+ " Taschen gefunden",response);
	}
	
	private void showNotification(final BluetoothDevice d,Bundle response) {
		 showNotification("Bagception", "Es wurde die Tasche \"" + d.getName() + "\" gefunden",response);
	}
	
	private void showNotification(String title,String msg, Bundle response){
		if (isDead()){
			return;
		}
		Intent intent = new Intent(this, NotificationReceiver.class);
		intent.putExtras(response);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Notification mBuilder = new Notification.Builder(
				this).setSmallIcon(R.drawable.shoppingbag)
				.setContentTitle(title)
				.setContentIntent(pIntent)
				.setContentText(msg).build();
		
		notificationManager.notify(0, mBuilder);
	}

		
	/**
	 * Response r = Response.getResponse(b); switch (r) { case
	 * Confirm_Established_Connection: Toast.makeText(this, "establish?",
	 * Toast.LENGTH_SHORT).show(); Bundle answer =
	 * ResponseAnswer.getResponseAnswerBundle
	 * (ResponseAnswer.Confirm_Established_Connection);
	 * answer.putBoolean(ResponseAnswer.EXTRA_KEYS.PAYLOAD, true);
	 * messengerHelper.sendResponseBundle(answer);
	 * 
	 * break;
	 * 
	 * default: break; }
	 */

	@Override
	public void onStatusMessage(Bundle b) {

	}

	@Override
	public void onCommandMessage(Bundle b) {
		Command c = Command.getCommand(b);
		if (c == Command.PONG) {
			Toast.makeText(getApplicationContext(), "PONG: Service",
					Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public void onError(Exception e) {
		// TODO Auto-generated method stub

	}



	//soActor callbacks
	
	@Override
	public void onServiceStarted(String serviceName) {
		//the middleware service is started
	}

	@Override
	public void onServiceStopped(String serviceName) {
		if (serviceName == null){
			//called at startup by this
		}
	}



	@Override
	public void onResponseAnswerMessage(Bundle b) {
		//nothing todo here
	}



	@Override
	public void onBundleMessageRecv(Bundle b) {
		//nothing todo here, we do not care about bundle messages at all
	}



	@Override
	public void onBundleMessageSend(Bundle b) {
		//nothing todo here, we do not care about bundle messages at all
	}


	
	
	
}
