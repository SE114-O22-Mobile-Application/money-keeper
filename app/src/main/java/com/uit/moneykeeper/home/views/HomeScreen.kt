package com.uit.moneykeeper.home.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uit.moneykeeper.R
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uit.moneykeeper.home.viewmodel.ListWalletViewModel
import com.uit.moneykeeper.home.viewmodel.SelectedWalletViewModel
import com.uit.moneykeeper.models.ViModel
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.uit.moneykeeper.budget.viewmodel.getListNganSachByMonthYear
import com.uit.moneykeeper.budget.views.BudgetDetail
import com.uit.moneykeeper.home.viewmodel.HomeScreenViewModel
import com.uit.moneykeeper.models.NganSachModel
import java.time.LocalDate


data class Wallet(val name: String, val amount: Int)
object SelectWallet {
    var Wallet: Wallet = Wallet("a",1)
}
@Composable
fun HomeScreen(navController: NavController,viewModel: HomeScreenViewModel , selectedWalletViewModel: SelectedWalletViewModel) {
//    Text(text = "This is Home Screen 3")
    println("is call home")
    MainContent(navController = navController,viewModel ,selectedWalletViewModel)
}

@Composable
fun MainContent(navController: NavController, viewModel: HomeScreenViewModel, selectedWalletViewModel: SelectedWalletViewModel) {
    val walletList = ListWalletViewModel().walletList
    val wallets = remember { mutableStateListOf<ViModel>() }
    var total = 0.0
    var showDialog by remember { mutableStateOf(false) }
    var textInput_ten by remember { mutableStateOf("") }
    var textInput_soDu by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val listNS: List<NganSachModel> = getListNganSachByMonthYear(LocalDate.now());

    LaunchedEffect(key1 = walletList ) {
        print("Launch")
        wallets.clear();
        for(wallet in walletList) {
            wallets.add(wallet);
        }
    }
    for(wallet in wallets) {
        total +=  wallet.soDu
    }
//    selectedWalletViewModel.setViModel(ViModel(0, "Tất cả", total))
    Box(modifier = Modifier.fillMaxSize()) {

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd) // Đặt FAB ở góc dưới cùng bên phải
                .padding(16.dp)
                .zIndex(1f),
            onClick = {navController.navigate("NewTransactionScreen"){
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }},
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add transaction",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
//                .height(80.dp)
                        .height(IntrinsicSize.Min)
                        .background(Color(0xFFFFFFF))
                )
                {
                    Column {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedWalletViewModel.setViModel(ViModel(0, "Tất cả", total))
                                navController.navigate("account") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Số dư",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = formatNumberWithCommas(total),
                                    fontSize = 20.sp,
                                    color = Color.Blue
                                )
                            }
                        }
                        for (wallet in wallets.take(4)) {
                            WalletCardItem(navController, wallet, selectedWalletViewModel)
                        }

                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            exit = shrinkVertically(
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            Column {
                                for (wallet in wallets.drop(4)) {
                                    WalletCardItem(navController, wallet, selectedWalletViewModel)
                                }
                            }
                        }
//                    Box(modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        ) {
//                        LazyColumn(modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn( max = 200.dp)) {
//                            items(wallets.size) {index ->
//                                WalletCardItem(navController,wallets[index],selectedWalletViewModel)
//                            }
//                        }
//                    }

                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(8.dp),

                            colors = ButtonDefaults.buttonColors(Color(0xFF00c190)),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add, // Replace with your desired icon
                                    contentDescription = "Home icon"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Thêm ví",
                                    fontSize = 20.sp,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = { expanded = !expanded },
                                modifier = Modifier
                                    .height(50.dp)
                                    .padding(8.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF009adb)),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (expanded) "Thu gọn" else "Xem thêm"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (expanded) "Thu gọn" else "Xem thêm",
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                        androidx.compose.material.Divider(
                            color = Color.Black,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(top = 30.dp)
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutSlowInEasing
                            )
                        )
//                .height(IntrinsicSize.Min)
                )
                {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Ngân sách tháng ${LocalDate.now().monthValue}/${LocalDate.now().year}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        if (listNS.isNotEmpty())
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                BudgetDetail(ngansach = listNS[0])
                            }
                        else {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 100.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = "Chưa có ngân sách",
                                        style = TextStyle(fontSize = 18.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("budget") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp)
                                        .padding(8.dp),

                                    colors = ButtonDefaults.buttonColors(Color(0xFF00c190)),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            "Thêm ngân sách tháng này",
                                            fontSize = 20.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        text = {
                            Column(
                            ) {
                                Text(
                                    "Thêm ví mới",
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 20.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                // Ô nhập văn bản
                                androidx.compose.material3.OutlinedTextField(
                                    value = textInput_ten,
                                    onValueChange = { textInput_ten = it },
                                    label = { Text("Tên ví") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color.LightGray,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                androidx.compose.material3.OutlinedTextField(
                                    value = textInput_soDu,
                                    onValueChange = { newValue ->
                                        if (newValue.all { it.isDigit() })
                                            textInput_soDu = newValue
                                    },
                                    label = { Text("Số dư") },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color.LightGray,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent
                                    )
                                )
                            }
                        },
                        buttons = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp) // Padding ngang cho hàng chứa các nút
                            ) {
                                Button(
                                    onClick = {
                                        showDialog = false
                                        textInput_ten = ""
                                        textInput_soDu = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFFf25207)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 4.dp) // Khoảng cách giữa hai nút
                                ) {
                                    Text("Huỷ")
                                }
                                Button(
                                    onClick = {
                                        viewModel.AddNewWallet(
                                            "Add",
                                            walletList = wallets,
                                            textInput_ten,
                                            textInput_soDu.toDouble()
                                        )
                                        wallets.add(
                                            ViModel(
                                                wallets.size + 1,
                                                textInput_ten,
                                                textInput_soDu.toDouble()
                                            )
                                        )
                                        showDialog = false
                                        textInput_ten = ""
                                        textInput_soDu = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF1cba46)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 4.dp) // Khoảng cách giữa hai nút
                                ) {
                                    Text("Thêm")
                                }
                            }
                        }

                    )
                }
            }
        }
    }
}

@Composable
fun WalletCardItem(navController: NavController,wallet: ViModel, selectedWalletViewModel: SelectedWalletViewModel) {
    val walletIcon = R.drawable.baseline_account_balance_wallet_24
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            selectedWalletViewModel.setViModel(wallet)
            navController.navigate("account") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier
                .padding(5.dp)
                ) {
                Icon(
                    ImageVector.vectorResource(
                        walletIcon
                    ),
                    contentDescription = "Ví 1"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = wallet.ten)

            }
            Row(modifier = Modifier
                .padding(5.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // Your row content here
                Text(formatNumberWithCommas(wallet.soDu)) // Example content
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .border(
                border = BorderStroke(
                    color = Color.Black,
                    width = 1.dp
                )
            )) {

        }
    }
}

@Composable
fun ButtonAddWallet() {

    Button(onClick = {  },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(8.dp),

        colors = ButtonDefaults.buttonColors(Color(0xFF00c190)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add, // Replace with your desired icon
                contentDescription = "Home icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thêm ví",
                fontSize = 20.sp,)
        }
    }
}
fun formatNumberWithCommas(doubleValue: Double): String {
    val formattedValue = String.format("%.2f", doubleValue) // Format với hai số sau dấu phẩy
    val parts = formattedValue.split('.') // Tách phần nguyên và phần thập phân

    var integerPart = parts[0] // Phần nguyên
    var decimalPart = parts.getOrElse(1) { "" } // Phần thập phân, mặc định là chuỗi trống nếu không có phần thập phân

    // Đổi mỗi 3 chữ số của phần nguyên thành một dấu chấm
    val integerLength = integerPart.length
    var index = 0
    while (integerLength - index > 3) {
        integerPart = integerPart.substring(0, integerLength - 3 - index) + "." + integerPart.substring(integerLength - 3 - index)
        index += 3
    }

    // Xóa các số 0 không cần thiết sau dấu phẩy nếu có
    while (decimalPart.isNotEmpty() && decimalPart.last() == '0') {
        decimalPart = decimalPart.dropLast(1)
    }

    // Nếu phần thập phân không còn chữ số nào thì không cần hiển thị dấu phẩy
    if (decimalPart.isEmpty()) {
        return integerPart
    }

    return "$integerPart,$decimalPart"
}
