package sm.com.camcollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sm.com.camcollection.util.PrefManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }

}
