package cs160.represent;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.Button;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sandeepsubramanian on 3/1/16.
 */
//http://stackoverflow.com/questions/28167473/loading-in-listview-loads-data-from-last-entry-into-all-rows
//http://stackoverflow.com/questions/12405575/using-a-listadapter-to-fill-a-linearlayout-inside-a-scrollview-layout
public class CongressionalActivity extends FragmentActivity {
    private static final String TAG = CongressionalActivity.class.getSimpleName();
    private String API_KEY = //Redacted;
    private String sunlight = "https://congress.api.sunlightfoundation.com/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cong);

        JSONObject currLocReps;
        try {
            currLocReps = new JSONObject(getIntent().getStringExtra("reps"));
            JSONArray reps = currLocReps.getJSONArray("results");
            LayoutInflater inflater = getLayoutInflater();
            LinearLayout main = (LinearLayout)findViewById(R.id.main_cong);

            for (int i = 0; i < reps.length(); i += 1) {
                JSONObject rep = reps.getJSONObject(i);
                final String repString = rep.toString();
                final String bgid = rep.getString("bioguide_id");
                String chamber = rep.getString("chamber");
                String party = rep.getString("party");
                String website = rep.getString("website");
                String email = rep.getString("oc_email");
                String first_name = rep.getString("first_name");
                String middle_name = rep.getString("middle_name");
                String last_name = rep.getString("last_name");
                if (middle_name == null || middle_name.equals("null")) {
                    middle_name = "";
                } else {
                    middle_name = middle_name + " ";
                }
                String name = first_name + " " + middle_name + last_name;
                String district = rep.getString("district");
                //Log.i(TAG, "This is the district: " + district);
                String state = rep.getString("state");
                if (district != null && !district.equals("null")) {
                    state = state + " D" + district;
                }
                String title = "";

                if (chamber.equals("senate")) {
                    title = "Sen. ";
                } else if (chamber.equals("house")) {
                    title = "Rep. ";
                }
                int color = R.color.white;
                switch (party) {
                    case "R":
                        color = R.color.primaryRed;
                        break;
                    case "D":
                        color = R.color.primaryBlue;
                        break;
                    case "I":
                        color = R.color.primaryGreen;
                        break;
                }

                View view = inflater.inflate(R.layout.cong, null);
                TextView em = (TextView) view.findViewById(R.id.email);
                em.setText(email);
                TextView web = (TextView) view.findViewById(R.id.website);
                web.setText(website);
                TextView tn = (TextView) view.findViewById(R.id.name);
                tn.setText(title + name);
                TextView pd = (TextView) view.findViewById(R.id.partydistrict);
                pd.setText(party + " - " + state);
                Button bt = (Button) view.findViewById(R.id.bgid);
                bt.setTag(bgid);
                bt.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        getDetailedView(bgid, repString);
                    }
                });
                LinearLayout repll = (LinearLayout) view.findViewById(R.id.rep);
                repll.setBackgroundColor(getResources().getColor(color));

                main.addView(view);
            }
        } catch (Exception e) {

        }

    }

    private void getDetailedView(String bgid, String repString) {
        try {
            String committees = new JsonAPI().execute(sunlight + "committees?member_ids=" +
                    bgid + "&apikey=" + API_KEY).get();
            String bills = new JsonAPI().execute(sunlight + "bills?sponsor_id=" +
                    bgid + "&apikey=" + API_KEY).get();
            if (committees != null && bills != null) {
                Intent intent = new Intent(getApplicationContext(), DetailedActivity.class);
                intent.putExtra("committees", committees);
                intent.putExtra("bills", bills);
                intent.putExtra("rep", repString);
                startActivity(intent);
            } else {
                Log.i(TAG, "Null RESPONSE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class JsonAPI extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                URL url = new URL(params[0]);
                URLConnection urlConnection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                return bufferedReader.readLine();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
