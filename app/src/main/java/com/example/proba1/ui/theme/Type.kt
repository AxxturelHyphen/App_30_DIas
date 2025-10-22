package com.example.proba1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Usaremos la tipografía por defecto de Material Design.
// No se necesita ninguna fuente externa.
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Fuente por defecto del sistema
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /*
    Puedes añadir más estilos si los necesitas,
    pero siempre usando las fuentes por defecto para evitar errores.
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    */
)
