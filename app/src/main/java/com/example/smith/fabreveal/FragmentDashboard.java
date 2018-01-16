package com.example.smith.fabreveal;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDashboard extends Fragment {


    public FragmentDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button learnHack = (Button) rootView.findViewById(R.id.learn_hack);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        final TextView title = (TextView) rootView.findViewById(R.id.title);
        final RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative_layout);
        learnHack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Fragment fragment = new FragmentHackathon();
                getFragmentManager()
                        .beginTransaction()
                        .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                        .addToBackStack("hackathon")
                        .replace(R.id.main_frame_layout, fragment)
                        .commit();*/
                Intent intent = new Intent(getActivity().getApplicationContext(), LearnMore.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(),
                                Pair.create((View) imageView, ViewCompat.getTransitionName(imageView))
                        );
                startActivity(intent, options.toBundle());
            }
        });
        return rootView;
    }

}
