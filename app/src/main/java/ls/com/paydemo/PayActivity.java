package ls.com.paydemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microquation.linkedme.android.LinkedME;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by danny on 21/07/2017.
 */

public class PayActivity extends Activity  implements View.OnClickListener{

    private static final String TAG = "PayActivity";

    public static PayActivity mInstance;

    private Button payBtn;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            Toast.makeText(PayActivity.this, "订单号: " + msg.obj.toString().split(":")[1], Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payBtn = (Button) findViewById(R.id.payBtn);
        payBtn.setOnClickListener(this);

        payBtn.setText("点我开始支付");

        if (mInstance == null) {
            mInstance = PayActivity.this;
        }

        Intent intent = getIntent();
        Uri uri = intent.getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        Bundle extras = intent.getExtras();

        Uri uri = intent.getData();

        if (uri != null) {
            Set<String> params = uri.getQueryParameterNames();
            Iterator<String> iter = params.iterator();
            StringBuilder sb = new StringBuilder();
            while (iter.hasNext()) {
                String param = iter.next();
                sb.append(param + ":" + uri.getQueryParameter(param) + "\n");
            }

            Message msg = mHandler.obtainMessage();
            msg.obj = sb.toString();
            msg.sendToTarget();
        }

        if (intent.getStringExtra("trx_id") != null) {
            Message msg = mHandler.obtainMessage();
            msg.obj = intent.getStringExtra("trx_id");
            Log.e(TAG, "trx_id: " + intent.getStringExtra("trx_id"));
            msg.sendToTarget();
        }

        if (extras != null) {
            String trx_id = extras.getString("trx_id");
            if (trx_id != null) {
                Message msg = mHandler.obtainMessage();
                msg.obj = trx_id;
                msg.sendToTarget();
            }
        }
    }

    @Override
    public void onClick(View v) {
        String appKey = "TZYJ9LQUYYXG087";
        String appSecret = "TZYJ9LQUYYXG087";

        String trx_id = String.format("%s%s", appKey, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

        Map<String, String> fields = new HashMap<String, String>();
        fields.put("appKey", appKey);
        fields.put("trx_id", trx_id);
        fields.put("totalAmount", "0.01");

        String sign = SignUtil.getLsPaySign(fields, appSecret);

        final String payUrl = String.format("http://ls.pullmi.cn/wechat_quick_pay_ticket?appKey=%s&trx_id=%s&totalAmount=0.01&sign=%s", appKey, trx_id, sign);

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(payUrl);

                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.connect();

                    String line = null;
                    StringBuilder respBuilder = new StringBuilder();

                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                    while ((line = br.readLine()) != null) {
                        respBuilder.append(line);
                    }

                    Log.e("Main", "--------> " + respBuilder.toString());

                    Uri uri = Uri.parse(respBuilder.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
