package com.example.peliculasianapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final String MOVIE_BASE_URL="https://image.tmdb.org/t/p/w185";
    public static final String endPoint ="http://api.themoviedb.org/3/discover/movie?api_key=44c90649c35b4b5329783a397c7df588&language=es";
    private ListView listView;
    private ArrayList<Pelicula> listaPeliculas = new ArrayList<Pelicula>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        new ObtenerPeliculasAsync().execute(endPoint);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                intent.putExtra("id", String.valueOf (listaPeliculas.get(position).getId() ) );
                intent.putExtra("titulo", listaPeliculas.get(position).getTitle() );
                intent.putExtra("imagen", listaPeliculas.get(position).getPoster_path() );
                intent.putExtra("sinopsis", listaPeliculas.get(position).getOverview() );
                Log.d("test", "Pasando id " + listaPeliculas.get(position).getId() );

                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuConfiguracion:
                intent = new Intent(getApplicationContext(), ConfigActivity.class);
                intent.putExtra("api", endPoint.substring(51));
                intent.putExtra("epp", endPoint);
                startActivity(intent);
                return true;
            case R.id.mnuAcerca:
                intent = new Intent(getApplicationContext(), aboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.mnuSalir:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class ObtenerPeliculasAsync extends AsyncTask<String, Integer, String> {
        ProgressDialog progreso;

        @Override
        protected void onPreExecute (){

            super.onPreExecute();

            progreso = new ProgressDialog(MainActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Obteniendo peliculas ...");
            progreso.setCancelable(false);
            progreso.setMax(100);
            progreso.setProgress(0);
            progreso.show();
        }

        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            try{
                URL urlObj = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = reader.readLine()) != null) result.append(line);

                Log.d("test", "respuesta: " + result.toString());

            } catch (Exception e) {
                Log.d("test", "error2: " + e.toString());
            }

            return result.toString();
        }

        @Override
        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject resp = null;
            JSONArray peliculas = null;

            try {
                resp = new JSONObject(result);
                peliculas = resp.getJSONArray("results");
                for (int i=0; i < peliculas.length(); i++) {
                    JSONObject pelicula = peliculas.getJSONObject(i);

                    //rellenamos el array
                    listaPeliculas.add(new Pelicula(
                            pelicula.getInt("id"),
                            pelicula.getString("title"),
                            pelicula.getString("backdrop_path"),
                            pelicula.getString("poster_path"),
                            pelicula.getString("original_title"),
                            pelicula.getString("overview"),
                            pelicula.getDouble("popularity"),
                            pelicula.getString("release_date")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progreso.dismiss();

            AdaptadorPelicula adaptador = new AdaptadorPelicula(getApplicationContext(), listaPeliculas);
            listView.setAdapter(adaptador);
        }
    }

    class AdaptadorPelicula extends BaseAdapter {
        Context context;
        ArrayList<Pelicula> arrayList;

        public AdaptadorPelicula(Context context, ArrayList<Pelicula> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        public int getCount() {
            return arrayList.size();
        }

        public Pelicula getItem(int position) {
            return arrayList.get(position);
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView ==  null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.pelicula, parent, false);
            }

            // Fecha
            TextView fecha = (TextView) convertView.findViewById(R.id.tvFecha);
            fecha.setText(arrayList.get(position).getRelease_date());

            // Titulo
            TextView name = (TextView) convertView.findViewById(R.id.tvTitle);
            name.setText(arrayList.get(position).getTitle());

            // Desc
            TextView descripcion = (TextView) convertView.findViewById(R.id.tvDescripcion);
            descripcion.setText(arrayList.get(position).getOverview().substring(0,100) + " ... ");

            // Imagen.
            ImageView imagen = (ImageView) convertView.findViewById(R.id.list_image);
            Picasso.get().load(MOVIE_BASE_URL + arrayList.get(position).getBackdrop_path()).into(imagen);
            imagen.setScaleType(ImageView.ScaleType.FIT_XY);

            return convertView;
        }
    }
}

