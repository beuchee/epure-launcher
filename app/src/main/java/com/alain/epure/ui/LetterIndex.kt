package com.alain.epure.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alain.epure.R
import com.alain.epure.domain.AlphabetIndex
import com.alain.epure.ui.theme.LabelStyle
import com.alain.epure.ui.theme.TextSecondary

// Vertical index of the letters present in the list (F-1.6). A touch or a drag selects a letter.
@Composable
fun LetterIndex(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit
) {
    var height by remember { mutableIntStateOf(0) }
    val currentOnSelected by rememberUpdatedState(onLetterSelected)

    // TalkBack cannot drag: each letter is also offered as a custom action.
    val actions = letters.map { letter ->
        val label = if (letter == AlphabetIndex.OTHER) {
            stringResource(R.string.index_go_other)
        } else {
            stringResource(R.string.index_go_to, letter)
        }
        CustomAccessibilityAction(label) {
            currentOnSelected(letter)
            true
        }
    }

    fun select(y: Float) {
        if (height == 0 || letters.isEmpty()) return
        val position = (y / height * letters.size).toInt().coerceIn(0, letters.lastIndex)
        currentOnSelected(letters[position])
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(40.dp)
            .onSizeChanged { height = it.height }
            .semantics { set(SemanticsActions.CustomActions, actions) }
            .pointerInput(letters) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    select(down.position.y)
                    drag(down.id) { change ->
                        select(change.position.y)
                        change.consume()
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        letters.forEach { letter ->
            Text(
                text = letter.toString(),
                style = LabelStyle,
                color = TextSecondary
            )
        }
    }
}
