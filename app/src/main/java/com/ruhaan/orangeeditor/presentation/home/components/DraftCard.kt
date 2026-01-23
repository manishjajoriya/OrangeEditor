package com.ruhaan.orangeeditor.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ruhaan.orangeeditor.R
import com.ruhaan.orangeeditor.data.entity.EditorStateEntity
import com.ruhaan.orangeeditor.presentation.theme.Typography
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DraftCard(
    modifier: Modifier = Modifier,
    draft: EditorStateEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
  Column(
      modifier = modifier.clickable(onClick = onClick),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box {
      AsyncImage(
          model = draft.previewUrl,
          contentDescription = null,
          modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)),
          contentScale = ContentScale.Crop,
      )

      IconButton(
          onClick = onDeleteClick,
          modifier =
              Modifier.align(Alignment.TopEnd)
                  .padding(6.dp)
                  .size(32.dp)
                  .background(color = Color.Red.copy(alpha = 0.5f), shape = CircleShape),
      ) {
        Icon(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = "delete",
            tint = Color.White,
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = draft.fileName,
        modifier = Modifier.fillMaxWidth(),
        style = Typography.titleMedium.copy(fontSize = 20.sp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )

    Text(
        text = "Updated ${formatCreatedAt(draft.timestamp)}",
        modifier = Modifier.fillMaxWidth(),
        style = Typography.titleSmall.copy(fontWeight = FontWeight.Normal),
    )
  }
}

private fun formatCreatedAt(timestamp: OffsetDateTime): String {
  val now = OffsetDateTime.now()

  val seconds = ChronoUnit.SECONDS.between(timestamp, now)
  val minutes = ChronoUnit.MINUTES.between(timestamp, now)
  val hours = ChronoUnit.HOURS.between(timestamp, now)
  val days = ChronoUnit.DAYS.between(timestamp, now)

  return when {
    seconds < 60 -> "Just now"
    minutes < 60 -> "$minutes min ago"
    hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
    days == 1L -> "Yesterday"
    days < 7 -> "$days days ago"
    else -> {
      val formatter =
          if (timestamp.year == now.year) DateTimeFormatter.ofPattern("dd MMM")
          else DateTimeFormatter.ofPattern("dd MMM yyyy")
      timestamp.format(formatter)
    }
  }
}
