package com.primera.evaluacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import com.primera.evaluacion.ui.theme.MyApplicationTheme

data class ProductResponse(
    val id: String,
    val name: String,
    val data: ProductData?
)

data class ProductData(
    val color: String?,
    val capacity: String?,
    val price: Double?,
    @SerializedName("capacity GB")
    val capacityGB: Int?,
    val generation: String?,
    val year: Int?,
    @SerializedName("CPU model")
    val cpuModel: String?,
    @SerializedName("Hard disk size")
    val hardDiskSize: String?,
    @SerializedName("Strap Colour")
    val strapColour: String?,
    @SerializedName("Case Size")
    val caseSize: String?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("Screen size")
    val screenSize: Double?,
    @SerializedName("Capacity")
    val capacityAlt: String?,
    @SerializedName("Generation")
    val generationAlt: String?,
    @SerializedName("Price")
    val priceAlt: String?,
    @SerializedName("Color")
    val colorAlt: String?
)

data class LoginState(
    val usuario: String = "",
    val password: String = "",
    val mensaje: String = "",
    val loginExito: Boolean = false
)

data class ProductListState(
    val productos: List<ProductResponse> = emptyList(),
    val cargando: Boolean = false,
    val error: String = ""
)

data class ProductDetailState(
    val producto: ProductResponse? = null,
    val cargando: Boolean = false,
    val error: String = ""
)

object ProductRetrofitClient {

    private const val BASE_URL = "https://api.restful-api.dev/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ProductApiService = retrofit.create(ProductApiService::class.java)
}

interface ProductApiService {
    @GET("objects")
    suspend fun getProducts(): Response<List<ProductResponse>>

    @GET("objects/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ProductResponse>
}

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onUsuarioChange(nuevoUsuario: String) {
        _state.value = _state.value.copy(usuario = nuevoUsuario)
    }

    fun onPasswordChange(nuevoPassword: String) {
        _state.value = _state.value.copy(password = nuevoPassword)
    }

    fun onLoginClic() {
        val usuario = _state.value.usuario
        val password = _state.value.password

        if (usuario == "admin" && password == "admin") {
            _state.value = _state.value.copy(
                loginExito = true,
                mensaje = "Bienvenido"
            )
        } else {
            _state.value = _state.value.copy(
                loginExito = false,
                mensaje = "Credenciales incorrectas"
            )
        }
    }
}

class ProductListViewModel : ViewModel() {

    private val apiService = ProductRetrofitClient.apiService

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state

    fun cargarProductos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = "")
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        productos = response.body()!!,
                        cargando = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        cargando = false,
                        error = "Error al cargar productos"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    cargando = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
}

class ProductDetailViewModel : ViewModel() {

    private val apiService = ProductRetrofitClient.apiService

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state

    fun cargarProducto(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = "")
            try {
                val response = apiService.getProductById(id)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        producto = response.body()!!,
                        cargando = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        cargando = false,
                        error = "Error al cargar el producto"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    cargando = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by loginViewModel.state.collectAsState()

    LaunchedEffect(state.loginExito) {
        if (state.loginExito) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.usuario,
            onValueChange = { loginViewModel.onUsuarioChange(it) },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { loginViewModel.onLoginClic() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.mensaje,
            color = if (state.loginExito) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    productListViewModel: ProductListViewModel = viewModel(),
    onProductClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val state by productListViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        productListViewModel.cargarProductos()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "AD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@admin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = onLogoutClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Salir")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Catálogo de Productos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { productListViewModel.cargarProductos() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.productos) { producto ->
                        ProductCard(
                            producto = producto,
                            onClick = { onProductClick(producto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: ProductResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = producto.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            val color = producto.data?.color ?: producto.data?.colorAlt ?: ""
            if (color.isNotEmpty()) {
                Text(
                    text = "Color: $color",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val precio = producto.data?.price ?: producto.data?.priceAlt?.toDoubleOrNull()
            if (precio != null) {
                Text(
                    text = "Precio: $${"%.2f".format(precio)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

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

object AppRoutes {
    const val LOGIN = "login"
    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"

    fun productDetailRoute(productId: String): String {
        return "product_detail/$productId"
    }
}

@Composable
fun AppNavigator(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        modifier = modifier
    ) {
        composable(AppRoutes.LOGIN) {
            val viewModel: LoginViewModel = viewModel()
            LoginScreen(
                loginViewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(AppRoutes.PRODUCT_LIST) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.PRODUCT_LIST) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate(AppRoutes.productDetailRoute(productId))
                },
                onLogoutClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.PRODUCT_DETAIL) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigator(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
