package com.uit.moneykeeper

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.uit.moneykeeper.budget.viewmodel.NewBudgetViewModel
import com.uit.moneykeeper.budget.viewmodel.getListNganSachByMonthYear
import com.uit.moneykeeper.budget.views.BudgetScreen
import com.uit.moneykeeper.budget.views.NewBudget
import com.uit.moneykeeper.home.viewmodel.DetailWalletViewModel
import com.uit.moneykeeper.home.viewmodel.HomeScreenViewModel
import com.uit.moneykeeper.home.viewmodel.SelectedWalletViewModel
import com.uit.moneykeeper.home.views.HomeScreen
import com.uit.moneykeeper.home.views.WalletDetail
import com.uit.moneykeeper.transaction.viewmodel.EditTransactionViewModel
import com.uit.moneykeeper.transaction.viewmodel.NewTransactionViewModel
import com.uit.moneykeeper.transaction.viewmodel.TransactionDetailViewModel
import com.uit.moneykeeper.transaction.viewmodel.TransactionViewModel
import com.uit.moneykeeper.transaction.views.EditTransactionScreen
import com.uit.moneykeeper.transaction.views.NewTransactionScreen
import com.uit.moneykeeper.transaction.views.TransactionDetailScreen
import com.uit.moneykeeper.transaction.views.TransactionScreen
import java.time.LocalDate

@Composable
fun MKNavHost(
    navController: NavHostController,
    selectedWalletViewModel: SelectedWalletViewModel,
    newbudgetViewModel: NewBudgetViewModel,
    modifier: Modifier = Modifier,
    showNavigationBar: MutableState<Boolean>
) {
    var previousBackStackEntry by remember { mutableStateOf<NavBackStackEntry?>(null) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentBackStackEntry) {
        if (previousBackStackEntry?.destination?.route != "home" && (currentBackStackEntry?.destination?.route == "WalletDetail" )) {
            navController.popBackStack();
        }

        else if (currentBackStackEntry != null && currentBackStackEntry != previousBackStackEntry) {
            // Update the previous back stack entry
            previousBackStackEntry = currentBackStackEntry
        }
    }
    val listNS = getListNganSachByMonthYear(LocalDate.now())
    println("List NS: $listNS")
    NavHost(
        navController = navController,
        startDestination = if(listNS.isEmpty()) "home" else "home",
        modifier = modifier
    ) {
        composable("home") {
            showNavigationBar.value = true
            HomeScreen(navController, viewModel = HomeScreenViewModel(), selectedWalletViewModel)
        }
        composable("WalletDetail") {
            showNavigationBar.value = true
            WalletDetail(navController = navController, viewModel = DetailWalletViewModel(), viModel2 = selectedWalletViewModel.getViModel())
        }
        composable("NewTransactionScreen") {
            showNavigationBar.value = false
            NewTransactionScreen(navController, viewModel = NewTransactionViewModel())
        }
        composable("EditTransactionScreen/{id}") { backStackEntry ->
            showNavigationBar.value = false
            val id = backStackEntry.arguments?.getString("id")?.toInt()
            if (id != null) {
                EditTransactionScreen(navController, viewModel = EditTransactionViewModel(id))
            } else {
                Text(text = "Error: id is null")
            }
        }
        composable("TransactionDetailScreen/{id}") { backStackEntry ->
            showNavigationBar.value = false
            val id = backStackEntry.arguments?.getString("id")?.toInt()
            if (id != null) {
                TransactionDetailScreen(navController, viewModel = TransactionDetailViewModel(id))
            } else {
                Text(text = "Error: id is null")
            }
        }

        composable("transaction") {

            showNavigationBar.value = true
            TransactionScreen(navController, viewModel = TransactionViewModel())
        }

//        composable("transaction") { backStackEntry ->
//            showNavigationBar.value = true
//            val month = backStackEntry.arguments?.getString("month")?.toIntOrNull()
//            val year = backStackEntry.arguments?.getString("year")?.toIntOrNull()
//            TransactionScreen(navController, month, year, viewModel = TransactionViewModel())
//        }

        composable("budget") {
            showNavigationBar.value = true
            BudgetScreen(navController = navController, newbudgetViewModel)
        }

        composable("newbudget") {
            showNavigationBar.value = false
//            Text(text = "Account")
            NewBudget(navController = navController, thoiGian = newbudgetViewModel.getTime())
        }

        composable("account") {
            showNavigationBar.value = true
//            Text(text = "Account")
            WalletDetail(navController = navController, viewModel = DetailWalletViewModel(), viModel2 = selectedWalletViewModel.getViModel())
//            NewBudget(navController = navController, thoiGian = newbudgetViewModel.getTime())
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        println("Route to: $route")
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
