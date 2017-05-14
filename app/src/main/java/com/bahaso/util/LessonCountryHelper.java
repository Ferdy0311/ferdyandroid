package com.bahaso.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bahaso.R;
import com.bahaso.adapter.LessonCountryAdapter;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.LessonCountry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shiperus on 3/6/2017.
 */

public class LessonCountryHelper {

    private static List<LessonCountry> lessonCountryList = new ArrayList<>();
    private static String URL_LESSON_COUNTRY;
    private static String token;
    private static LessonCountryAdapter lessonCountryAdapter;
    public static List<LessonCountry> getLessonCountryList(Context context, String tk, LessonCountryAdapter lcAdapter) {
        lessonCountryAdapter = lcAdapter;
        token = tk;
        if (lessonCountryList.size() == 0) {

                getListData(context,lessonCountryAdapter);
//            lessonCountryList.add(new LessonCountry("TES", "TES"));
        }

        Log.i("ArrLength",lessonCountryList.size()+"");


//        token="mhVGOFUko6T95n1ySA4MaafPzj1gkeZBeR3gddxU";
//        getListData(context);

//        if(lessonCountryList.size() == 0) {
//            lessonCountryList.add(new LessonCountry("TES", "TES"));
//            lessonCountryList.add(new LessonCountry("TES", "TES"));
//            lessonCountryList.add(new LessonCountry("TES", "TES"));
//            lessonCountryList.add(new LessonCountry("TES", "TES"));
//        }
        return lessonCountryList;
    }



    private static void getListData(Context ctx,LessonCountryAdapter lcAdapter)
    {

        final String client_secret = "client1secret";
        final String grant_type = "password";
        final String client_id = "client1id";
        final Context context = ctx;
        final GlobalVar global = (GlobalVar)context;
        URL_LESSON_COUNTRY = global.getBaseURLpath() + "courses/";
        Log.i("Token", token);
        Log.i("TessAtas", URL_LESSON_COUNTRY);

        try {


            StringRequest request = new StringRequest(Request.Method.GET, URL_LESSON_COUNTRY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonResponse = new JSONObject(response);
                                String status = jsonResponse.getString("status");
                                String message = jsonResponse.getString("message");

                                //
                                // Log.i("JSONMsg", message);

                                if(status.equals("true")) {
                                    if(lessonCountryList.size() == 0) {
                                        JSONArray jsonArr = jsonResponse.getJSONArray("data");

                                        try {
                                            for (int idxJsonArr = 0; idxJsonArr < jsonArr.length(); idxJsonArr++) {
                                                JSONObject jsonObjLessonCountry = jsonArr.getJSONObject(idxJsonArr);
                                                String countryName = jsonObjLessonCountry.getString("name");
                                                String placementTestId = "";
                                                try{
                                                    placementTestId = jsonObjLessonCountry.getString("placement_test_id");
                                                }catch (JSONException e)
                                                {
                                                    placementTestId = "";
                                                    e.printStackTrace();
                                                }
                                                lessonCountryList.add(new LessonCountry(countryName, "", placementTestId));
                                            Log.i("jsonData", jsonArr.getJSONObject(idxJsonArr) + "");
                                            }
                                            lessonCountryAdapter.notifyDataSetChanged();
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
//                            hidePDialog();
                            }
                        }
                        },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(context,
                                        context.getString(R.string.error_network_timeout),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(context,
                                        context.getString(R.string.error_auth_failure),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(context,
                                        context.getString(R.string.error_server),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError || error instanceof TimeoutError) {
                                Toast.makeText(context,
                                        context.getString(R.string.error_internet_access),
                                        Toast.LENGTH_LONG).show();
                            }
//                        hidePDialog();

                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(ctx).add(request);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
