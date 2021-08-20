package com.example.checkboxes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    ListView listView;
    ListView checkboxlistview;
    static ArrayAdapter<String> adapter;
    static adapterCheckbox checkBoxArrayAdapter;
    static ArrayList<String> list;
    static ArrayList<checkboxclass> checkBoxArrayList;
    Switch darkMode;
    EditText editText;
    ImageView addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        listView = findViewById(R.id.listview);
        checkboxlistview = findViewById(R.id.checkbox);
        darkMode = findViewById(R.id.dark_mode);
        addItem = findViewById(R.id.addItem);
        editText = findViewById(R.id.editText);

        loadData();
        loadListData();
        buildRecyclerView();

        addItem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                checkboxclass checkboxclass = new checkboxclass();
                checkboxclass.setText(name);
                if (TextUtils.isEmpty(name)) {
                    editText.setError("please enter a name");
                    return;
                } else {
                    if (!checkBoxArrayList.contains(checkboxclass)) {
                        checkboxclass checkboxdynammic = new checkboxclass();
                        checkboxdynammic.setText(name);
                        checkboxdynammic.setChecked(false);
                        checkBoxArrayList.add(checkboxdynammic);
                        checkBoxArrayAdapter.notifyDataSetChanged();
                        saveData();
                        editText.setText("");
                    } else {
                        editText.setError("Already exists");
                        return;
                    }
                }

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkMode.setChecked(isDarkModeOn);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            darkMode.setChecked(isDarkModeOn);
        }

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                }
            }
        });
    }

    private void buildRecyclerView() {
        checkBoxArrayAdapter = new adapterCheckbox(this, checkBoxArrayList);
        checkboxlistview.setAdapter(checkBoxArrayAdapter);

        adapter = new ArrayAdapter<>(this, R.layout.list_black_text, R.id.itemText, list);
        listView.setAdapter(adapter);

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sharedPreferences.getString("saveCheckboxList", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<checkboxclass>>() {
        }.getType();

        checkBoxArrayList = gson.fromJson(json, type);

        if (checkBoxArrayList == null) {
            checkBoxArrayList = new ArrayList<>();
        }
    }

    public void loadListData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences2", MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sharedPreferences.getString("saveCheckListData", null);

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        list = gson.fromJson(json, type);

        if (list == null) {
            list = new ArrayList<>();
        }
    }

    public static void saveData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(checkBoxArrayList);

        editor.putString("saveCheckboxList", json);

        editor.apply();

    }

    public static void saveListData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences2", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(list);
        editor.putString("saveCheckListData", json);

        editor.apply();

    }

}

class adapterCheckbox extends ArrayAdapter<checkboxclass> {

    public adapterCheckbox(@NonNull Context context, ArrayList<checkboxclass> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentView = convertView;
        if (currentView == null) {
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.sample_checkbox, parent, false);
        }
        checkboxclass checkboxclass = getItem(position);
        CheckBox checkBox = currentView.findViewById(R.id.item);
        checkBox.setChecked(checkboxclass.isChecked());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    MainActivity.list.add((String) checkBox.getText());
                    checkboxclass.setChecked(true);
                    MainActivity.checkBoxArrayList.set(position, checkboxclass);
                    MainActivity.adapter.notifyDataSetChanged();
                    MainActivity.checkBoxArrayAdapter.notifyDataSetChanged();
                    MainActivity.saveListData();
                    MainActivity.saveData();
                } else {
                    MainActivity.list.remove((String) checkBox.getText());
                    checkboxclass.setChecked(false);
                    MainActivity.checkBoxArrayList.set(position, checkboxclass);
                    MainActivity.adapter.notifyDataSetChanged();
                    MainActivity.checkBoxArrayAdapter.notifyDataSetChanged();
                    MainActivity.saveListData();
                    MainActivity.saveData();
                }
            }
        });
        checkBox.setText(checkboxclass.getText().toString());
        return currentView;
    }
}

