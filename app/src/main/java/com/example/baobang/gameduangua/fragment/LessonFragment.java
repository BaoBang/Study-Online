package com.example.baobang.gameduangua.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baobang.gameduangua.Constant;
import com.example.baobang.gameduangua.R;
import com.example.baobang.gameduangua.adapter.LessonListAdapter;
import com.example.baobang.gameduangua.base.BaseFragment;
import com.example.baobang.gameduangua.data.SOService;
import com.example.baobang.gameduangua.model.Lesson;
import com.example.baobang.gameduangua.model.LessonResponse;
import com.example.baobang.gameduangua.youtube_video.VideoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huuduc on 08/03/2018.
 */

public class LessonFragment extends BaseFragment {

    private RecyclerView rvListLesson;
    private LessonListAdapter listAdapter;
    private SOService mService;
    private ArrayList<Lesson> lessons;
    private Boolean isPurchased;

    public static LessonFragment newInstance(List<Lesson> lessons, Boolean isPurchased) {
        LessonFragment fragment = new LessonFragment();

        Bundle bundle = new Bundle();
        ArrayList<Lesson> arrayList = new ArrayList<>();
        arrayList.addAll(lessons);
        bundle.putSerializable("LIST", arrayList);
        bundle.putBoolean("IsPurchased", isPurchased);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.activity_lesson_list, container, false);
        return itemView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
    }

    private void addControls(View itemView) {
        rvListLesson = itemView.findViewById(R.id.rvLessonList);
        listAdapter = new LessonListAdapter();

        rvListLesson.setLayoutManager(new LinearLayoutManager(getContext()));

        lessons = (ArrayList<Lesson>) getArguments().getSerializable("LIST");

        listAdapter.setListLesson(lessons);
        rvListLesson.setAdapter(listAdapter);
        isPurchased = getArguments().getBoolean("IsPurchased");

        if (isPurchased == true){
            listAdapter.setOnItemClickListener(new LessonListAdapter.OnItemClickListener() {
                @Override
                public void onClick(int pos) {
                    Lesson lesson = lessons.get(pos);
                    String youtubeUrl = lesson.getLessonUrl().substring(32, 43);
                    Log.d("youtube", "onClick: " + youtubeUrl);
                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    intent.putExtra(Constant.URL, youtubeUrl);
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public void onUpdateData(LessonResponse lessonResponse) {

    }
}
