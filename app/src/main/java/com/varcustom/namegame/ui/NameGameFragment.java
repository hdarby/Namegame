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


public class NameGameFragment extends Fragment {

    private static final String HTTP = "http:";
    private static final int MAX_QUESTIONS = 10;

    private TextView mScore;
    private TextView mQuestion;
    private List<FrameLayout> mFrames;
    private ArrayList<Person> mEmployees;
    private Person mAnswer;
    private int mComputedHW;
    private boolean mSecondChance;

    private NameGameListener mNameGameListener;

    public static NameGameFragment newInstance() {
        NameGameFragment fragment = new NameGameFragment();

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mNameGameListener = (NameGameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NameGameListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("people", mEmployees);
        savedInstanceState.putInt("computedHW", mComputedHW);
        savedInstanceState.putBoolean("secondChance", mSecondChance);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_namegame, container, false);

        if (savedInstanceState != null) {
            Log.d(getClass().getSimpleName(), "restoring savedInstanceState");

            mEmployees = savedInstanceState.getParcelableArrayList("people");
            mComputedHW = savedInstanceState.getInt("computedHW");
            mSecondChance = savedInstanceState.getBoolean("secondChance");

        }
        View containerView;
        List<ImageView> faces;

        mScore = view.findViewById(R.id.score);

        mFrames = new ArrayList<>();
        faces = new ArrayList<>();

        mQuestion = view.findViewById(R.id.title);
        containerView = view.findViewById(R.id.face_container);

        List views = getAllChildren(containerView);
        mFrames.add(0, view.findViewById(R.id.frame_one));
        mFrames.add(1, view.findViewById(R.id.frame_two));
        mFrames.add(2, view.findViewById(R.id.frame_three));
        mFrames.add(3, view.findViewById(R.id.frame_four));
        mFrames.add(4, view.findViewById(R.id.frame_five));
        mFrames.add(5, view.findViewById(R.id.frame_six));
        faces.add(0, view.findViewById(R.id.face_one));
        faces.add(1, view.findViewById(R.id.face_two));
        faces.add(2, view.findViewById(R.id.face_three));
        faces.add(3, view.findViewById(R.id.face_four));
        faces.add(4, view.findViewById(R.id.face_five));
        faces.add(5, view.findViewById(R.id.face_six));

        for (int i = 0; i < faces.size(); i++) {
            final int index = i;
            faces.get(i).setOnClickListener((View lambdaView) -> {
                Score s = mNameGameListener.getScore();

                if (checkSelection(mEmployees.get(index))) {
                    s.correctAnswer();
                    mNameGameListener.showSnackbar(getResources().getString(R.string.correct));

                } else {
                    if (mNameGameListener.getGameMode() == GameData.GameMode.SECOND_CHANCE_GAME) {
                        if (mSecondChance) {
                            s.incorrectAnswer();
                            mNameGameListener.showSnackbar(getResources().getString(R.string.incorrect));
                        } else {
                            mSecondChance = true;
                            mNameGameListener.showSnackbar(getResources().getString(R.string.try_again));

                            return;
                        }

                    } else {
                        s.incorrectAnswer();
                        mNameGameListener.showSnackbar(getResources().getString(R.string.incorrect));
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

        mComputedHW = (int) Ui.convertDpToPixel(Math.max(dpHeight, dpWidth) / 10, getContext());
    }

    private void playRound() {
        GameInstanceProvider data = mNameGameListener.getNewGameInstance();
        data.init();
        mSecondChance = false;

        // Pick an answer from the available people
        mEmployees = data.getPeople();
        mAnswer = GenericChooser.chooseOneFromList(mEmployees);

        // Set the question
        mQuestion.setText(String.format(getResources().getString(R.string.question), mAnswer.getName()));

        // Set the headshots
        setImages(mFrames, mEmployees);
    }

    private void roundOver() {
        Score s = mNameGameListener.getScore();

        if (s.getAttempts() < MAX_QUESTIONS) {
            playRound();
        } else {
            endGame();
        }
    }

    private void endGame() {
        mNameGameListener.updateHighScore(mNameGameListener.getScore());
        mNameGameListener.gameOver();
    }

    private boolean checkSelection(@NonNull Person selection) {
        return selection.equals(mAnswer);
    }

    private void setImages(@NonNull List<FrameLayout> frames, @NonNull List<Person> people) {
        for (int i = 0; i < frames.size(); i++) {
            final FrameLayout frame = frames.get(i);

            // index 0:ImageView, index 1: ProgressView
            if (frame.getChildAt(0) != null) {
                if (frame.getChildAt(0) instanceof ImageView) {

                    final ImageView face = (ImageView) frame.getChildAt(0);

                    if (frame.getChildAt(1) != null) {
                        if (frame.getChildAt(1) instanceof ProgressBar) {

                            final ProgressBar progressBar = (ProgressBar) frame.getChildAt(1);
                            progressBar.setVisibility(View.VISIBLE);

                            Picasso.with(getContext()).load(HTTP + people.get(i).getHeadshot().getUrl())
                                    .resize(mComputedHW, mComputedHW)
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
        Score s = mNameGameListener.getScore();

        StringBuilder scoreString = new StringBuilder("Current Score: ");
        if (s.getAttempts() > 0) {

            scoreString.append(String.format("%s %% (%s)",s.getScoreAsPercentage(), s.getScoreAsRatio()));
        }

        mScore.setText(scoreString);
    }

    private List<View> getAllViews(View v) {
        if (!(v instanceof ViewGroup) || ((ViewGroup) v).getChildCount() == 0)
        { List<View> r = new ArrayList<>(); r.add(v); return r; }
        else {
            List<View> list = new ArrayList<>(); list.add(v);
            int children = ((ViewGroup) v).getChildCount();
            for (int i=0;i<children;++i) {
                list.addAll(getAllViews(((ViewGroup) v).getChildAt(i)));
            }
            return list;
        }
    }
    private List<View> getAllChildren(View v) {
        List<View> list = getAllViews(v);
        list.remove(v);
        return list;
    }

    public interface NameGameListener {
        GameInstanceProvider getNewGameInstance();
        GameData.GameMode getGameMode();
        void showSnackbar(String message);
        Score getScore();
        void updateHighScore(@NonNull Score score);
        void gameOver();
    }
}
