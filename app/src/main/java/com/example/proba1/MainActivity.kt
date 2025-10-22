package com.example.proba1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proba1.model.Suggestion
import com.example.proba1.model.SuggestionsRepository
import com.example.proba1.ui.theme.Proba1Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Proba1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestro nuevo composable que gestiona el estado de la lista
                    WellnessScreenWithInfiniteScroll()
                }
            }
        }
    }
}

// PASO 1: Creamos un "ViewModel" simulado para gestionar la lógica de carga.
// En una app real, esto estaría en una clase ViewModel de Android.
@Composable
fun rememberWellnessViewModel(): WellnessViewModel {
    return remember { WellnessViewModel() }
}

class WellnessViewModel {
    // Usamos un MutableState para que Compose sepa cuándo la lista cambia.
    private val _suggestions = mutableStateListOf<Suggestion>().apply {
        // Carga inicial de datos
        addAll(SuggestionsRepository.suggestions)
    }
    val suggestions: List<Suggestion> = _suggestions

    // Estado para saber si estamos cargando más datos.
    var isLoading by mutableStateOf(false)
        private set

    // Función que simula la carga de más datos (por ejemplo, de una API)
    suspend fun loadMoreSuggestions() {
        if (isLoading) return // Si ya estamos cargando, no hacemos nada

        isLoading = true
        delay(2000) // Simulamos una espera de red de 2 segundos
        // Añadimos los mismos datos otra vez para simular una nueva "página"
        _suggestions.addAll(SuggestionsRepository.suggestions)
        isLoading = false
    }
}

// PASO 2: El composable principal que une la UI y la lógica
@Composable
fun WellnessScreenWithInfiniteScroll(
    viewModel: WellnessViewModel = rememberWellnessViewModel()
) {
    val suggestions = viewModel.suggestions
    val listState = rememberLazyListState() // Estado para controlar el scroll de la LazyColumn

    LazyColumn(
        state = listState, // Vinculamos el estado a nuestra LazyColumn
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(suggestions) { suggestion ->
            SuggestionItem(suggestion = suggestion)
        }

        // Mostramos un indicador de carga al final de la lista si isLoading es true
        item {
            if (viewModel.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // PASO 3: El "efecto" que observa el scroll y carga más datos.
    // Esta es la clave del scroll infinito.
    LaunchedEffect(listState) {
        // derivedStateOf se usa para ser más eficiente. Solo se recalcula si una de
        // las propiedades que lee (como layoutInfo) cambia.
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount

                // Si el último ítem visible es uno de los 5 últimos de la lista y no estamos
                // ya cargando, entonces pedimos más datos.
                if (lastVisibleItemIndex >= totalItems - 5 && !viewModel.isLoading) {
                    viewModel.loadMoreSuggestions()
                }
            }
    }
}


// --- Tus otros composables no necesitan cambios ---

@Composable
fun SuggestionItem(suggestion: Suggestion, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = suggestion.titleRes),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = suggestion.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = suggestion.descriptionRes),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// Renombramos el Preview para que coincida con el nuevo composable principal
@Preview(showBackground = true)
@Composable
fun WellnessScreenPreview() {
    Proba1Theme {
        WellnessScreenWithInfiniteScroll()
    }
}
