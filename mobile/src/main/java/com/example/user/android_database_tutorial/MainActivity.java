package com.example.user.android_database_tutorial;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;

import static com.example.user.android_database_tutorial.DatabaseHelper.DATABASE_NAME;


public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editName, editSurname, editMarks, editTextId;
    Button btnAddData;
    Button btnviewAll;
    Button btnDelete;
    Button MYSQL;
    Button extrnal;

    Button btnviewUpdate;
    private final String filenameInternal = "couponsFile";
    private final String filenameExternal = "cashbackFile";


    ProgressDialog pd;
    String ServerURL = "http://10.230.6.2/android/insert.php";
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        editName = (EditText) findViewById(R.id.editText_name);
        editSurname = (EditText) findViewById(R.id.editText_surname);
        editMarks = (EditText) findViewById(R.id.editText_Marks);
        editTextId = (EditText) findViewById(R.id.editText_id);
        btnAddData = (Button) findViewById(R.id.button_add);
        btnviewAll = (Button) findViewById(R.id.button_viewAll);
        btnviewUpdate = (Button) findViewById(R.id.button_update);
        btnDelete = (Button) findViewById(R.id.button_delete);
        extrnal = (Button) findViewById(R.id.button_external);
        AddData();
        viewAll();
        UpdateData();
        DeleteData();

        MYSQL = (Button) findViewById(R.id.button_myslq);
        MYSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();

            }
        });

        extrnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFileExternalStorage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    public void writeFileInternalStorage(View view) {
        String coupons = "Get upto 20% off mobile @ xyx shop \n Get upto 30% off on appliances @ yuu shop";
        createUpdateFile(filenameInternal, coupons, false);
    }

    public void appendFileInternalStorage(View view) {
        String coupons = "Get upto 50% off fashion @ xyx shop \n Get upto 80% off on beauty @ yuu shop";
        createUpdateFile(filenameInternal, coupons, true);
    }

    private void createUpdateFile(String fileName, String content, boolean update) {
        FileOutputStream outputStream;

        try {
            if (update) {
                outputStream = openFileOutput(fileName, Context.MODE_APPEND);
            } else {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFileInternalStorage(View view) {
        try {
            FileInputStream fileInputStream = openFileInput(filenameInternal);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTemporaryFile(View view) {
        try {
            String fileName = "couponstemp";
            String coupons = "Get upto 50% off shoes @ xyx shop \n Get upto 80% off on shirts @ yuu shop";

            File file = File.createTempFile(fileName, null, getCacheDir());

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(coupons.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
        }
    }

    public void deleteFile(View view) {
        try {
            String fileName = "couponstemp";
            File file = File.createTempFile(fileName, null, getCacheDir());

            file.delete();
        } catch (IOException e) {
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









        /*String cashback = "Get 2% cashback on all purchases from xyz \n Get 10% cashback on travel from dhhs shop";
        String state = Environment.getExternalStorageState();
        //external storage availability check
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), filenameExternal);


        FileOutputStream outputStream = null;
        try {

            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(cashback.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*File dir = getDatabasePath("Student.db");
        //System.out.println(dir.getAbsolutePath());
        Context context = MainActivity.this;
        //System.out.println(context.getDatabasePath(DatabaseHelper.DATABASE_NAME));


        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String currentDBPath = context.getDatabasePath(myDb.DATABASE_NAME).toString();
                System.out.println(currentDBPath);
                String backupDBPath = "backupname.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    System.out.println("existSS");
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    System.out.println(src.toString());
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }else {
                    System.out.println("doesn't exist");
                }
            }
        } catch (Exception e) {

        }*/




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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
