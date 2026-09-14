package com.jorotayo.algorubickrevamped.ui.notation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.theme.Accent
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun NotationScreen() {
    val pages = NotationPage.entries
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Primary,
            contentColor = White,
            edgePadding = 8.dp,
        ) {
            pages.forEachIndexed { index, page ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = stringResource(page.titleRes),
                            color = if (selected) Accent else White.copy(alpha = 0.72f),
                        )
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (pages[page]) {
                NotationPage.Intro -> IntroPage()
                NotationPage.Faces -> FacesPage()
                NotationPage.Moves -> MovesPage()
                NotationPage.Doubles -> DoublesPage()
                NotationPage.TwoLayer -> TwoLayerPage()
                NotationPage.Slices -> SlicesPage()
                NotationPage.Rotations -> RotationsPage()
                NotationPage.Algorithms -> AlgorithmsPage()
            }
        }
    }
}

private val NotationPage.titleRes: Int
    get() = when (this) {
        NotationPage.Intro -> R.string.notation_tab_intro
        NotationPage.Faces -> R.string.notation_tab_faces
        NotationPage.Moves -> R.string.notation_tab_moves
        NotationPage.Doubles -> R.string.notation_tab_doubles
        NotationPage.TwoLayer -> R.string.notation_tab_two_layer
        NotationPage.Slices -> R.string.notation_tab_slices
        NotationPage.Rotations -> R.string.notation_tab_rotations
        NotationPage.Algorithms -> R.string.notation_tab_algorithms
    }

@Composable
private fun IntroPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_intro_header_title),
            body = stringResource(R.string.notation_intro_header_body),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.icons8_swipe_80),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(80.dp),
            )
            Text(
                text = stringResource(R.string.notation_intro_gestures_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun FacesPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_faces_header_title),
            body = stringResource(R.string.notation_faces_header_body),
        )
        FaceRow(R.string.notation_faces_left, R.drawable.left_face)
        FaceRow(R.string.notation_faces_right, R.drawable.right_face)
        FaceRow(R.string.notation_faces_front, R.drawable.front_face)
        FaceRow(R.string.notation_faces_back, R.drawable.back_face)
        FaceRow(R.string.notation_faces_up, R.drawable.up_face)
        FaceRow(R.string.notation_faces_down, R.drawable.down_face)
    }
}

@Composable
private fun MovesPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_moves_header_title),
            body = stringResource(R.string.notation_moves_header_body),
        )
        MovePair(R.string.notation_moves_clockwise_r, R.drawable.clockwise_r, R.string.notation_moves_anticlockwise_r, R.drawable.anticlockwise_r)
        MovePair(R.string.notation_moves_clockwise_l, R.drawable.clockwise_l, R.string.notation_moves_anticlockwise_l, R.drawable.anticlockwise_l)
        MovePair(R.string.notation_moves_clockwise_u, R.drawable.clockwise_u, R.string.notation_moves_anticlockwise_u, R.drawable.anticlockwise_u)
        MovePair(R.string.notation_moves_clockwise_d, R.drawable.clockwise_d, R.string.notation_moves_anticlockwise_d, R.drawable.anticlockwise_d)
        MovePair(R.string.notation_moves_clockwise_f, R.drawable.clockwise_f, R.string.notation_moves_anticlockwise_f, R.drawable.anticlockwise_f)
        MovePair(R.string.notation_moves_clockwise_b, R.drawable.clockwise_b, R.string.notation_moves_anticlockwise_b, R.drawable.anticlockwise_b)
    }
}

@Composable
private fun DoublesPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_doubles_header_title),
            body = stringResource(R.string.notation_doubles_header_body),
        )
        MovePair(R.string.notation_doubles_r, R.drawable.double_r, R.string.notation_doubles_l, R.drawable.double_l)
        MovePair(R.string.notation_doubles_u, R.drawable.double_u, R.string.notation_doubles_d, R.drawable.double_d)
        MovePair(R.string.notation_doubles_f, R.drawable.double_f, R.string.notation_doubles_b, R.drawable.double_b)
    }
}

@Composable
private fun TwoLayerPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_two_layer_header_title),
            body = stringResource(R.string.notation_two_layer_header_body),
        )
        FaceRow(R.string.notation_two_layer_r, R.drawable.two_right)
        FaceRow(R.string.notation_two_layer_l, R.drawable.two_left)
        FaceRow(R.string.notation_two_layer_u, R.drawable.two_up)
        FaceRow(R.string.notation_two_layer_d, R.drawable.two_down)
        FaceRow(R.string.notation_two_layer_f, R.drawable.two_front)
        FaceRow(R.string.notation_two_layer_b, R.drawable.two_back)
    }
}

@Composable
private fun SlicesPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_slices_header_title),
            body = stringResource(R.string.notation_slices_header_body),
        )
        FaceRow(R.string.notation_slices_m, R.drawable.m_slice)
        FaceRow(R.string.notation_slices_e, R.drawable.e_slice)
        FaceRow(R.string.notation_slices_s, R.drawable.s_slice)
        HeaderCard(
            title = stringResource(R.string.notation_slices_extra_header),
            body = stringResource(R.string.notation_slices_extra_body),
        )
    }
}

@Composable
private fun RotationsPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_rotations_header_title),
            body = stringResource(R.string.notation_rotations_header_body),
        )
        FaceRow(R.string.notation_rotations_x, R.drawable.x_rotation)
        FaceRow(R.string.notation_rotations_y, R.drawable.y_rotation)
        FaceRow(R.string.notation_rotations_z, R.drawable.z_rotation)
        HeaderCard(
            title = stringResource(R.string.notation_rotations_extra_header),
            body = stringResource(R.string.notation_rotations_extra_body),
        )
    }
}

@Composable
private fun AlgorithmsPage() {
    NotationScrollColumn {
        HeaderCard(
            title = stringResource(R.string.notation_algorithms_header_title),
            body = stringResource(R.string.notation_algorithms_header_body),
        )
        Image(
            painter = painterResource(R.drawable.fru_algorithm),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(180.dp),
        )
    }
}

@Composable
private fun NotationScrollColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        content()
    }
}

@Composable
private fun HeaderCard(title: String, body: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun FaceRow(@StringRes textRes: Int, @DrawableRes imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(textRes).replace(Regex("<[^>]*>"), ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
            )
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
        }
    }
}

@Composable
private fun MovePair(
    @StringRes leftText: Int,
    @DrawableRes leftImage: Int,
    @StringRes rightText: Int,
    @DrawableRes rightImage: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        MoveCard(leftText, leftImage, Modifier.weight(1f))
        MoveCard(rightText, rightImage, Modifier.weight(1f))
    }
}

@Composable
private fun MoveCard(
    @StringRes textRes: Int,
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@DefaultPreviews
@Composable
private fun IntroPagePreview() {
    AlgorubickTheme {
        IntroPage()
    }
}

@DefaultPreviews
@Composable
private fun FacesPagePreview() {
    AlgorubickTheme {
        FacesPage()
    }
}
