package com.wadis.p2pridesharing.login;

import androidx.appcompat.app.AppCompatActivity;
import com.wadis.p2pridesharing.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectionActivity extends AppCompatActivity {

    private Button mDriver, mCustomer;
    public String selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        mDriver=(Button) findViewById(R.id.driver);
        mCustomer=(Button) findViewById(R.id.customer);
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selection = "Driver";
                Intent intent=new Intent(SelectionActivity.this,DriverLoginActivity.class);
                //intent.putExtra("selection", selection);
                startActivity(intent);
                finish();
                return;

            }
        });
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selection = "Customer";

                Intent intent=new Intent(SelectionActivity.this,CustomerLoginActivity.class);
                //intent.putExtra("selection", selection);
                startActivity(intent);
                finish();
                return;

            }
        });
    }
}