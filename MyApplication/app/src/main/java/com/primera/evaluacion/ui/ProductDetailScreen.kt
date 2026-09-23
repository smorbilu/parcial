package com.primera.evaluacion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.primera.evaluacion.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    modifier: Modifier = Modifier,
    productDetailViewModel: ProductDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by productDetailViewModel.state.collectAsState()

    LaunchedEffect(productId) {
        productDetailViewModel.cargarProducto(productId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedButton(onClick = onBackClick) {
            Text("← Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            state.producto != null -> {
                val producto = state.producto!!

                Text(
                    text = producto.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailInfoRow(label = "ID", value = producto.id)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(label = "Nombre", value = producto.name)

                        if (producto.data != null) {
                            val data = producto.data

                            val color = data.color ?: data.colorAlt ?: data.strapColour
                            if (color != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Color", value = color)
                            }

                            val capacidad = data.capacity ?: data.capacityAlt
                                ?: data.capacityGB?.let { "$it GB" }
                            if (capacidad != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Capacidad", value = capacidad)
                            }

                            val precio = data.price ?: data.priceAlt?.toDoubleOrNull()
                            if (precio != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(
                                    label = "Precio",
                                    value = "$${"%.2f".format(precio)}"
                                )
                            }

                            val generacion = data.generation ?: data.generationAlt
                            if (generacion != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Generación", value = generacion)
                            }

                            if (data.year != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Año", value = data.year.toString())
                            }

                            if (data.cpuModel != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "CPU", value = data.cpuModel)
                            }

                            if (data.hardDiskSize != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Disco Duro", value = data.hardDiskSize)
                            }

                            if (data.caseSize != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Tamaño", value = data.caseSize)
                            }

                            if (data.screenSize != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(
                                    label = "Pantalla",
                                    value = "${data.screenSize} pulgadas"
                                )
                            }

                            if (data.description != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailInfoRow(label = "Descripción", value = data.description)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
