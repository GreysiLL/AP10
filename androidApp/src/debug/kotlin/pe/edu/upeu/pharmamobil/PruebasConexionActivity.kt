package pe.edu.upeu.pharmamobil
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import pe.edu.upeu.pharmamobil.theme.PharmaMobilTheme
import pe.edu.upeu.pharmamobil.presentation.conexion.PruebasConexionScreen

class PruebasConexionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PharmaMobilTheme { PruebasConexionScreen() } }
    }
}
