package com.uclaradio.uclaradio.fragments.schedule;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uclaradio.uclaradio.R;
import com.uclaradio.uclaradio.interfaces.RadioPlatform;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;

    private ContentLoadingProgressBar scheduleProgress;

    // Again, don't want to remove boilerplate so leaving this stuff here
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private String mParam1;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    @SuppressWarnings("unused")
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.shows_ids_rv);
        scheduleProgress = view.findViewById(R.id.schedule_progress);
        scheduleProgress.show();

        getSchedules();
    }

    private void getSchedules() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://uclaradio.com/")
                .build();

        RadioPlatform platform = retrofit.create(RadioPlatform.class);

        platform.getSchedules()
                .enqueue(new Callback<ScheduleList>() {
                    @Override
                    public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {
                        if(response.isSuccessful()) {
                            ScheduleAdapter adapter = new ScheduleAdapter(response.body().getScheduleList(), getActivity());
                            recyclerView.setAdapter(adapter);
//                            LinearLayoutManager manager =
//                                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            GridLayoutManager manager =
                                    new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);

                            for (ScheduleData show : response.body().getScheduleList())
                            {
                                if (show == null) continue;
                                Log.d("TAG", "SHOW NAME IS " + show.getTitle());
                                Log.d("TAG", "Time: " + show.getTime());
                                Log.d("TAG", "Day: " + show.getDay());
                                Log.d("TAG", "Genre: " + show.getGenre());
                            }

                            scheduleProgress.hide();

                        } else {
                            Log.e("TAG", "HERE FAILED");
                            getSchedules();
                        }
                    }

                    @Override
                    public void onFailure(Call<ScheduleList> call, Throwable t) {
                        Log.e("TAG", "FAILED TO MAKE API CALL");
                        getSchedules();
                    }


                });
    }


        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}