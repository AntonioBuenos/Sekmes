package com.example.sekmeszodynas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sekmeszodynas.ui.theme.SekmesSpacing
import com.example.sekmeszodynas.ui.theme.SekmesZodynasTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SekmesTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XxxSmall)) {
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            onBack?.let {
                IconButton(onClick = it) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
                }
            }
        },
        actions = { actions?.invoke() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

@Composable
fun SekmesSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}

@Composable
fun SekmesActionRow(
    title: String,
    supportingText: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val containerColor = if (highlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val contentColor = if (highlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 76.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(SekmesSpacing.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(SekmesSpacing.Small))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = supportingText,
                    modifier = Modifier.padding(top = SekmesSpacing.XxxSmall),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun SekmesCourseCard(
    title: String,
    description: String,
    metadata: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(SekmesSpacing.Small)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = description,
                modifier = Modifier.padding(top = SekmesSpacing.XxSmall),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = metadata,
                modifier = Modifier.padding(top = SekmesSpacing.XSmall),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

enum class AnswerOptionState { Idle, Selected, Correct, Incorrect }

enum class SekmesStatusTone { Neutral, Learning, Hard, Known }

data class SekmesStatusOption(
    val id: String,
    val label: String,
    val tone: SekmesStatusTone,
)

@Composable
fun SekmesStatusChip(
    label: String,
    tone: SekmesStatusTone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val leadingIcon = when (tone) {
        SekmesStatusTone.Known -> Icons.Rounded.CheckCircle
        SekmesStatusTone.Hard -> Icons.Rounded.ErrorOutline
        else -> null
    }
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, modifier = Modifier.size(18.dp)) } },
    )
}

@Composable
fun SekmesWordRow(
    word: String,
    translation: String,
    status: SekmesStatusOption,
    statusOptions: List<SekmesStatusOption>,
    onStatusSelected: (SekmesStatusOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SekmesSpacing.Small, vertical = SekmesSpacing.XSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(word, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    translation,
                    modifier = Modifier.padding(top = SekmesSpacing.XxxSmall),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(SekmesSpacing.XSmall))
            Box {
                SekmesStatusChip(status.label, status.tone, onClick = { statusMenuExpanded = true })
                DropdownMenu(expanded = statusMenuExpanded, onDismissRequest = { statusMenuExpanded = false }) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            leadingIcon = {
                                when (option.tone) {
                                    SekmesStatusTone.Known -> Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                                    SekmesStatusTone.Hard -> Icon(Icons.Rounded.ErrorOutline, contentDescription = null)
                                    else -> Icon(Icons.Rounded.MoreVert, contentDescription = null)
                                }
                            },
                            onClick = {
                                statusMenuExpanded = false
                                if (option.id != status.id) onStatusSelected(option)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SekmesAnswerOption(
    label: String,
    option: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: AnswerOptionState = AnswerOptionState.Idle,
    enabled: Boolean = true,
) {
    val (containerColor, contentColor) = when (state) {
        AnswerOptionState.Idle -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
        AnswerOptionState.Selected -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        AnswerOptionState.Correct -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        AnswerOptionState.Incorrect -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    OutlinedCard(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 60.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor, contentColor = contentColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SekmesSpacing.Small, vertical = SekmesSpacing.XSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(SekmesSpacing.Small))
            Text(option, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            when (state) {
                AnswerOptionState.Correct -> Icon(Icons.Rounded.CheckCircle, contentDescription = "Правильный ответ")
                AnswerOptionState.Incorrect -> Icon(Icons.Rounded.ErrorOutline, contentDescription = "Неправильный ответ")
                else -> Unit
            }
        }
    }
}

@Composable
fun SekmesEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Info,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(SekmesSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SekmesComponentsPreview() {
    SekmesZodynasTheme {
        Column(
            modifier = Modifier.padding(SekmesSpacing.Small),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
        ) {
            SekmesActionRow(
                title = "Учить слова",
                supportingText = "Словарь по темам",
                icon = Icons.Rounded.CheckCircle,
                highlighted = true,
                onClick = {},
            )
            SekmesCourseCard(
                title = "Nė dienos be lietuvių kalbos",
                description = "Курс литовского языка для ежедневной практики.",
                metadata = "12 тем · 690 слов",
                onClick = {},
            )
            SekmesAnswerOption("A", "šaltibarščiai", onClick = {})
        }
    }
}
