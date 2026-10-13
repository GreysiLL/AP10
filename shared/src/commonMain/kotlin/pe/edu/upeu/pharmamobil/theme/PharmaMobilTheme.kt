package pe.edu.upeu.pharmamobil.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF246D62), onPrimary = Color.White,
    primaryContainer = Color(0xFFE4F1EC), onPrimaryContainer = Color(0xFF174C43),
    secondary = Color(0xFF526982), onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAF0F7), onSecondaryContainer = Color(0xFF334E68),
    tertiary = Color(0xFF8D6840), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF6EBDC), onTertiaryContainer = Color(0xFF634521),
    background = Color(0xFFF5F7F9), onBackground = Color(0xFF202D38),
    surface = Color.White, onSurface = Color(0xFF202D38),
    surfaceVariant = Color(0xFFEEF1F4), onSurfaceVariant = Color(0xFF607080),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF7F9FA),
    surfaceContainer = Color(0xFFF0F3F5), surfaceContainerHigh = Color(0xFFEAF0F2),
    surfaceContainerHighest = Color(0xFFE5EAEE),
    outline = Color(0xFF8998A5), outlineVariant = Color(0xFFDCE3E8),
    error = Color(0xFFB43D3D), onError = Color.White,
    errorContainer = Color(0xFFFCECEC), onErrorContainer = Color(0xFF8A2C2C)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF91CEBA), onPrimary = Color(0xFF123D33),
    primaryContainer = Color(0xFF25483E), onPrimaryContainer = Color(0xFFC1E9DB),
    secondary = Color(0xFFB6CAE0), onSecondary = Color(0xFF23384D),
    secondaryContainer = Color(0xFF2C4055), onSecondaryContainer = Color(0xFFD5E3F2),
    tertiary = Color(0xFFE0BF98), onTertiary = Color(0xFF43311C),
    tertiaryContainer = Color(0xFF564129), onTertiaryContainer = Color(0xFFF2DDC2),
    background = Color(0xFF121A22), onBackground = Color(0xFFE2E8ED),
    surface = Color(0xFF1C2732), onSurface = Color(0xFFE2E8ED),
    surfaceVariant = Color(0xFF293641), onSurfaceVariant = Color(0xFFAFBBC6),
    surfaceContainerLowest = Color(0xFF10171E), surfaceContainerLow = Color(0xFF18222C),
    surfaceContainer = Color(0xFF1F2B36), surfaceContainerHigh = Color(0xFF283541),
    surfaceContainerHighest = Color(0xFF32414E),
    outline = Color(0xFF8293A3), outlineVariant = Color(0xFF384854),
    error = Color(0xFFF0AAAA), onError = Color(0xFF5C2020),
    errorContainer = Color(0xFF572E32), onErrorContainer = Color(0xFFFFDADA)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 23.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 27.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 23.sp),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 21.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 17.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
)

@Composable
fun PharmaMobilTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if(darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = Shapes(extraSmall = RoundedCornerShape(6.dp), small = RoundedCornerShape(10.dp), medium = RoundedCornerShape(14.dp), large = RoundedCornerShape(20.dp), extraLarge = RoundedCornerShape(24.dp)),
        content = content
    )
}
