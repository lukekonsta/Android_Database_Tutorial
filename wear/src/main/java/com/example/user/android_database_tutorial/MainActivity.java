package com.example.user.android_database_tutorial;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    DatabaseHelper myDb;

    EditText editName, editSurname, editMarks, editTextId;
    Button btnAddData;
    Button btnviewAll;
    Button btnDelete;
    Button btnviewUpdate;

    ProgressDialog pd;
    String ServerURL = "http://10.230.6.2/android/insert.php";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);



        // Enables Always-on
        setAmbientEnabled();

        myDb = new DatabaseHelper(this);
        /*AddData();
        viewAll();
        UpdateData();
        DeleteData();*/
        try {
            writeFileExternalStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }







    public void writeFileExternalStorage() throws IOException {

        Context context = MainActivity.this;



        /*File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Student.db");
        File currentDB = context.getDatabasePath(myDb.DATABASE_NAME);
        System.out.println(currentDB);
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            System.out.println(src.size());
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }*/

        String s = context.getDatabasePath(myDb.DATABASE_NAME).toString();
        String path = s.replace("/Student.db","");
        System.out.println(path);
        System.out.println(myDb.DATABASE_NAME);
        myDb.insertData(";",
                ";",
                "2");
        File directory = new File(path);
        File[] files = directory.listFiles();
        System.out.println(files.length);
        File one;

        for (int i = 0; i < files.length; i++)
        {

            System.out.println(files[i].getName());
            one = files[i];
            File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), files[i].getName());



            FileChannel inChannel = new FileInputStream(one).getChannel();
            FileChannel outChannel = new FileOutputStream(backupDB).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }


        }
    }


    public void DeleteData() {//give all information in the edittext, so as to find and delete the data
        btnDelete.setOnClickListener(


                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //myDb.deleteData();
                        Integer deletedRows = myDb.deleteData(editTextId.getText().toString());
                        System.out.println(deletedRows);
                        if (deletedRows > 0)
                            Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Data not Deleted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void UpdateData() {
        btnviewUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdate = myDb.updateData(editTextId.getText().toString(),
                                editName.getText().toString(),
                                editSurname.getText().toString(), editMarks.getText().toString());
                        if (isUpdate == true)
                            Toast.makeText(MainActivity.this, "Data Update", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Data not Updated", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(editName.getText().toString(),
                                editSurname.getText().toString(),
                                editMarks.getText().toString());
                        if (isInserted == true)
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void getData() {

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            //showMessage("Error", "Nothing found");
            new JsonTask().execute("http://192.168.10.3/android/getdata.php");
            //return;
        }

        else{

            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append("Id :" + res.getString(0) + "\n");
                buffer.append("Name :" + res.getString(1) + "\n");
                buffer.append("Surname :" + res.getString(2) + "\n");
                buffer.append("Marks :" + res.getString(3) + "\n\n");
            }

            // Show all data
            showMessage("Data", buffer.toString());

        }

    }


    public void viewAll() {
        btnviewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if (res.getCount() == 0) {
                            // show message
                            showMessage("Error", "Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Id :" + res.getString(0) + "\n");
                            buffer.append("Name :" + res.getString(1) + "\n");
                            buffer.append("Surname :" + res.getString(2) + "\n");
                            buffer.append("Marks :" + res.getString(3) + "\n\n");
                        }

                        // Show all data
                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }


    //get data from database
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                JSONObject json = new JSONObject(String.valueOf(buffer));

                JSONArray cast = json.getJSONArray("result");

                for (int i = 0; i < cast.length(); ++i) {
                    JSONObject rec = cast.getJSONObject(i);
                    System.out.println(rec);
                    String first = rec.getString("first");
                    String second = rec.getString("second");
                    String third = rec.getString("date");
                    myDb.insertData(first, second, third);
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

        }
    }// end of async caller*/






}
