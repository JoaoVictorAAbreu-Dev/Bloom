package com.bloom.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomPixelFlower
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomPixelPlant
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onStart: () -> Unit,
) {
    val pages = listOf(
        OnboardingPage(
            title = "Cultive pequenos hábitos",
            description = "Transform everyday routines into calm, visible progress.",
            helper = "A few minutes a day is enough to make the garden grow.",
            painter = { BloomPixelPlant() },
        ),
        OnboardingPage(
            title = "Foque com Pomodoro",
            description = "Work in gentle focus rounds with intentional breaks.",
            helper = "Keep your attention soft, steady, and measurable.",
            painter = { BloomPixelMascot() },
        ),
        OnboardingPage(
            title = "Veja seu jardim crescer",
            description = "Collect rewards, unlock plants, and celebrate streaks.",
            helper = "Small wins bloom into a calmer, more productive day.",
            painter = { BloomPixelFlower() },
        ),
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomColors.Background)
            .padding(horizontal = BloomSpacing.screenPadding, vertical = BloomSpacing.xxl),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Bloom",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Grow a little every day.",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            BloomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = BloomSpacing.xl),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    pages[page].painter()
                }
                Text(
                    text = pages[page].title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = BloomSpacing.xl),
                )
                Text(
                    text = pages[page].description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = BloomSpacing.sm),
                )
                Text(
                    text = pages[page].helper,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = BloomSpacing.md),
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            PageIndicators(
                currentPage = pagerState.currentPage,
                pageCount = pages.size,
            )
            BloomButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Começar",
                onClick = onStart,
            )
            BloomButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (pagerState.currentPage < pages.lastIndex) "Próximo" else "Explorar o Bloom",
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onStart()
                    }
                },
            )
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String,
    val helper: String,
    val painter: @Composable () -> Unit,
)

@Composable
private fun PageIndicators(
    currentPage: Int,
    pageCount: Int,
) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (selected) 22.dp else 10.dp)
                    .height(10.dp)
                    .background(
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = BloomRadius.pill,
                    )
                    ,
            )
        }
    }
}
