package com.varcustom.namegame.util;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Random;

/**
 * Created by hdarby on 12/14/2017.
 */

public class GenericChooser {

    public static <T> T chooseOneFromList(@NonNull List<T> things) {
        return things.get(new Random().nextInt(things.size()));
    }
}
