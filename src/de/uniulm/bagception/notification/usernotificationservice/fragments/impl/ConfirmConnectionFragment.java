package de.uniulm.bagception.notification.usernotificationservice.fragments.impl;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.uniulm.bagception.notification.usernotificationservice.R;
import de.uniulm.bagception.notification.usernotificationservice.fragments.BaseResponseAnswerFragment;
import de.uniulm.bagception.protocol.bundle.constants.Response;
import de.uniulm.bagception.protocol.bundle.constants.ResponseAnswer;

public class ConfirmConnectionFragment extends BaseResponseAnswerFragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.fragment_confirm_connection, null);
		
		v.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle b = ResponseAnswer.Confirm_Established_Connection.toBundle();
				b.putBoolean(ResponseAnswer.EXTRA_KEYS.PAYLOAD, true);
				ConfirmConnectionFragment.this.setAnswer(b);
			}
		});

		v.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle b = ResponseAnswer.Confirm_Established_Connection.toBundle();
				b.putBoolean(ResponseAnswer.EXTRA_KEYS.PAYLOAD, false);
				ConfirmConnectionFragment.this.setAnswer(b);
			}
		});

		TextView t = (TextView) v.findViewById(R.id.deviceName);
		BluetoothDevice device = getArguments().getParcelable(Response.EXTRA_KEYS.PAYLOAD);
		t.setText(device.getName());
		
		return v; 
	}
	
	
}
