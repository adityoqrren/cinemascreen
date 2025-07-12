package com.myapp.cinemascreen.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun <T> EasyGrid(
    columnCount: Int,
    paddingValues: PaddingValues,
    verticalSpace: Dp,
    horizontalSpace: Arrangement.Horizontal,
    list: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    Column(
        modifier = modifier.padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(space = verticalSpace)
    ) {
        for (i in list.indices step columnCount) {
            Row(
                horizontalArrangement = horizontalSpace,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in 0 until columnCount) {

                    if ((i + j) < list.size) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            content(list[i + j])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f, fill = true))
                    }

                }
            }
        }
    }
}