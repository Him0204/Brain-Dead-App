package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ScoreboardPage extends AppCompatActivity {

    ListView listView;
    String[][] allInfo;
    TextView playerNameTextView;
    TextView playerRankTextView;
    TextView playerStageTextView;
    TextView playerTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard);

        Bundle extras = getIntent().getExtras();
        String email = extras != null ? extras.getString("email") : null;
        String player_username = extras != null ? extras.getString("username") : null;

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        allInfo = dbHelper.getAllInfo();

        listView = findViewById(R.id.list_view);
        CustomListAdapter adapter = new CustomListAdapter(this, allInfo);

        listView.setAdapter(adapter);

        playerNameTextView = findViewById(R.id.player_name);
        playerStageTextView = findViewById(R.id.stage);
        playerTimeTextView = findViewById(R.id.time_taken);
        playerRankTextView = findViewById(R.id.rank);
        playerNameTextView.setText(player_username);
        playerStageTextView.setText(String.format("Stage: %s", getStage(player_username, allInfo)));
        playerRankTextView.setText(getRank(player_username, allInfo));

        int default_time_taken_time = 0;
        double second = 0;
        second= (double) default_time_taken_time /1000;
        double min = 0;
        min = second /60;
        second = second % 60;

        if (min <= 60){
            if(min == 1){
                playerTimeTextView.setText(String.format("Time Taken: %s min %s secs", (int) min, (int) second));
            }
            playerTimeTextView.setText(String.format("Time Taken: %s mins %s secs", (int) min, (int) second));
        } else {
            playerTimeTextView.setText(String.format("Time Taken: > 1 hour"));
        }

        Button back = findViewById(R.id.button_back);
        back.setOnClickListener(v -> {
            Intent i = new Intent(ScoreboardPage.this, LobbyPage.class);
            i.putExtra("Email", email);
            startActivity(i);
        });
    }

    private static class CustomListAdapter extends BaseAdapter {
        private final Context mContext;
        private final String[][] mData;

        public CustomListAdapter(Context context, String[][] data) {
            mContext = context;
            mData = data;
        }

        @Override
        public int getCount() {
            // Return the number of items in your data
            return Math.min(mData.length, 50);
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.list_item, parent, false);
            }

            TextView username = convertView.findViewById(R.id.Username);
            TextView stage = convertView.findViewById(R.id.Stage);
            TextView time_taken = convertView.findViewById(R.id.Time_taken);
            TextView rank = convertView.findViewById(R.id.Rank);

            int time_taken_time = 0;
            time_taken_time = Integer.parseInt(mData[position][2]);
            double second = 0;
            second= (double) time_taken_time /1000;
            double min = 0;
            min = second /60;
            second = second % 60;

            // Fetch data from your array for each item
            username.setText(mData[position][0]);
            stage.setText(String.format("Stage: %s", mData[position][1]));
            rank.setText(String.valueOf(position + 1));
            if (min <= 60){
                if(min == 1){
                    time_taken.setText(String.format("Time Taken: %s min %s secs", (int) min, (int) second));
                }
                time_taken.setText(String.format("Time Taken: %s mins %s secs", (int) min, (int) second));
            } else {
                time_taken.setText(String.format("Time Taken: > 1 hour"));
            }

            return convertView;
        }
    }

    private String getRank(String username, String[][] allInfo) {
        for (int i = 0; i < allInfo.length; i++) {
            if (allInfo[i][0].equals(username)) {
                return String.valueOf(i + 1);
            }
        }
        return "unranked";
    }

    private String getStage(String username, String[][] allInfo) {
        for (String[] strings : allInfo) {
            if (strings[0].equals(username)) {
                return String.valueOf(strings[1]);
            }
        }
        return "-";
    }

    private String getTime(String username, String[][] allInfo) {
        for (String[] strings : allInfo) {
            if (strings[0].equals(username)) {
                return String.valueOf(strings[2]);
            }
        }
        return "-";
    }
}
