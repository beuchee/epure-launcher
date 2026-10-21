package com.alain.epure.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.domain.Customization
import com.alain.epure.ui.theme.AppNameStyle
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextPrimary
import com.alain.epure.ui.theme.TextSecondary

// Paramètres > Favoris: remove, move, and add empty spaces (F-2.1, F-2.3).
@Composable
fun FavoritesScreen(viewModel: AppsViewModel) {
    val favorites by viewModel.favorites.collectAsState()
    val context = LocalContext.current
    val favoritesFull = stringResource(R.string.favorites_full, Customization.MAX_FAVORITES)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.favorites_title),
            style = AppNameStyle,
            color = TextPrimary
        )
        if (favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.favorites_none),
                style = LabelStyle,
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
        favorites.forEachIndexed { index, slot ->
            Column(Modifier.padding(top = 24.dp)) {
                Text(
                    text = slot.app?.label ?: stringResource(R.string.favorites_space),
                    style = AppNameStyle,
                    color = TextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    if (index > 0) {
                        TextAction(stringResource(R.string.favorites_move_up)) {
                            viewModel.moveFavorite(index, -1)
                        }
                    }
                    if (index < favorites.lastIndex) {
                        TextAction(stringResource(R.string.favorites_move_down)) {
                            viewModel.moveFavorite(index, 1)
                        }
                    }
                    TextAction(stringResource(R.string.favorites_remove)) {
                        viewModel.removeFavorite(index)
                    }
                }
            }
        }
        ActionRow(
            text = AnnotatedString(stringResource(R.string.favorites_add_space)),
            onClick = {
                if (!viewModel.addSpace()) {
                    Toast.makeText(context, favoritesFull, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}
