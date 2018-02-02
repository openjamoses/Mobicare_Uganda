package date_time;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.john.mobicare_uganda.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by john on 2/1/18.
 */

public class Pick_Time2  extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    List<String> list = new ArrayList<>();

    public Pick_Time2(){

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // list.add();
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        LayoutInflater inflater = getActivity(). getLayoutInflater();
        View views = inflater.inflate(R.layout.schedule_dialog, null);
        Button pick_time = (Button) views.findViewById(R.id.pick_stop);
        pick_time.setText(view.getCurrentHour()+":"+view.getCurrentMinute());
    }
}