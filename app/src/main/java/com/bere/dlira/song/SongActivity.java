package com.bere.dlira.song;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class SongActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button btnInsert;
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnList;

    private EditText edtName;
    private EditText edtArtist;
    private EditText edtAlbum;
    private EditText edtId;

    private ListView lsvSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        //Buttons
        btnInsert = findViewById(R.id.btn_insert);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnList = findViewById(R.id.btn_list);

        //Text boxes
        edtName = findViewById(R.id.edt_name);
        edtArtist = findViewById(R.id.edt_artist);
        edtAlbum = findViewById(R.id.edt_album);
        edtId = findViewById(R.id.edt_id);

        //List
        lsvSongs = findViewById(R.id.lsv_songs);

        //Variables
        btnInsert.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_insert:
                InsertWSTask insertWSTask = new InsertWSTask();
                insertWSTask.execute(edtName.getText().toString(),
                        edtArtist.getText().toString(),
                        edtAlbum.getText().toString());
                break;
            case R.id.btn_update:
                UpdateWSTask updateWSTask = new UpdateWSTask();
                updateWSTask.execute(edtName.getText().toString(), edtArtist.getText().toString(), edtAlbum.getText().toString());
                break;
            case R.id.btn_delete:
                DeleteWSTask deleteWSTask = new DeleteWSTask();
                deleteWSTask.execute(edtId.getText().toString());
                break;
            case R.id.btn_list:
                ListWSTask listWSTask = new ListWSTask();
                listWSTask.execute();
                break;
            default:
                break;
        }
    }

    //Add method
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }//End method

    //Method list
    private class ListWSTask extends AsyncTask<String, Integer, Boolean> {
        private String[] songs;

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean result = true;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://192.168.0.13:3000/api/songs");
            get.setHeader("content-type", "application/json");
            try {
                HttpResponse response = httpClient.execute(get);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONArray jsonResponse = new JSONArray(strResponse);
                songs = new String[jsonResponse.length()];

                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject jsonObject = jsonResponse.getJSONObject(i);
                    String idSong = jsonObject.getString("_id");
                    String name = jsonObject.getString("name");
                    String artist = jsonObject.getString("artist");
                    String album = jsonObject.getString("album");
                    songs[i] = "" + idSong + " | " + name + " | " + artist + " | " + album;
                }
            } catch (Exception e) {
                Log.e("Rest service error", "Error!", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SongActivity.this, android.R.layout.simple_list_item_1, songs);
                lsvSongs.setAdapter(arrayAdapter);
            }
        }
    }//End method list


    //Method insert
    private class InsertWSTask extends AsyncTask<String, Integer, Boolean> {
        private String[] songs;

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://192.168.0.13:3000/api/songs");
            post.setHeader("Content-type", "application/json");
            try {
                //Create the client object in format json
                JSONObject data = new JSONObject();
                data.put("name", params[0]);
                data.put("artist", params[1]);
                data.put("album", params[2]);
                StringEntity entity = new StringEntity(data.toString());
                post.setEntity(entity);
                HttpResponse response = httpClient.execute(post);
                String strResponse = EntityUtils.toString(response.getEntity());

                /*JSONArray jsonResponse = new JSONArray(strResponse);
                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject jsonObject = jsonResponse.getJSONObject(i);
                    int idSong = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String artist = jsonObject.getString("artist");
                    String album = jsonObject.getString("album");
                    songs[i] = "" + idSong + " | " + name + " | " + artist + " | " + album;
                }
                Log.i("Response", strResponse);*/
            } catch (Exception e) {
                Log.e("Rest service error", "Error!", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(SongActivity.this, "Agregado con éxito.", Toast.LENGTH_SHORT).show();
            }
        }
    }//End method insert

    //Method update
    private class UpdateWSTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            String id = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut put = new HttpPut("http://192.168.0.13:3000/api/songs/" + id);
            put.setHeader("content-type", "application/json");
            try {
                //Create the client object in format json
                JSONObject data = new JSONObject();
                data.put("name", params[0]);
                data.put("artist", params[1]);
                data.put("album", params[2]);
                StringEntity entity = new StringEntity(data.toString());
                put.setEntity(entity);
                HttpResponse response = httpClient.execute(put);
                String strResponse = EntityUtils.toString(response.getEntity());
                Log.i("Response", strResponse);
            } catch (Exception e) {
                Log.e("Rest service error", "Error!", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(SongActivity.this, "Modificado con éxito.", Toast.LENGTH_SHORT).show();
            }
        }
    }//End method update

    //Method delete
    private class DeleteWSTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            String id = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete delete = new HttpDelete
                    ("http://192.168.0.13:3000/api/songs/" + id);
            delete.setHeader("content-type", "application/json");
            try {
                HttpResponse response = httpClient.execute(delete);
            } catch (Exception e) {
                Log.e("Rest service error", "Error!", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(SongActivity.this,
                        "Eliminado con éxito.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}//End
