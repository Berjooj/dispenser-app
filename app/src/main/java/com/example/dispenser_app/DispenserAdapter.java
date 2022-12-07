package com.example.dispenser_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class DispenserAdapter extends RecyclerView.Adapter<DispenserAdapter.DispenserHolder> {

    private Context context;
    private List<Dispenser> dispenserList;
    private AlertDialog.Builder alert;
    private ProgressDialog progress;
    private RequestQueue requestQueue;

    public DispenserAdapter(Context context, List<Dispenser> dispensers, AlertDialog.Builder alert, ProgressDialog progress, RequestQueue requestQueue) {
        this.dispenserList = dispensers;
        this.context = context;
        this.alert = alert;
        this.progress = progress;
        this.requestQueue = requestQueue;
    }

    @NonNull
    @Override
    public DispenserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new DispenserHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DispenserHolder holder, int position) {
        NumberFormat formatter = new DecimalFormat("0.00");

        Dispenser dispenser = dispenserList.get(position);

        holder.address.setText(dispenser.getAddress().toString() + " " + dispenser.getComplement().toString());
        holder.currentCapacity.setText(formatter.format(dispenser.getCurrentCapacity()) + "/" + formatter.format(dispenser.getCapacity()));
        holder.token.setText("#" + dispenser.getToken().toString().substring(0, 20) + "...");

        holder.reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.setTitle("Confirmação");
                alert.setMessage("Deseja confirmar a recarga de álcool em gel do dispenser?");
                alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progress.show();

                        try {
                            JSONObject jsonBody = new JSONObject();

                            jsonBody.put("token", dispenser.getToken());
                            jsonBody.put("type", 2);
                            jsonBody.put("uses", 0);

                            String url = "https://dispenser.berjooj.com/api/dispenser";

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    jsonBody,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            ScrollingActivity.fetchDispensers();

                                            progress.dismiss();

                                            alert.setTitle("Sucesso!");
                                            alert.setMessage("Dispenser recarregado com sucesso.");

                                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            alert.setNegativeButton(null, null);
                                            alert.show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progress.dismiss();

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dispenserList.size();
    }

    public static class DispenserHolder extends RecyclerView.ViewHolder {

        TextView token, currentCapacity, address;
        Button reloadButton;

        public DispenserHolder(@NonNull View itemView) {
            super(itemView);

            token = itemView.findViewById(R.id.token);
            currentCapacity = itemView.findViewById(R.id.current_capacity);
            address = itemView.findViewById(R.id.address);
            reloadButton = itemView.findViewById(R.id.reloadButton);
        }
    }
}
