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
import android.widget.Toast;
import de.philipphock.android.lib.logging.LOG;
import de.uniulm.bagception.bluetoothservermessengercommunication.messenger.MessengerHelper;
import de.uniulm.bagception.bluetoothservermessengercommunication.messenger.MessengerHelperCallback;
import de.uniulm.bagception.protocol.bundle.constants.Command;
import de.uniulm.bagception.protocol.bundle.constants.Response;
import de.uniulm.bagception.protocol.bundle.constants.ResponseAnswer;
import de.uniulm.bagception.services.ServiceNames;

public class NotificationService extends Service implements
		MessengerHelperCallback {

	private MessengerHelper messengerHelper;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(getApplicationContext(), "Starting Service",
				Toast.LENGTH_SHORT).show();

		int ret = super.onStartCommand(intent, flags, startId);
		messengerHelper = new MessengerHelper(this,
				ServiceNames.BLUETOOTH_CLIENT_SERVICE);
		messengerHelper.register(this);
		return ret;
	}

	@Override
	public void onDestroy() {
		messengerHelper.unregister(this);
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
			Toast.makeText(getApplicationContext(), "Establish???",
					Toast.LENGTH_SHORT).show();
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

	@Override
	public void connectedWithRemoteService() {
		messengerHelper.sendCommandBundle(Command
				.getCommandBundle(Command.PING));

	}

	@Override
	public void disconnectedFromRemoteService() {
		// TODO Auto-generated method stub

	}

}
