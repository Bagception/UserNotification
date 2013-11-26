package de.uniulm.bagception.notification.usernotificationservice.fragments;

import de.philipphock.android.lib.logging.LOG;
import de.uniulm.bagception.notification.usernotificationservice.NotificationReceiver;
import de.uniulm.bagception.protocol.bundle.constants.ResponseAnswerListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class BaseResponseAnswerFragment extends Fragment{
	protected ResponseAnswerListener listener;
	
	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
	
	@Override
	public void onAttach(Activity activity) {
		listener = ((NotificationReceiver) activity);
		super.onAttach(activity);
	}
	
	protected void setAnswer(Bundle responseAnswer){
		if (listener == null){
			LOG.out(this, "####### ERROR, no listener attached");
			return;
		}
		listener.onResponseAnswerGiven(responseAnswer);
		
	}
}
