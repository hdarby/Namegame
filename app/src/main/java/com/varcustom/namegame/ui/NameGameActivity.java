package com.varcustom.namegame.ui;

import android.app.AlertDialog;
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
import android.util.Log;

import com.varcustom.namegame.R;
import com.varcustom.namegame.data.GameInstanceProvider;
import com.varcustom.namegame.data.HighScoreProvider;
import com.varcustom.namegame.data.PersonListProvider;
import com.varcustom.namegame.data.PersonListRepository;
import com.varcustom.namegame.model.GameData;
import com.varcustom.namegame.model.Person;
import com.varcustom.namegame.model.Score;

import java.util.ArrayList;

public class NameGameActivity extends AppCompatActivity implements
        GameModeFragment.GameModeListener,
        NameGameFragment.NameGameListener,
        EndgameFragment.EndgameListener,
        PersonListRepository.Listener {

    private static final long FADE_DEFAULT_TIME = 1000;

    private static final String LOADER = "LoaderTag";
    private static final String GAME_MODE_CHOOSER = "GameModeChooserTag";
    private static final String NAMEGAME = "NamegameTag";
    private static final String ENDGAME = "EndgameTag";

    private CoordinatorLayout coordinatorLayout;
    private PersonListProvider mPersonListProvider;
    private HighScoreProvider mHighScoreProvider;
    private GameInstanceProvider mGameInstanceProvider;
    private FragmentManager mFragmentManager;
    private GameData mGameData;
    private Score mScore;
    private boolean canStartNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getFragmentManager();
        setContentView(R.layout.activity_namegame);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        if (savedInstanceState != null) {
            restoreFragment(savedInstanceState);

            mGameData = savedInstanceState.getParcelable("gameData");
            mPersonListProvider = savedInstanceState.getParcelable(("personList"));
            mGameInstanceProvider = savedInstanceState.getParcelable("gameInstance");
            mHighScoreProvider = savedInstanceState.getParcelable("highScore");
            mScore = savedInstanceState.getParcelable("score");
            canStartNewGame = savedInstanceState.getBoolean("canStartNewGame");
        } else {

            mGameData = new GameData(GameData.GameMode.NAME_GAME);
            mPersonListProvider = new PersonListProvider(getApplicationContext());
            mPersonListProvider.getPersonListRepository().register(this);
            mHighScoreProvider = new HighScoreProvider(getApplicationContext());
            mScore = new Score(0, 0);
            canStartNewGame = false;

            loadLoaderFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("gameData", mGameData);
        outState.putParcelable("personList", mPersonListProvider);
        outState.putParcelable("gameInstance", mGameInstanceProvider);
        outState.putParcelable("highScore", mHighScoreProvider);
        outState.putParcelable("score", mScore);
        outState.putBoolean("canStartNewGame", canStartNewGame);

        if (getFragmentManager().findFragmentByTag(GAME_MODE_CHOOSER) != null) {
            Log.d("onSaveInstanceState", "saving " + GAME_MODE_CHOOSER);
            getFragmentManager().putFragment(outState, GAME_MODE_CHOOSER,
                    getFragmentManager().findFragmentByTag(GAME_MODE_CHOOSER));
        } else if (getFragmentManager().findFragmentByTag(NAMEGAME) != null) {
            Log.d("onSaveInstanceState", "saving " + NAMEGAME);
            getFragmentManager().putFragment(outState, NAMEGAME,
                    getFragmentManager().findFragmentByTag(NAMEGAME));
        } else if (getFragmentManager().findFragmentByTag(ENDGAME) != null) {
            Log.d("onSaveInstanceState", "saving " + ENDGAME);
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
                    .replace(R.id.container, getFragmentManager().findFragmentByTag(GAME_MODE_CHOOSER))
                    .commit();
        } else if (getFragmentManager()
                .getFragment(savedInstanceState, NAMEGAME) != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, getFragmentManager().findFragmentByTag(NAMEGAME))
                    .commit();
        } else if (getFragmentManager()
                .getFragment(savedInstanceState, ENDGAME) != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, getFragmentManager().findFragmentByTag(ENDGAME))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NameGameActivity.this);

        alertDialog.setTitle("Abort Game?")
                .setMessage("What would you like to do?");
        alertDialog.setPositiveButton("Change Game Type", (dialog, which) -> loadGameChooserFragment());
        if (canStartNewGame) {
            alertDialog.setNeutralButton("Start Game Over", (dialog, which) -> startNewGame());
        }
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void loadLoaderFragment() {
        canStartNewGame = false;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        LoadingFragment fragment = LoadingFragment.newInstance();
        ft.replace(R.id.container, fragment, LOADER);
        ft.commit();
    }

    private void loadGameChooserFragment() {
        canStartNewGame = false;
        GameModeFragment nextFragment = GameModeFragment.newInstance();
        performFragmentTransition(nextFragment, GAME_MODE_CHOOSER);
    }

    private void loadGameFragment() {
        canStartNewGame = true;
        NameGameFragment nextFragment = NameGameFragment.newInstance();
        performFragmentTransition(nextFragment, NAMEGAME);
    }

    private void loadEndgameFragment() {
        canStartNewGame = true;
        EndgameFragment nextFragment = EndgameFragment.newInstance();
        performFragmentTransition(nextFragment, ENDGAME);
    }

    private void performFragmentTransition(@NonNull Fragment to, String tag) {
        if (isDestroyed()) {
            return;
        }
        Fragment previousFragment = mFragmentManager.findFragmentById(R.id.container);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fade exitFade = new Fade();
        exitFade.setDuration(FADE_DEFAULT_TIME);
        previousFragment.setExitTransition(exitFade);

        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(FADE_DEFAULT_TIME);
        enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);

        Fade enterFade = new Fade();
        enterFade.setStartDelay(FADE_DEFAULT_TIME);
        enterFade.setDuration(FADE_DEFAULT_TIME);
        to.setEnterTransition(enterFade);

        fragmentTransaction.replace(R.id.container, to, tag);
        Log.d("performFragmentTransition", String.format("replacing fragment with %s", tag));
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
    }

    @Override
    public void startNewGame() {
        mScore = new Score(0, 0);
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
        mHighScoreProvider.clearHighScore(getApplicationContext());
    }

    @Override
    public void gameOver() {
        loadEndgameFragment();
    }

    @Override
    public void playAgain() {
        mScore = new Score(0, 0);
        loadGameFragment();
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void chooseNewGameType() {
        mScore = new Score(0, 0);
        loadGameChooserFragment();
    }

    @Override
    public void allDone() {
        finish();
    }

    @Override
    public void updateHighScore(@NonNull Score score) {
        if (mHighScoreProvider.isNewHighScore(score)) {
            mHighScoreProvider.setHighScore(getApplicationContext(), score);
        }
    }

    @Override
    public void onLoadFinished(@NonNull ArrayList<Person> people) {
        loadGameChooserFragment();
    }

    @Override
    public void onError(@NonNull Throwable error) {

        new AlertDialog.Builder(this)
                .setTitle("Failed Load")
                .setCancelable(false)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setMessage("Unable to download game data")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .show();
    }
}
