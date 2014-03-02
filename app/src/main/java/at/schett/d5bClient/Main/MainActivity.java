package at.schett.d5bClient.Main;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import org.apache.http.client.methods.HttpGet;

import at.schett.d5bClient.Constants.RequestConstants;
import at.schett.d5bClient.Enums.RequestCode;
import at.schett.d5bClient.R;


public class MainActivity extends Activity {

    private Button btnScan;
    private TextView contentTxt;

    private String accountName;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            btnScan = (Button) findViewById(R.id.scan_button);
            contentTxt = (TextView) findViewById(R.id.scan_content);
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, RequestCode.AccountRequestCode);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void scanNow(View view) {
        if (view.getId() == R.id.scan_button) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, RequestCode.ScanRequestCode);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case RequestCode.ScanRequestCode:
                if (resultCode == RESULT_OK) {
                    String contents = intent.getStringExtra("SCAN_RESULT");
                    Log.i("xZing", "contents: " + contents);
                    // Handle successful scan
                    contentTxt.setText(contents);

                    String url = RequestConstants.ServerUrlTrack;
                    url = url.concat(Integer.toString(accountId)).concat("/").concat(contents);

                    HttpGet getReq = new HttpGet(url);

                } else if (resultCode == RESULT_CANCELED) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.i("xZing", "Cancelled");
                }
                break;

            case RequestCode.AccountRequestCode:
                if (resultCode == RESULT_OK) {
                    accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        accountId = accountName.hashCode();
                    } else {
                        accountId = 1;
                    }
                }
                break;

            default:
                Log.i("Default", "Switch did not found a match");
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
