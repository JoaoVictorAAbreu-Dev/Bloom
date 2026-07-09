package com.bloom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing

data class BloomBottomBarItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun BloomBottomBar(
    items: List<BloomBottomBarItem>,
    currentRoute: String,
    onItemSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(MaterialTheme.colorScheme.surface, BloomRadius.large)
            .padding(horizontal = BloomSpacing.md)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val selectedColor = MaterialTheme.colorScheme.primary
            val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            Column(
                modifier = Modifier
                    .weight(1f)
                    .sizeIn(minHeight = 56.dp)
                    .semantics {
                        contentDescription = item.label
                    }
                    .selectable(
                        selected = selected,
                        onClick = { onItemSelected(item.route) },
                        role = Role.Tab,
                    )
                    .padding(vertical = BloomSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (selected) selectedColor else unselectedColor,
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
                    color = if (selected) selectedColor else unselectedColor,
                )
                if (selected) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(6.dp)) {
                        drawCircle(color = selectedColor)
                    }
                } else {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                }
            }
        }
    }
}

fun bloomBottomBarItems() = listOf(
    BloomBottomBarItem("home", "Home", Icons.Rounded.Home),
    BloomBottomBarItem("habits", "Habits", Icons.Rounded.LocalFlorist),
    BloomBottomBarItem("focus", "Focus", Icons.Rounded.Timer),
    BloomBottomBarItem("coach", "Coach", Icons.Rounded.AutoAwesome),
    BloomBottomBarItem("stats", "Stats", Icons.Rounded.BarChart),
    BloomBottomBarItem("profile", "Profile", Icons.Rounded.Person),
)
