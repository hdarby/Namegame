package com.varcustom.namegame.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.varcustom.namegame.R;
import com.varcustom.namegame.data.GameInstanceProvider;
import com.varcustom.namegame.data.HighScoreProvider;
import com.varcustom.namegame.data.PersonListProvider;
import com.varcustom.namegame.data.PersonListRepository;
import com.varcustom.namegame.model.GameData;
import com.varcustom.namegame.model.Person;
import com.varcustom.namegame.model.Score;

import java.util.List;

public class Namegame extends AppCompatActivity implements
        GameModeChooserFragment.GameModeChooserListener,
        NamegameFragment.NamegameGameInstanceListener,
        EndgameFragment.EndgameListener,
        PersonListRepository.Listener {

    private static final long MOVE_DEFAULT_TIME = 1000;
    private static final long FADE_DEFAULT_TIME = 500;

    private static final String GAME_MODE_CHOOSER = "GameModeChooserTag";
    private static final String NAMEGAME = "NamegameTag";
    private static final String ENDGAME = "EndgameTag";

    private CoordinatorLayout coordinatorLayout;
    private PersonListProvider mPersonListProvider;
    private HighScoreProvider mHighScoreProvider;
    private GameInstanceProvider mGameInstanceProvider;
    private FragmentManager mFragmentManager;
    private GameData mGameData;
    private boolean isDataLoaded;
    private Score mScore;
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            restoreFragment(savedInstanceState);
        }
        setContentView(R.layout.activity_namegame);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        isDataLoaded = false;

        mFragmentManager = getFragmentManager();

        mGameData = new GameData(GameData.GameMode.NAME_GAME);
        mPersonListProvider = new PersonListProvider(getApplicationContext());
        mPersonListProvider.getPersonListRepository().register(this);
        mHighScoreProvider = new HighScoreProvider(getApplicationContext());
        mScore = new Score();

        loadLoaderFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getFragmentManager().findFragmentByTag(GAME_MODE_CHOOSER) != null) {
            getFragmentManager().putFragment(outState, GAME_MODE_CHOOSER,
                    getFragmentManager().findFragmentByTag(GAME_MODE_CHOOSER));
        } else if (getFragmentManager().findFragmentByTag(NAMEGAME) != null) {
            getFragmentManager().putFragment(outState, NAMEGAME,
                    getFragmentManager().findFragmentByTag(NAMEGAME));
        } else if (getFragmentManager().findFragmentByTag(ENDGAME) != null) {
            getFragmentManager().putFragment(outState, ENDGAME,
                    getFragmentManager().findFragmentByTag(ENDGAME));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void restoreFragment(@NonNull Bundle savedInstanceState) {
            //Restore the fragment's instance
            if (getFragmentManager()
                    .getFragment(savedInstanceState, GAME_MODE_CHOOSER) != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, GameModeChooserFragment.newInstance(), GAME_MODE_CHOOSER)
                        .commit();
            } else if (getFragmentManager()
                    .getFragment(savedInstanceState, NAMEGAME) != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, NamegameFragment.newInstance(), NAMEGAME)
                        .commit();
            } else if (getFragmentManager()
                    .getFragment(savedInstanceState, ENDGAME) != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, EndgameFragment.newInstance(), ENDGAME)
                        .commit();
            }
    }

    private void loadLoaderFragment() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        LoadingFragment fragment = LoadingFragment.newInstance();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void loadGameChooserFragment() {
        GameModeChooserFragment nextFragment = GameModeChooserFragment.newInstance();
        performFragmentTransition(nextFragment, GAME_MODE_CHOOSER);
    }

    private void loadGameFragment() {
        NamegameFragment nextFragment = NamegameFragment.newInstance();
        performFragmentTransition(nextFragment, NAMEGAME);
    }

    private void loadEndgameFragment() {
        EndgameFragment nextFragment = EndgameFragment.newInstance();
        performFragmentTransition(nextFragment, ENDGAME);
    }

    private void performFragmentTransition(@NonNull Fragment to, String tag) {
        if (isDestroyed()) {
            return;
        }
        Fragment previousFragment = mFragmentManager.findFragmentById(R.id.container);
        Fragment nextFragment = to;

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fade exitFade = new Fade();
        exitFade.setDuration(FADE_DEFAULT_TIME);
        previousFragment.setExitTransition(exitFade);

        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(MOVE_DEFAULT_TIME);
        enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);

        Fade enterFade = new Fade();
        enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
        enterFade.setDuration(FADE_DEFAULT_TIME);
        nextFragment.setEnterTransition(enterFade);

        fragmentTransaction.replace(R.id.container, nextFragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public GameInstanceProvider getNewGameInstance() {
        return mGameInstanceProvider;
    }

    @Override
    public GameData.GameMode getGameMode() {
        return mGameData.getGameMode();
    }

    @Override
    public void setGameMode(GameData.GameMode mode) {
        mGameData.setGameMode(mode);
        mGameInstanceProvider = new GameInstanceProvider(mGameData, mPersonListProvider);

        mScore = new Score();
        playAgain();
    }

    @Override
    public Score getScore() {
        return mScore;
    }

    @Override
    public Score getHighScore() {
        return mHighScoreProvider.getHighScore();
    }

    @Override
    public void clearHighScore() {
        mHighScoreProvider.clearHighScore();
    }

    @Override
    public void gameOver() {
        loadEndgameFragment();
    }

    @Override
    public void playAgain() {
        mScore = new Score();
        loadGameFragment();
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void chooseNewGameType() {
        mScore = new Score();
        loadGameChooserFragment();
    }

    @Override
    public void allDone() {
        finish();
    }

    @Override
    public void updateHighScore(@NonNull Score score) {
        if (mHighScoreProvider.isNewHighScore(score)) {
            mHighScoreProvider.setHighScore(score);
        }
    }

    @Override
    public void onLoadFinished(@NonNull List<Person> people) {
        isDataLoaded = true;
        loadGameChooserFragment();
    }

    @Override
    public void onError(@NonNull Throwable error) {
        isDataLoaded = false;
        loadGameFragment();
    }
}
