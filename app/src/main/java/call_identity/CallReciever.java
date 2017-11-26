package call_identity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by john on 11/21/17.
 */

public class CallReciever extends BroadcastReceiver {
    private Context context;
    private String dialphonenumber;
    private static final String TAG = "CallReciever";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        try {
            setResultData(null);
            dialphonenumber = getResultData();
            if (dialphonenumber == null) {
                dialphonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            }
            setResultData(dialphonenumber);
            callActionHandler.postDelayed(runRingingActivity, 850);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    Handler callActionHandler = new Handler();
    Runnable runRingingActivity = new Runnable()
    {
        @Override
        public void run()
        {

            Intent intentPhoneCall = new Intent(context, HideDialer.class);
            Log.e(TAG, "Call Number: "+dialphonenumber);
            intentPhoneCall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentPhoneCall.putExtra("incomingnumber",dialphonenumber);
            context.startActivity(intentPhoneCall);
        }
    };
}
