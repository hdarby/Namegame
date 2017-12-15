package com.varcustom.namegame.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.varcustom.namegame.R;
import com.varcustom.namegame.data.GameInstanceProvider;
import com.varcustom.namegame.model.GameData;
import com.varcustom.namegame.model.Person;
import com.varcustom.namegame.model.Score;
import com.varcustom.namegame.util.CircleBorderTransform;
import com.varcustom.namegame.util.GenericChooser;
import com.varcustom.namegame.util.Ui;

import java.util.ArrayList;
import java.util.List;


public class NamegameFragment extends Fragment {

    private static final String HTTP = "http:";
    private static final int MAX_QUESTIONS = 10;

    private TextView mScore;
    private TextView mQuestion;
    private View mContainer;
    private List<FrameLayout> mFrames;
    private List<ImageView> mFaces;
    private List<Person> mEmployees;
    private Person mAnswer;
    private int computedHW;
    private boolean secondChance;

    private NamegameGameInstanceListener mNamegameGameInstanceListener;

    public static NamegameFragment newInstance() {
        NamegameFragment fragment = new NamegameFragment();

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mNamegameGameInstanceListener = (NamegameGameInstanceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NamegameGameInstanceListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_namegame, container, false);

        if (savedInstanceState != null) {
        }

        mScore = view.findViewById(R.id.score);

        mFrames = new ArrayList<>();
        mFaces = new ArrayList<>();

        mQuestion = view.findViewById(R.id.title);
        mContainer = view.findViewById(R.id.face_container);

        ArrayList views = getAllChildren(mContainer);
        mFrames.add(0, view.findViewById(R.id.frame_one));
        mFrames.add(1, view.findViewById(R.id.frame_two));
        mFrames.add(2, view.findViewById(R.id.frame_three));
        mFrames.add(3, view.findViewById(R.id.frame_four));
        mFrames.add(4, view.findViewById(R.id.frame_five));
        mFrames.add(5, view.findViewById(R.id.frame_six));
        mFaces.add(0, view.findViewById(R.id.face_one));
        mFaces.add(1, view.findViewById(R.id.face_two));
        mFaces.add(2, view.findViewById(R.id.face_three));
        mFaces.add(3, view.findViewById(R.id.face_four));
        mFaces.add(4, view.findViewById(R.id.face_five));
        mFaces.add(5, view.findViewById(R.id.face_six));

        for (int i = 0; i < mFaces.size(); i++) {
            final int index = i;
            mFaces.get(i).setOnClickListener((View lambdaView) -> {
                Score s = mNamegameGameInstanceListener.getScore();

                if (checkSelection(mEmployees.get(index)) == true) {
                    s.correctAnswer();
                    mNamegameGameInstanceListener.showSnackbar(getResources().getString(R.string.correct));

                } else {
                    if (mNamegameGameInstanceListener.getGameMode() == GameData.GameMode.SECOND_CHANCE_GAME) {
                        if (secondChance == true) {
                            s.incorrectAnswer();
                            mNamegameGameInstanceListener.showSnackbar(getResources().getString(R.string.incorrect));
                        } else {
                            secondChance = true;
                            mNamegameGameInstanceListener.showSnackbar(getResources().getString(R.string.try_again));

                            return;
                        }

                    } else {
                        s.incorrectAnswer();
                        mNamegameGameInstanceListener.showSnackbar(getResources().getString(R.string.incorrect));
                    }
                }

                updateScore();
                roundOver();
            });

        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calculateImageHeight();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        updateScore();

        // Let's Play!
        playRound();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void calculateImageHeight() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;

        computedHW = (int) Ui.convertDpToPixel(Math.max(dpHeight, dpWidth) / 10, getContext());
    }

    private void playRound() {
        GameInstanceProvider data = mNamegameGameInstanceListener.getNewGameInstance();
        data.init();
        secondChance = false;

        // First, pick an answer from the available people
        mEmployees = data.getPeople();
        mAnswer = GenericChooser.chooseOneFromList(mEmployees);

        // Set the question
        mQuestion.setText(String.format(getResources().getString(R.string.question), mAnswer.getName()));

        // Set the headshots
        setImages(mFrames, mEmployees);
    }

    private void roundOver() {
        Score s = mNamegameGameInstanceListener.getScore();

        if (s.getAttempts() < MAX_QUESTIONS) {
            playRound();
        } else {
            endGame();
        }
    }

    private void endGame() {
        mNamegameGameInstanceListener.updateHighScore(mNamegameGameInstanceListener.getScore());
        mNamegameGameInstanceListener.gameOver();
    }

    private boolean checkSelection(@NonNull Person selection) {
        return selection.equals(mAnswer);
    }

    private void setImages(@NonNull List<FrameLayout> frames, @NonNull List<Person> people) {
        for (int i = 0; i < frames.size(); i++) {
            final FrameLayout frame = frames.get(i);

            // In FrameLayout:
            // index 0 = ImageView,
            // index 1 = ProgressView
            if (frame.getChildAt(0) != null) {
                if (frame.getChildAt(0) instanceof ImageView) {

                    final ImageView face = (ImageView) frame.getChildAt(0);

                    if (frame.getChildAt(1) != null) {
                        if (frame.getChildAt(1) instanceof ProgressBar) {

                            final ProgressBar progressBar = (ProgressBar) frame.getChildAt(1);
                            progressBar.setVisibility(View.VISIBLE);

                            Picasso.with(getContext()).load(HTTP + people.get(i).getHeadshot().getUrl())
                                    .resize(computedHW, computedHW)
                                    .transform(new CircleBorderTransform())
                                    .into(face, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            progressBar.setVisibility(View.GONE);
                                            face.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError() {
                                            progressBar.setVisibility(View.GONE);
                                            face.setBackgroundResource(R.drawable.ic_face_white_48dp);
                                            Log.e(getClass().getSimpleName(), "Error loading image");
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void updateScore() {
        Score s = mNamegameGameInstanceListener.getScore();

        StringBuilder scoreString = new StringBuilder("Current Score: ");
        if (s.getAttempts() > 0) {

            scoreString.append(s.getScoreAsPercentage() + "% (" + s.getScoreAsRatio() + ")");
        }

        mScore.setText(scoreString);
    }

    private ArrayList<View> getAllChildren(@NonNull View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    public interface NamegameGameInstanceListener {
        GameInstanceProvider getNewGameInstance();
        GameData.GameMode getGameMode();
        void showSnackbar(String message);
        Score getScore();
        void updateHighScore(@NonNull Score score);
        void gameOver();
    }
}
