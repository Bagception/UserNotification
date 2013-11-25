package de.uniulm.bagception.notification.usernotificationservice;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import de.philipphock.android.lib.logging.LOG;
import de.philipphock.android.lib.services.observation.ConstantFactory;
import de.philipphock.android.lib.services.observation.ObservableService;
import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.bluetoothservermessengercommunication.messenger.MessengerHelper;
import de.uniulm.bagception.bluetoothservermessengercommunication.messenger.MessengerHelperCallback;
import de.uniulm.bagception.protocol.bundle.constants.Command;
import de.uniulm.bagception.protocol.bundle.constants.Response;
import de.uniulm.bagception.protocol.bundle.constants.ResponseAnswer;
import de.uniulm.bagception.services.ServiceNames;

public class NotificationService extends ObservableService implements
		MessengerHelperCallback,ServiceObservationReactor {

	private ServiceObservationActor soActor;
	private MessengerHelper messengerHelper;
	
	private boolean isDead=true;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(getApplicationContext(), "start service",
				Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, Service.START_NOT_STICKY);
	}
	

	
	@Override
	public void onFirstInit(){
		isDead = false;
		
		messengerHelper = new MessengerHelper(this,
				ServiceNames.BLUETOOTH_CLIENT_SERVICE);
		messengerHelper.register(this);
		
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


	}
	
		
	@Override
	public void onDestroy() {
		isDead = true;
		if (messengerHelper != null)
			messengerHelper.unregister(this);
		
		if (soActor!=null)
			soActor.unregister(this);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}

	// MessengerHelperCallback

	@Override
	public void onBundleMessage(Bundle b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponseMessage(Bundle b) {
		Response r = Response.getResponse(b);
		
		switch (r) {
		case Ask_For_Specific_Device:
			Toast.makeText(getApplicationContext(), "DEVICES...???",
					Toast.LENGTH_SHORT).show();
			break;

		case Confirm_Established_Connection:
			BluetoothDevice d = (BluetoothDevice) b.getParcelable(Response.EXTRA_KEYS.PAYLOAD);
			Bundle answer = ResponseAnswer
					.getResponseAnswerBundle(ResponseAnswer.Confirm_Established_Connection);
			/*answer.putBoolean(ResponseAnswer.EXTRA_KEYS.PAYLOAD, true);
			messengerHelper.sendResponseBundle(answer);*/
			showNotification(d);

			break;
		default:
			break;
		}

	}

	private void showNotification(final BluetoothDevice d) {
		if (isDead()){
			return;
		}
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Notification mBuilder = new Notification.Builder(
				this).setSmallIcon(R.drawable.shoppingbag)
				.setContentTitle("Bagception")
				.setContentIntent(pIntent)
				.setContentText("Es wurde die Tasche \"" + d.getName() + "\" gefunden").build();
		
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

	/**
	 * called when the connection to the bluetooth middleware is established 
	 */
	@Override
	public void connectedWithRemoteService() {
		//when we reconnect with the bluetoothMiddleware, we ask if the btclient is connected
		messengerHelper.sendCommandBundle(Command.getCommandBundle(Command.RESEND_STATUS));
		
		messengerHelper.sendCommandBundle(Command.POLL_ALL_RESPONSES.toBundle());
		
		
	}

	/**
	 * called when the connection to the bluetooth middleware is disconnected 
	 */
	@Override
	public void disconnectedFromRemoteService() {
		//restart middleware and reinit self if the connection to the middleware breaks up
		startService(new Intent(ServiceNames.BLUETOOTH_CLIENT_SERVICE));
		onFirstInit();
		
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

	
	
	
}
