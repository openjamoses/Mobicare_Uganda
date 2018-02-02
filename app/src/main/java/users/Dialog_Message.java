package users;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.dbsyncing.Appoitments;

import server_connections.Doctor_Operations;

/**
 * Created by john on 1/22/18.
 */

public class Dialog_Message {
    private Context context;
    public Dialog_Message(Context context){
        this.context = context;
    }

    public void showDialog(final String header, final String body){
        // final int block_id = new Block_Operations(context).seletedID(number);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        //LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.thanks_dialog, null);
        dialog.setView(view);

        Button close_btn = (Button) view.findViewById(R.id.close_btn);

        final EditText editText = (EditText) view.findViewById(R.id.block_text);
        TextView titleText = (TextView) view.findViewById(R.id.titleText);
        TextView bodyText = (TextView) view.findViewById(R.id.msg_txt);
        titleText.setText(header);
        bodyText.setText(body);
        final AlertDialog alert = dialog.create();
        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               alert.dismiss();
            }
        });
    }
}
