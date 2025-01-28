package com.rotibook

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rotibook.ui.theme.RotiTrackerTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = RotiTrackerRepository(database.clientDao(), database.rotiPurchaseDao())

        setContent {
            RotiTrackerTheme {
                SetSystemBarsColor()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RotiTrackerApp(application, repository)
                }
            }
        }
    }
}

@Composable
fun SetSystemBarsColor() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5
    val statusBarColor = MaterialTheme.colorScheme.primaryContainer
    val navigationBarColor = MaterialTheme.colorScheme.background

    systemUiController.setStatusBarColor(
        color = statusBarColor,
        darkIcons = useDarkIcons
    )
    systemUiController.setNavigationBarColor(
        color = navigationBarColor,
        darkIcons = useDarkIcons
    )
}

@Composable
fun RotiTrackerApp(application: android.app.Application, repository: RotiTrackerRepository) {
    val navController = rememberNavController()
    var currentLanguage by remember { mutableStateOf(Translations.Language.ENGLISH) }

    NavHost(navController, startDestination = "clientList") {
        composable("clientList") {
            val viewModel: ClientListViewModel = viewModel(
                factory = ClientListViewModel.Factory(repository)
            )
            ClientListScreen(
                viewModel = viewModel,
                onClientClick = { client ->
                    navController.navigate("clientDetail/${client.id}")
                },
                onDataExportImportClick = {
                    navController.navigate("dataExportImport")
                }
            )
        }
        composable(
            "clientDetail/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: return@composable
            val viewModel: ClientDetailViewModel = viewModel(
                factory = ClientDetailViewModel.Factory(repository, clientId)
            )
            ClientDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onToggleLanguage = {
                    currentLanguage = if (currentLanguage == Translations.Language.ENGLISH) {
                        Translations.Language.URDU
                    } else {
                        Translations.Language.ENGLISH
                    }
                }
            )
        }
        composable("dataExportImport") {
            val viewModel: DataExportImportViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return DataExportImportViewModel(application, repository) as T
                    }
                }
            )
            DataExportImportScreen(
                viewModel = viewModel,
                currentLanguage = currentLanguage,
                onNavigateBack = { navController.popBackStack() },
                onToggleLanguage = {
                    currentLanguage = if (currentLanguage == Translations.Language.ENGLISH) {
                        Translations.Language.URDU
                    } else {
                        Translations.Language.ENGLISH
                    }
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    viewModel: ClientDetailViewModel,
    onBackClick: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val client by viewModel.client.collectAsState()
    val purchases by viewModel.purchases.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showCalculator by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Translations.get("client_details", currentLanguage)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Translations.get("back", currentLanguage))
                    }
                },
                actions = {
                    IconButton(onClick = { showCalculator = true }) {
                        Image(painter = painterResource(id = R.drawable.calculator), contentDescription = Translations.get("calculator", currentLanguage))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = Translations.get("add_purchase", currentLanguage))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(purchases) { purchase ->
                    PurchaseItem(
                        purchase = purchase,
                        onEdit = { viewModel.updatePurchase(it) },
                        onDelete = { viewModel.deletePurchase(it) },
                        currentLanguage = currentLanguage
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPurchaseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { quantity ->
                viewModel.addPurchase(quantity)
                showAddDialog = false
            },
            currentLanguage = currentLanguage
        )
    }

    if (showCalculator) {
        Calculator(
            initialRotiCount = purchases.sumOf { it.quantity },
            currentLanguage = currentLanguage,
            onDismiss = { showCalculator = false }
        )
    }
}

@Composable
fun PurchaseItem(
    purchase: RotiPurchase,
    onEdit: (RotiPurchase) -> Unit,
    onDelete: (RotiPurchase) -> Unit,
    currentLanguage: Translations.Language
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${Translations.get("quantity", currentLanguage)}: ${purchase.quantity}")
                Text(text = "${Translations.get("date", currentLanguage)}: ${dateFormat.format(purchase.date)}")
            }
            IconButton(onClick = { onEdit(purchase) }) {
                Icon(Icons.Default.Edit, contentDescription = Translations.get("edit", currentLanguage))
            }
            IconButton(onClick = { onDelete(purchase) }) {
                Icon(Icons.Default.Delete, contentDescription = Translations.get("delete", currentLanguage))
            }
        }
    }
}

@Composable
fun AddPurchaseDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit, currentLanguage: Translations.Language) {
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Translations.get("add_purchase", currentLanguage)) },
        text = {
            TextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(Translations.get("quantity", currentLanguage)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    quantity.toIntOrNull()?.let { onConfirm(it) }
                },
                enabled = quantity.toIntOrNull() != null
            ) {
                Text(Translations.get("add", currentLanguage))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(Translations.get("cancel", currentLanguage))
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
    viewModel: ClientListViewModel,
    onClientClick: (Client) -> Unit,
    onDataExportImportClick: () -> Unit
) {
    val clients by viewModel.clients.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var clientToEdit by remember { mutableStateOf<Client?>(null) }
    var clientToDelete by remember { mutableStateOf<Client?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Translations.get("app_name", currentLanguage)) },
                actions = {
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Image(painter = painterResource(id = R.drawable.langauges), contentDescription = Translations.get("language", currentLanguage),
                            modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = onDataExportImportClick) {
                        Icon(Icons.Default.Settings, contentDescription = Translations.get("data_export_import", currentLanguage))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = Translations.get("add_client", currentLanguage))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp),
                    placeholder = { Text(Translations.get("search_clients", currentLanguage)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = Translations.get("clear_search", currentLanguage),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(clients) { client ->
                    ClientItem(
                        client = client,
                        onClick = onClientClick,
                        onEdit = { clientToEdit = it },
                        onDelete = { clientToDelete = it },
                        currentLanguage = currentLanguage
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditClientDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phoneNumber ->
                viewModel.addClient(name, phoneNumber)
                showAddDialog = false
            },
            currentLanguage = currentLanguage
        )
    }

    clientToEdit?.let { client ->
        AddEditClientDialog(
            onDismiss = { clientToEdit = null },
            onConfirm = { name, phoneNumber ->
                viewModel.updateClient(client.copy(name = name, phoneNumber = phoneNumber))
                clientToEdit = null
            },
            currentLanguage = currentLanguage,
            initialName = client.name,
            initialPhoneNumber = client.phoneNumber
        )
    }

    clientToDelete?.let { client ->
        DeleteClientDialog(
            client = client,
            onDismiss = { clientToDelete = null },
            onConfirm = {
                viewModel.deleteClient(client)
                clientToDelete = null
            },
            currentLanguage = currentLanguage
        )
    }
}

@Composable
fun ClientItem(
    client: Client,
    onClick: (Client) -> Unit,
    onEdit: (Client) -> Unit,
    onDelete: (Client) -> Unit,
    currentLanguage: Translations.Language
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(client) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = client.name, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${Translations.get("phone", currentLanguage)}: ${client.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onEdit(client) }) {
                Icon(Icons.Default.Edit, contentDescription = Translations.get("edit", currentLanguage))
            }
            IconButton(onClick = { onDelete(client) }) {
                Icon(Icons.Default.Delete, contentDescription = Translations.get("delete", currentLanguage))
            }
        }
    }
}

@Composable
fun AddEditClientDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    currentLanguage: Translations.Language,
    initialName: String = "",
    initialPhoneNumber: String = ""
) {
    var name by remember { mutableStateOf(initialName) }
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Translations.get(if (initialName.isEmpty()) "add_client" else "edit_client", currentLanguage)) },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Translations.get("client_name", currentLanguage)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(Translations.get("phone", currentLanguage)) }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, phoneNumber) }) {
                Text(Translations.get(if (initialName.isEmpty()) "add" else "update", currentLanguage))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(Translations.get("cancel", currentLanguage))
            }
        }
    )
}

@Composable
fun DeleteClientDialog(
    client: Client,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    currentLanguage: Translations.Language
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Translations.get("delete_client", currentLanguage)) },
        text = { Text(Translations.get("delete_client_confirmation", currentLanguage).format(client.name)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(Translations.get("delete", currentLanguage))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(Translations.get("cancel", currentLanguage))
            }
        }
    )
}



@Composable
fun Calculator(
    initialRotiCount: Int,
    currentLanguage: Translations.Language,
    onDismiss: () -> Unit
) {
    var rotiCount by remember { mutableStateOf(initialRotiCount.toString()) }
    var pricePerRoti by remember { mutableStateOf("") }
    var totalPrice by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Translations.get("calculator", currentLanguage)) },
        text = {
            Column {
                OutlinedTextField(
                    value = rotiCount,
                    onValueChange = {
                        rotiCount = it
                        calculateTotal(rotiCount, pricePerRoti) { totalPrice = it }
                    },
                    label = { Text(Translations.get("roti_count", currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pricePerRoti,
                    onValueChange = {
                        pricePerRoti = it
                        calculateTotal(rotiCount, pricePerRoti) { totalPrice = it }
                    },
                    label = { Text(Translations.get("price_per_roti", currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${Translations.get("total_price", currentLanguage)}: $totalPrice",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(Translations.get("close", currentLanguage))
            }
        }
    )
}

private fun calculateTotal(rotiCount: String, pricePerRoti: String, onResult: (String) -> Unit) {
    val count = rotiCount.toIntOrNull() ?: 0
    val price = pricePerRoti.toFloatOrNull() ?: 0f
    val total = count * price
    onResult(String.format("%.2f", total))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataExportImportScreen(
    viewModel: DataExportImportViewModel,
    currentLanguage: Translations.Language,
    onNavigateBack: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val exportStatus by viewModel.exportStatus.collectAsState()
    val importStatus by viewModel.importStatus.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportData(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importData(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Translations.get("data_export_import", currentLanguage)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Translations.get("back", currentLanguage))
                    }
                },
                actions = {
                    IconButton(onClick = onToggleLanguage) {
                        Image(painter = painterResource(id = R.drawable.langauges), contentDescription = Translations.get("language", currentLanguage),
                            modifier = Modifier.size(32.dp))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = Translations.get("data_management_description", currentLanguage),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            DataManagementCard(
                title = Translations.get("export_data", currentLanguage),
                description = Translations.get("export_data_description", currentLanguage),
                icon = R.drawable.export,
                buttonText = Translations.get("export", currentLanguage),
                onClick = { exportLauncher.launch("rotitracker_data.json") },
                status = exportStatus,
                currentLanguage = currentLanguage
            )

            DataManagementCard(
                title = Translations.get("import_data", currentLanguage),
                description = Translations.get("import_data_description", currentLanguage),
                icon = R.drawable.resource_import,
                buttonText = Translations.get("import", currentLanguage),
                onClick = { importLauncher.launch(arrayOf("application/json")) },
                status = importStatus,
                currentLanguage = currentLanguage
            )
        }
    }
}

@Composable
fun DataManagementCard(
    title: String,
    description: String,
    icon: Int,
    buttonText: String,
    onClick: () -> Unit,
    status: DataExportImportViewModel.ExportImportStatus,
    currentLanguage: Translations.Language
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
            AnimatedVisibility(
                visible = status !is DataExportImportViewModel.ExportImportStatus.Idle,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                when (status) {
                    is DataExportImportViewModel.ExportImportStatus.InProgress -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    is DataExportImportViewModel.ExportImportStatus.Success -> {
                        Text(
                            text = Translations.get("operation_success", currentLanguage),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    is DataExportImportViewModel.ExportImportStatus.Error -> {
                        Text(
                            text = Translations.get("operation_error", currentLanguage) + ": ${status.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

