package ls.com.paydemo;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.microquation.linkedme.android.LinkedME;

public class MainActivity extends Activity implements OnClickListener {

    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payButton = (Button) findViewById(R.id.payBtn);
        payButton.setOnClickListener(this);

        Log.e("MainActivity", "--------> deviceId: "+LinkedME.getInstance(this).getDeviceId());
    }

    @Override
    public void onClick(View v) {

        Intent payIntent = new Intent(this, PayActivity.class);

        startActivity(payIntent);

    }
}
