package com.tunnaduong.meovat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.ui.AppViewModel
import com.tunnaduong.meovat.ui.components.CategoryCard
import com.tunnaduong.meovat.ui.components.PageHeader
import com.tunnaduong.meovat.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: AppViewModel, state: AppState, onCategory: (String) -> Unit) {
    val l = state.l10n
    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        PageHeader(l.homeTitle, l.homeSubtitle)
        PullToRefreshBox(isRefreshing = state.isSyncing, onRefresh = vm::refresh) {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.categories, key = { it.id }) { category ->
                    CategoryCard(category) { onCategory(category.id) }
                }
            }
        }
    }
}
