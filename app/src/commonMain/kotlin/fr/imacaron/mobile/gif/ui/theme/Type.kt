package fr.imacaron.mobile.gif.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
expect fun font(name: String, res: String, weight: FontWeight, style: FontStyle): Font

@Composable
fun getJosefin() = FontFamily(
    font("Josefin Sans Bold", "josefin_sans_bold", FontWeight.Bold, FontStyle.Normal),
    font("Josefin Sans Bold Italic", "josefin_sans_bold_italic", FontWeight.Bold, FontStyle.Italic),
    font("Josefin Sans Extra Light", "josefin_sans_bold_extra_light", FontWeight.ExtraLight, FontStyle.Normal),
    font("Josefin Sans Extra Light Italic", "josefin_sans_bold_extra_light_italic", FontWeight.ExtraLight, FontStyle.Italic),
    font("Josefin Sans Italic", "josefin_sans_italic", FontWeight.Normal, FontStyle.Italic),
    font("Josefin Sans Light", "josefin_sans_light", FontWeight.Light, FontStyle.Normal),
    font("Josefin Sans Light Italic", "josefin_sans_light_italic", FontWeight.Light, FontStyle.Italic),
    font("Josefin Sans Medium", "josefin_sans_medium", FontWeight.Medium, FontStyle.Normal),
    font("Josefin Sans Medium Italix", "josefin_sans_medium_italic", FontWeight.Medium, FontStyle.Italic),
    font("Josefin Sans Regular", "josefin_sans_regular", FontWeight.Normal, FontStyle.Normal),
    font("Josefin Sans Semi Bold", "josefin_sans_semi_bold", FontWeight.SemiBold, FontStyle.Normal),
    font("Josefin Sans Semi Bold Italic", "josefin_sans_semi_bold_italic", FontWeight.SemiBold, FontStyle.Italic),
    font("Josefin Sans Thin", "josefin_sans_thin", FontWeight.Thin, FontStyle.Normal),
    font("Josefin Sans Thin Italic", "josefin_sans_thin_italic", FontWeight.Thin, FontStyle.Italic),
)

@Composable
fun getKaamelottFont() = FontFamily(
    font("Kaamelott", "font", FontWeight.Normal, FontStyle.Normal)
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun getTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = getJosefin()),
    displayMedium = baseline.displayMedium.copy(fontFamily = getJosefin()),
    displaySmall = baseline.displaySmall.copy(fontFamily = getJosefin()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = getJosefin()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = getJosefin()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = getJosefin()),
    titleLarge = baseline.titleLarge.copy(fontFamily = getJosefin()),
    titleMedium = baseline.titleMedium.copy(fontFamily = getJosefin()),
    titleSmall = baseline.titleSmall.copy(fontFamily = getJosefin()),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = getJosefin()),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = getJosefin()),
    bodySmall = baseline.bodySmall.copy(fontFamily = getJosefin()),
    labelLarge = baseline.labelLarge.copy(fontFamily = getJosefin()),
    labelMedium = baseline.labelMedium.copy(fontFamily = getJosefin()),
    labelSmall = baseline.labelSmall.copy(fontFamily = getJosefin()),
)

