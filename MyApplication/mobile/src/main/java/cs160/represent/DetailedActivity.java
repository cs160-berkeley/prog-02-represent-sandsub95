package cs160.represent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sandeepsubramanian on 3/1/16.
 */
public class DetailedActivity extends FragmentActivity {
    private static final String TAG = CongressionalActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        JSONObject rep;
        JSONObject commsCont;
        JSONObject billsCont;
        try {
            rep = new JSONObject(getIntent().getStringExtra("rep"));
            commsCont = new JSONObject(getIntent().getStringExtra("committees"));
            JSONArray comms = commsCont.getJSONArray("results");
            billsCont = new JSONObject(getIntent().getStringExtra("bills"));
            JSONArray bills = billsCont.getJSONArray("results");
            LayoutInflater inflater = getLayoutInflater();
            ScrollView main = (ScrollView)findViewById(R.id.detailed);

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

            String term_end = rep.getString("term_end");
            String twit = rep.getString("twitter_id");

            String district = rep.getString("district");
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

            main.setBackgroundColor(getResources().getColor(color));

            TextView detemail = (TextView) main.findViewById(R.id.detemail);
            detemail.setText(email);
            TextView detweb = (TextView) main.findViewById(R.id.detwebsite);
            detweb.setText(website);
            TextView tn = (TextView) main.findViewById(R.id.title);
            tn.setText(title + name);
            TextView pd = (TextView) main.findViewById(R.id.partydist);
            pd.setText(party + " - " + state);
            TextView te = (TextView) main.findViewById(R.id.termend);
            te.setText(term_end);
            TextView th = (TextView) main.findViewById(R.id.twitterhandle);
            th.setText(twit);

            String committees = "";
            TextView comm = (TextView) main.findViewById(R.id.committees);

            for (int i = 0; i < comms.length(); i += 1) {
                JSONObject commObj = comms.getJSONObject(i);
                committees = committees + commObj.getString("name") + "\n";
            }

            comm.setText(committees);
            LinearLayout detll = (LinearLayout) main.findViewById(R.id.detailedll);

            //Will give the 11 most recent bills without "null" as short name.
            int added = 0;
            for (int i = 0; i < bills.length() && added < 11; i += 1) {

                JSONObject bill = bills.getJSONObject(i);
                String short_name = bill.getString("short_title");
                if (!short_name.equals("null")) {
                    String date = bill.getString("introduced_on");
                    View view = inflater.inflate(R.layout.bills, null);
                    TextView billdate = (TextView) view.findViewById(R.id.billdate);
                    billdate.setText(date);
                    TextView billname = (TextView) view.findViewById(R.id.billname);
                    billname.setText(short_name);

                    detll.addView(view);
                    added += 1;
                }
            }
        } catch (Exception e) {

        }

    }
}
