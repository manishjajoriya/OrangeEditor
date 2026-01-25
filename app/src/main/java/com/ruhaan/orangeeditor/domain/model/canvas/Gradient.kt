package com.ruhaan.orangeeditor.domain.model.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class Gradient(val points: List<List<Pair<Offset, Color>>>) {

  SUNSET(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFF8A65),
              Offset(0.5f, 0f) to Color(0xFFFF7043),
              Offset(1f, 0f) to Color(0xFFF4511E),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFAB47BC),
              Offset(0.5f, 0.5f) to Color(0xFF8E24AA),
              Offset(1f, 0.5f) to Color(0xFF6A1B9A),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF1A237E),
              Offset(0.5f, 1f) to Color(0xFF0D47A1),
              Offset(1f, 1f) to Color(0xFF002171),
          ),
      )
  ),
  ICE_BLUE(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFE0F7FA),
              Offset(0.5f, 0f) to Color(0xFFB2EBF2),
              Offset(1f, 0f) to Color(0xFF80DEEA),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF4DD0E1),
              Offset(0.5f, 0.5f) to Color(0xFF26C6DA),
              Offset(1f, 0.5f) to Color(0xFF00BCD4),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF0097A7),
              Offset(0.5f, 1f) to Color(0xFF00838F),
              Offset(1f, 1f) to Color(0xFF006064),
          ),
      )
  ),
  NATURE(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFE8F5E9),
              Offset(0.5f, 0f) to Color(0xFFC8E6C9),
              Offset(1f, 0f) to Color(0xFFA5D6A7),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF66BB6A),
              Offset(0.5f, 0.5f) to Color(0xFF43A047),
              Offset(1f, 0.5f) to Color(0xFF2E7D32),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF1B5E20),
              Offset(0.5f, 1f) to Color(0xFF0B3D14),
              Offset(1f, 1f) to Color(0xFF052E0A),
          ),
      )
  ),
  NEON_DARK(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF00E5FF),
              Offset(0.5f, 0f) to Color(0xFF00B8D4),
              Offset(1f, 0f) to Color(0xFF0091EA),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF7C4DFF),
              Offset(0.5f, 0.5f) to Color(0xFF651FFF),
              Offset(1f, 0.5f) to Color(0xFF6200EA),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF212121),
              Offset(0.5f, 1f) to Color(0xFF121212),
              Offset(1f, 1f) to Color(0xFF000000),
          ),
      )
  ),
  INSTAGRAM(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF833AB4),
              Offset(0.5f, 0f) to Color(0xFFC13584),
              Offset(1f, 0f) to Color(0xFFE1306C),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFD1D1D),
              Offset(0.5f, 0.5f) to Color(0xFFF77737),
              Offset(1f, 0.5f) to Color(0xFFFCAF45),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFFDC80),
              Offset(0.5f, 1f) to Color(0xFFFED576),
              Offset(1f, 1f) to Color(0xFFFCCC63),
          ),
      )
  ),
  PEACH_DREAM(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFFE5E5),
              Offset(0.5f, 0f) to Color(0xFFFFCDD2),
              Offset(1f, 0f) to Color(0xFFFFB3BA),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFFAEB9),
              Offset(0.5f, 0.5f) to Color(0xFFFFB3C1),
              Offset(1f, 0.5f) to Color(0xFFFFC2D1),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFFDAE9),
              Offset(0.5f, 1f) to Color(0xFFFBE4F1),
              Offset(1f, 1f) to Color(0xFFF5EEF8),
          ),
      )
  ),
  OCEAN_BREEZE(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF667EEA),
              Offset(0.5f, 0f) to Color(0xFF5B73E8),
              Offset(1f, 0f) to Color(0xFF4F68E6),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF4F9FE8),
              Offset(0.5f, 0.5f) to Color(0xFF4FC3F7),
              Offset(1f, 0.5f) to Color(0xFF4DD0E1),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF50E3C2),
              Offset(0.5f, 1f) to Color(0xFF5EEAB5),
              Offset(1f, 1f) to Color(0xFF6EF1A8),
          ),
      )
  ),
  FIRE_BLAZE(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFF0844),
              Offset(0.5f, 0f) to Color(0xFFFF3355),
              Offset(1f, 0f) to Color(0xFFFF5566),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFF6B35),
              Offset(0.5f, 0.5f) to Color(0xFFFF8534),
              Offset(1f, 0.5f) to Color(0xFFFFA033),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFFBB32),
              Offset(0.5f, 1f) to Color(0xFFFFD731),
              Offset(1f, 1f) to Color(0xFFFFF030),
          ),
      )
  ),
  PURPLE_HAZE(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF9D50BB),
              Offset(0.5f, 0f) to Color(0xFFB357C3),
              Offset(1f, 0f) to Color(0xFFC95FCB),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFD766D3),
              Offset(0.5f, 0.5f) to Color(0xFFE56DDB),
              Offset(1f, 0.5f) to Color(0xFFF374E3),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFED7BEB),
              Offset(0.5f, 1f) to Color(0xFFE782F3),
              Offset(1f, 1f) to Color(0xFFE189FB),
          ),
      )
  ),
  MINT_FRESH(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFB8F3FF),
              Offset(0.5f, 0f) to Color(0xFFA8F0F7),
              Offset(1f, 0f) to Color(0xFF98EDEE),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF88E9E5),
              Offset(0.5f, 0.5f) to Color(0xFF78E6DC),
              Offset(1f, 0.5f) to Color(0xFF68E3D3),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF58E0CA),
              Offset(0.5f, 1f) to Color(0xFF48DDC1),
              Offset(1f, 1f) to Color(0xFF38DAB8),
          ),
      )
  ),
  COSMIC_FUSION(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF1E3A8A),
              Offset(0.5f, 0f) to Color(0xFF3B4C9F),
              Offset(1f, 0f) to Color(0xFF5B5EB4),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF7B70C9),
              Offset(0.5f, 0.5f) to Color(0xFF9B82DE),
              Offset(1f, 0.5f) to Color(0xFFBB94F3),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFD1A6FF),
              Offset(0.5f, 1f) to Color(0xFFE1B8FF),
              Offset(1f, 1f) to Color(0xFFF1CAFF),
          ),
      )
  ),
  CANDY_POP(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFF6FD8),
              Offset(0.5f, 0f) to Color(0xFFFF7CE4),
              Offset(1f, 0f) to Color(0xFFFF89F0),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFF96FC),
              Offset(0.5f, 0.5f) to Color(0xFFFFA3FF),
              Offset(1f, 0.5f) to Color(0xFFFFB0FF),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFFBDFF),
              Offset(0.5f, 1f) to Color(0xFFFFCAFF),
              Offset(1f, 1f) to Color(0xFFFFD7FF),
          ),
      )
  ),
  TROPICAL_PARADISE(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF00D4FF),
              Offset(0.5f, 0f) to Color(0xFF00D9DB),
              Offset(1f, 0f) to Color(0xFF00DEB7),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF00E393),
              Offset(0.5f, 0.5f) to Color(0xFF3DE86F),
              Offset(1f, 0.5f) to Color(0xFF7AED4B),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFB7F227),
              Offset(0.5f, 1f) to Color(0xFFD4F715),
              Offset(1f, 1f) to Color(0xFFF1FC03),
          ),
      )
  ),
  ROSE_GOLD(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFFC3A0),
              Offset(0.5f, 0f) to Color(0xFFFFB4A0),
              Offset(1f, 0f) to Color(0xFFFFA5A0),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFF96A0),
              Offset(0.5f, 0.5f) to Color(0xFFFF87A0),
              Offset(1f, 0.5f) to Color(0xFFFF78A0),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFF69A0),
              Offset(0.5f, 1f) to Color(0xFFFF5AA0),
              Offset(1f, 1f) to Color(0xFFFF4BA0),
          ),
      )
  ),
  DARK_GALAXY(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF0F0C29),
              Offset(0.5f, 0f) to Color(0xFF1A1634),
              Offset(1f, 0f) to Color(0xFF24203F),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF2E2A4A),
              Offset(0.5f, 0.5f) to Color(0xFF383455),
              Offset(1f, 0.5f) to Color(0xFF423E60),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF4C486B),
              Offset(0.5f, 1f) to Color(0xFF565276),
              Offset(1f, 1f) to Color(0xFF605C81),
          ),
      )
  ),
  EMERALD_WATER(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF348F50),
              Offset(0.5f, 0f) to Color(0xFF56B4D3),
              Offset(1f, 0f) to Color(0xFF78D9FF),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF88E3FF),
              Offset(0.5f, 0.5f) to Color(0xFF98EDFF),
              Offset(1f, 0.5f) to Color(0xFFA8F7FF),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFB8FFFF),
              Offset(0.5f, 1f) to Color(0xFFC8FFFF),
              Offset(1f, 1f) to Color(0xFFD8FFFF),
          ),
      )
  ),
  LAVA_LAMP(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFF0080),
              Offset(0.5f, 0f) to Color(0xFFFF0066),
              Offset(1f, 0f) to Color(0xFFFF004D),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFF3333),
              Offset(0.5f, 0.5f) to Color(0xFFFF6619),
              Offset(1f, 0.5f) to Color(0xFFFF9900),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFFCC00),
              Offset(0.5f, 1f) to Color(0xFFFFE600),
              Offset(1f, 1f) to Color(0xFFFFFF00),
          ),
      )
  ),
  AURORA_BOREALIS(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF00FFA3),
              Offset(0.5f, 0f) to Color(0xFF00E5C3),
              Offset(1f, 0f) to Color(0xFF00CCE3),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF00B2FF),
              Offset(0.5f, 0.5f) to Color(0xFF3D99FF),
              Offset(1f, 0.5f) to Color(0xFF7A80FF),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFB766FF),
              Offset(0.5f, 1f) to Color(0xFFD34DFF),
              Offset(1f, 1f) to Color(0xFFEF33FF),
          ),
      )
  ),
  HONEY_DEW(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFFFFF9E6),
              Offset(0.5f, 0f) to Color(0xFFFFF4CC),
              Offset(1f, 0f) to Color(0xFFFFEFB3),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFFFFEA99),
              Offset(0.5f, 0.5f) to Color(0xFFFFE580),
              Offset(1f, 0.5f) to Color(0xFFFFE066),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFFFFDB4D),
              Offset(0.5f, 1f) to Color(0xFFFFD633),
              Offset(1f, 1f) to Color(0xFFFFD11A),
          ),
      )
  ),
  MIDNIGHT_CITY(
      listOf(
          listOf(
              Offset(0f, 0f) to Color(0xFF232526),
              Offset(0.5f, 0f) to Color(0xFF2D2F31),
              Offset(1f, 0f) to Color(0xFF37393C),
          ),
          listOf(
              Offset(0f, 0.5f) to Color(0xFF414347),
              Offset(0.5f, 0.5f) to Color(0xFF4B4D52),
              Offset(1f, 0.5f) to Color(0xFF55575D),
          ),
          listOf(
              Offset(0f, 1f) to Color(0xFF5F6168),
              Offset(0.5f, 1f) to Color(0xFF696B73),
              Offset(1f, 1f) to Color(0xFF73757E),
          ),
      )
  ),
}
