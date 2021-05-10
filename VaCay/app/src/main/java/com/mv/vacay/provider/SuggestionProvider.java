package com.mv.vacay.provider;

/**
 * Created by a on 2017.01.03.
 */

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by sodha on 4/3/16.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.mv.vacay.provider.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
