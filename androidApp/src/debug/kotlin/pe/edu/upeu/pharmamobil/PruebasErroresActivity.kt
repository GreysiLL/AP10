package pe.edu.upeu.pharmamobil
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import pe.edu.upeu.pharmamobil.presentation.conexion.PruebasErroresScreen
import pe.edu.upeu.pharmamobil.theme.PharmaMobilTheme
class PruebasErroresActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState);setContent { PharmaMobilTheme { PruebasErroresScreen() } } }
}
