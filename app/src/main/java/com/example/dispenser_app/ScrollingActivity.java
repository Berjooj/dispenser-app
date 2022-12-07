package com.example.dispenser_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dispenser_app.databinding.ActivityScrollingBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static RequestQueue requestQueue;
    private static List<Dispenser> dispenserList;
    private static ActivityScrollingBinding binding;
    private static AlertDialog.Builder alert;
    private static ProgressDialog progress;
    private static SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        dispenserList = new ArrayList<>();
        alert = new AlertDialog.Builder(ScrollingActivity.this);

        progress = new ProgressDialog(this);
        progress.setTitle("Carregando");
        progress.setMessage("Aguarde o carregamento...");
        progress.setCancelable(false);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ScrollingActivity.fetchDispensers();
            }
        });

        fetchDispensers();
    }

    public static void fetchDispensers() {
        dispenserList.clear();

        String url = "https://dispenser.berjooj.com/api/dispenser/list/1";

        progress.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        swipeRefreshLayout.setRefreshing(false);

                        JSONArray dispensers = null;
                        try {
                            dispensers = response.getJSONArray("dispensers");

                            for (int i = 0; i < dispensers.length(); i++) {
                                JSONObject dispenserJSON = dispensers.getJSONObject(i);

                                Dispenser dispenser = new Dispenser(
                                        dispenserJSON.getString("token"),
                                        dispenserJSON.getString("address"),
                                        dispenserJSON.getString("complement"),
                                        (float) dispenserJSON.getDouble("current_capacity"),
                                        (float) dispenserJSON.getDouble("capacity"),
                                        dispenserJSON.getInt("id")
                                );

                                dispenserList.add(dispenser);
                            }

                        } catch (JSONException e) {
                            alert.setTitle("Erro!");
                            alert.setMessage(e.getMessage());

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alert.setNegativeButton(null, null);
                            alert.show();
                        }

                        DispenserAdapter adapter = new DispenserAdapter(binding.getRoot().getContext(), dispenserList, alert, progress, requestQueue);

                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        swipeRefreshLayout.setRefreshing(false);

                        alert.setTitle("Erro!");
                        alert.setMessage(error.getMessage());

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.setNegativeButton(null, null);
                        alert.show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}