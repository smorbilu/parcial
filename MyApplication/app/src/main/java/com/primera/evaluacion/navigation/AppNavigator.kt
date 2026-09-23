package com.primera.evaluacion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.primera.evaluacion.ui.LoginScreen
import com.primera.evaluacion.ui.ProductDetailScreen
import com.primera.evaluacion.ui.ProductListScreen
import com.primera.evaluacion.viewmodel.LoginViewModel

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
