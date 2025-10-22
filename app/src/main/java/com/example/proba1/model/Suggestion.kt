package com.example.proba1.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Suggestion(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
)
