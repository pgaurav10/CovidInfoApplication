package com.example.covidinfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.covidinfo.api.ApiUtilities;
import com.example.covidinfo.api.CountryData;
import com.google.android.material.navigation.NavigationView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView totalConfirm,totalRecover,totalActive,totalDeath,totalTest;
    TextView todayConfirm,todayRecover,todayDeath;
    TextView dateTv;
    PieChart pieChart;
    TextView countryName;
    List<CountryData> list;
    String country = "India";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();

        list = new ArrayList<>();
        if (getIntent().getStringExtra("country")!=null) {
            country = getIntent().getStringExtra("country");
        }
        init();

        countryName.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CountryActivity.class)));

        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                
                return false;
            }
        });

        ApiUtilities.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>() {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                        list.addAll(response.body());

                        for(int i=0;i<list.size();i++) {
                            if(list.get(i).getCountry().equals(country)) {

                                int confirm = Integer.parseInt(list.get(i).getCases());
                                int active = Integer.parseInt(list.get(i).getActive());
                                int recover = Integer.parseInt(list.get(i).getRecovered());
                                int death = Integer.parseInt(list.get(i).getDeaths());

                                countryName.setText(country);
                                totalConfirm.setText(NumberFormat.getInstance().format(confirm).toString());
                                totalActive.setText(NumberFormat.getInstance().format(active).toString());
                                totalRecover.setText(NumberFormat.getInstance().format(recover).toString());
                                totalDeath.setText(NumberFormat.getInstance().format(death).toString());

                                todayConfirm.setText("(+"+NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())).toString()+")");
                                todayRecover.setText("(+"+NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())).toString()+")");
                                todayDeath.setText("(+"+NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())).toString()+")");
                                totalTest.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())).toString());

                                setTime(list.get(i).getUpdated());

                                pieChart.addPieSlice(new PieModel("Confirm", confirm, Color.parseColor("#FBC233")));
                                pieChart.addPieSlice(new PieModel("Active", active, Color.parseColor("#78DBF3")));
                                pieChart.addPieSlice(new PieModel("Recover", recover, Color.parseColor("#7EC544")));
                                pieChart.addPieSlice(new PieModel("Death", death, Color.parseColor("#F6404F")));

                                pieChart.startAnimation();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {
                        Toast.makeText(MainActivity.this,
                                "Error fetching data: " +t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setTime(String updated) {

        DateFormat format = new SimpleDateFormat("MMM dd,yyyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        dateTv.setText("Updated at "+ format.format(calendar.getTime()));
    }
    private void init() {
        totalConfirm = findViewById(R.id.totalConfirmed);
        totalRecover = findViewById(R.id.totalRecovered);
        totalActive = findViewById(R.id.totalActive);
        totalDeath = findViewById(R.id.totalDeath);
        totalTest = findViewById(R.id.totalTests);
        todayConfirm = findViewById(R.id.todayConfirmed);
        todayRecover = findViewById(R.id.todayRecovered);
        todayDeath = findViewById(R.id.todayDeath);
        pieChart = findViewById(R.id.piechart);
        dateTv = findViewById(R.id.dateTV);
        countryName = findViewById(R.id.cname);
    }
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
}
