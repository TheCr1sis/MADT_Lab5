package com.example.currencyrate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private DataLoader dataLoader;
    private EditText currencyEditText;
    private List<Map<String, String>> allCurrenciesList; // Original list of currencies

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        currencyEditText = findViewById(R.id.currencyEditText);
        allCurrenciesList = new ArrayList<>();

        dataLoader = new DataLoader(new DataLoader.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(String result) {
                Parser parser = new Parser();
                List<Map<String, String>> currencyList = parser.parseData(result);

                // Save the original list of currencies
                allCurrenciesList = new ArrayList<>(currencyList);

                updateListView(currencyList);
                setupCurrencyFilter();
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "Error: " + error);
            }
        });

        dataLoader.execute("https://www.floatrates.com/daily/usd.xml");
    }

    private void updateListView(List<Map<String, String>> currencyList) {
        ArrayList<String> displayList = new ArrayList<>();

        for (Map<String, String> currencyData : currencyList) {
            String targetName = currencyData.get("targetName");
            String title = currencyData.get("title");

            if (targetName != null && title != null) {
                displayList.add(targetName + "\n" + title);
            }
        }

        adapter.clear();
        adapter.addAll(displayList);
        adapter.notifyDataSetChanged();
    }

    private void setupCurrencyFilter() {
        currencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the original list based on user input
                List<Map<String, String>> filteredList = filterCurrencies(charSequence.toString());

                // Update the ListView with the filtered list
                updateListView(filteredList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this implementation
            }
        });
    }

    private List<Map<String, String>> filterCurrencies(String filter) {
        List<Map<String, String>> filteredList = new ArrayList<>();

        // Filter the original list based on the user input
        for (Map<String, String> currencyData : allCurrenciesList) {
            String targetName = currencyData.get("targetName");
            String title = currencyData.get("title");

            if ((targetName != null && targetName.toLowerCase().contains(filter.toLowerCase())) ||
                    (title != null && title.toLowerCase().contains(filter.toLowerCase()))) {
                filteredList.add(currencyData);
            }
        }

        return filteredList;
    }
}
