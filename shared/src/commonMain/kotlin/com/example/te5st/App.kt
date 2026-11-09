package com.example.te5st

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import te5st.shared.generated.resources.Res
import te5st.shared.generated.resources.logo2024_compatto

val FijlkamBlue = Color(0xFF003366)
val FijlkamDarkBlue = Color(0xFF001F3F)
val FijlkamNavy = Color(0xFF002B66)
val FijlkamDarkNavy = Color(0xFF001A40)
val FijlkamBlueButton = Color(0xFF003380)
val FijlkamGreen = Color(0xFF009944)
val FijlkamGold = Color(0xFFFFC107)
val FijlkamBg = Color(0xFFF5F7FA)
val FijlkamRed = Color(0xFFCE2B37)

val ItalianRegions = listOf(
    "Abruzzo", "Basilicata", "Calabria", "Campania", "Emilia-Romagna",
    "Friuli Venezia Giulia", "Lazio", "Liguria", "Lombardia", "Marche",
    "Molise", "Piemonte", "Puglia", "Sardegna", "Sicilia",
    "Toscana", "Trentino-Alto Adige", "Umbria", "Valle d'Aosta", "Veneto"
)

val FijlkamRoles = listOf("Regionale", "Nazionale 1", "Nazionale 2", "Nazionale 3")
val FijlkamSports = listOf("Karate", "Judo", "Lotta")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportDropdown(
    selectedSport: String,
    onSportSelected: (String) -> Unit,
    label: String = "Disciplina",
    sportsList: List<sport> = emptyList()
) {
    var expanded by remember { mutableStateOf(false) }
    val displaySports = if (sportsList.isNotEmpty()) {
        sportsList.mapNotNull { it.sport_name }
    } else {
        FijlkamSports
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedSport,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            displaySports.forEach { sportName ->
                DropdownMenuItem(
                    text = { Text(sportName) },
                    onClick = {
                        onSportSelected(sportName)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun isOfficialEligibleForCompetition(
    user: Udg,
    compScope: String?,
    compRegion: String?,
    compSport: String? = null
): Boolean {
    val userScope = user.scope?.trim()

    if (!compSport.isNullOrBlank()) {
        val matchesSportOrScope = userScope?.equals(compSport, ignoreCase = true) == true ||
                userScope?.contains(compSport, ignoreCase = true) == true
        if (!matchesSportOrScope && !userScope.isNullOrBlank()) {
            return false
        }
    }

    val isNationalUser = user.role?.contains("Nazionale", ignoreCase = true) == true ||
            userScope?.contains("Nazionale", ignoreCase = true) == true
    val isNationalComp = compScope?.contains("Nazionale", ignoreCase = true) == true ||
            compScope?.startsWith("N", ignoreCase = true) == true
    val isSameRegion = user.region?.trim()?.equals(compRegion?.trim(), ignoreCase = true) == true

    return if (isNationalComp) {
        // Gara Nazionale: AVVISARE UFFICIALI NAZIONALI
        isNationalUser
    } else {
        // Gara Regionale: AVVISARE UFFICIALI REGIONALI DELLA STESSA REGIONE O NAZIONALI
        isSameRegion || isNationalUser || compRegion.isNullOrBlank() || user.region.isNullOrBlank()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDropdown(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    label: String = "Regione"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRegion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ItalianRegions.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region) },
                    onClick = {
                        onRegionSelected(region)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    label: String = "Ruolo Ufficiale"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FijlkamRoles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label, fontSize = 11.sp) },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }, modifier = Modifier.size(24.dp)) {
                Text("📅", fontSize = 14.sp)
            }
        },
        modifier = modifier.clickable { showDialog = true },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateToMillis(if (selectedDate.contains("-")) selectedDate else "2025-06-01")
        )
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dateStr = formatMillisToDateString(millis)
                        onDateSelected(dateStr)
                    }
                    showDialog = false
                }) {
                    Text("Conferma", color = FijlkamNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Annulla", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = FijlkamNavy,
            primaryContainer = FijlkamDarkNavy,
            secondary = FijlkamRed,
            background = FijlkamBg
        )
    ) {
        val udgRepo = remember { UdgRepository() }
        val compRepo = remember { CompetitionRepository() }
        val scope = rememberCoroutineScope()

        var currentScreen by remember { mutableStateOf("login") }
        var loggedUser by remember { mutableStateOf<Udg?>(null) }
        var statusMessage by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        var users by remember { mutableStateOf<List<Udg>>(emptyList()) }
        var competitions by remember { mutableStateOf<List<Competition>>(emptyList()) }
        var availabilities by remember { mutableStateOf<List<Availability>>(emptyList()) }
        var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
        var sports by remember { mutableStateOf<List<sport>>(emptyList()) }

        fun loadAllData() {
            scope.launch {
                isLoading = true
                statusMessage = "Sincronizzazione dati..."
                users = udgRepo.fetchUsers()
                competitions = compRepo.fetchCompetitions()
                availabilities = compRepo.fetchAvailabilities()
                notifications = compRepo.fetchNotifications()
                sports = compRepo.fetchSports()
                isLoading = false
                statusMessage = ""
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                "login" -> LoginScreen(
                    users = users,
                    sports = sports,
                    onLoginSuccess = { user ->
                        loggedUser = user
                        if (user.isFirstLogin == true) {
                            currentScreen = "changePassword"
                        } else if (user.isAdmin == true) {
                            currentScreen = "adminDashboard"
                        } else {
                            currentScreen = "officialDashboard"
                        }
                    },
                    statusMessage = statusMessage,
                    isLoading = isLoading,
                    onReload = { loadAllData() }
                )
                "changePassword" -> ChangePasswordScreen(
                    user = loggedUser,
                    udgRepo = udgRepo,
                    onPasswordChanged = {
                        if (loggedUser?.isAdmin == true) {
                            currentScreen = "adminDashboard"
                        } else {
                            currentScreen = "officialDashboard"
                        }
                    }
                )
                "adminDashboard" -> AdminDashboard(
                    loggedUser = loggedUser!!,
                    users = users,
                    competitions = competitions,
                    availabilities = availabilities,
                    sports = sports,
                    udgRepo = udgRepo,
                    compRepo = compRepo,
                    onRefresh = { loadAllData() },
                    onLogout = { loggedUser = null; currentScreen = "login" }
                )
                "officialDashboard" -> OfficialDashboard(
                    loggedUser = loggedUser!!,
                    users = users,
                    competitions = competitions,
                    availabilities = availabilities,
                    notifications = notifications,
                    compRepo = compRepo,
                    onRefresh = { loadAllData() },
                    onLogout = { loggedUser = null; currentScreen = "login" }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    users: List<Udg>,
    sports: List<sport> = emptyList(),
    onLoginSuccess: (Udg) -> Unit,
    statusMessage: String,
    isLoading: Boolean,
    onReload: () -> Unit
) {
    var selectedSport by remember { mutableStateOf("Karate") }
    var selectedRegion by remember { mutableStateOf("Regione di Apparteneza") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    var regionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onReload()
    }

    val executeLogin = {
        errorMessage = ""
        val cleanUser = username.trim()
        val cleanPass = password.trim()
        val cleansport = selectedSport.trim()
        val cleanRegion = selectedRegion.trim()
        val match = users.find {
            it.username.trim().equals(cleanUser, ignoreCase = true) &&
            it.password.trim() == cleanPass &&
            (it.scope?.trim()?.equals(cleansport, ignoreCase = true) == true || cleansport.isEmpty()) &&
            it.region?.trim().equals(cleanRegion, ignoreCase = true)
        }

        if (match != null) {
            onLoginSuccess(match)
        } else {
            errorMessage = "Credenziali non valide o utente non trovato"
            if (users.isEmpty()) {
                onReload()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FijlkamBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar area
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
        )

        // Tricolore accent stripe
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF009246)))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFFFFFF)))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFCE2B37)))
        }

        // Top Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FijlkamNavy)
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.logo2024_compatto),
                            contentDescription = "Logo FIJLKAM",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "FEDERAZIONE ITALIANA JUDO LOTTA KARATE ARTI MARZIALI",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dynamic Badge for Sport Selection
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    color = Color(0xFF001529),
                    border = BorderStroke(1.dp, Color(0xFF004080))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(FijlkamRed, androidx.compose.foundation.shape.CircleShape)
                        )
                        Text(
                            text = "${selectedSport.uppercase()} FIJLKAM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Form Section below header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ACCESSO UFFICIALI DI GARA",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FijlkamNavy
            )

            Text(
                text = "Seleziona sport, regione e inserisci le credenziali",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // Sport Selection Dropdown
            SportDropdown(
                selectedSport = selectedSport,
                onSportSelected = { selectedSport = it },
                label = "Disciplina / Sport",
                sportsList = sports
            )

            // Region Picklist Field
            ExposedDropdownMenuBox(
                expanded = regionExpanded,
                onExpandedChange = { regionExpanded = !regionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRegion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Regione di Appartenenza") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = regionExpanded,
                    onDismissRequest = { regionExpanded = false }
                ) {
                    ItalianRegions.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(region) },
                            onClick = {
                                selectedRegion = region
                                regionExpanded = false
                                usernameFocusRequester.requestFocus()
                            }
                        )
                    }
                }
            }

            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; errorMessage = "" },
                label = { Text("Nome Utente (Username)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = "" },
                label = { Text("Password") },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Nascondi" else "Mostra", color = FijlkamNavy)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    executeLogin()
                }),
                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = FijlkamRed)
            }

            if (isLoading) {
                CircularProgressIndicator(color = FijlkamNavy)
            } else {
                Button(
                    onClick = { executeLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
                ) {
                    Text(
                        text = "ACCEDI ALL'AREA RISERVATA",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }

            if (statusMessage.isNotEmpty()) {
                Text(statusMessage, color = FijlkamNavy)
            }
        }
    }
}

@Composable
fun ChangePasswordScreen(
    user: Udg?,
    udgRepo: UdgRepository,
    onPasswordChanged: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FijlkamNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Cambio Password Obbligatorio", style = MaterialTheme.typography.titleLarge, color = FijlkamNavy)
                Text("Primo accesso rilevato per ${user?.username}. Inserisci una nuova password.")

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nuova Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Conferma Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )

                if (error.isNotEmpty()) {
                    Text(error, color = FijlkamRed)
                }

                Button(
                    onClick = {
                        if (newPassword.length < 4) {
                            error = "La password deve avere almeno 4 caratteri"
                        } else if (newPassword != confirmPassword) {
                            error = "Le password non coincidono"
                        } else {
                            scope.launch {
                                val success = udgRepo.updateUserPassword(user!!.username, newPassword)
                                if (success) {
                                    onPasswordChanged()
                                } else {
                                    error = "Errore aggiornamento password su Supabase"
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
                ) {
                    Text("Aggiorna Password e Procedi", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FijlkamHeaderBar(
    onRefresh: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Status bar area (White background)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        )

        // Tricolore accent stripe
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF009246))) // Green
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFFFFFF))) // White
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFCE2B37))) // Red
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo2024_compatto),
                        contentDescription = "Logo FIJLKAM",
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "FIJLKAM",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = FijlkamNavy,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "FEDERAZIONE ITALIANA JUDO LOTTA KARATE ARTI MARZIALI",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = FijlkamNavy,
                            lineHeight = 9.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0044A0), FijlkamDarkNavy)
                            )
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                            Text("☁", color = Color.White, fontSize = 16.sp)
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                            Text("🛡", color = Color.White, fontSize = 16.sp)
                        }
                        IconButton(onClick = onLogout, modifier = Modifier.size(32.dp)) {
                            Text("➔", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileCard(
    user: Udg,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FijlkamNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color(0xFF004099)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("👤", fontSize = 24.sp, color = Color.White)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${user.firstname} ${user.lastname}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text("🛡️", fontSize = 14.sp)
                    }
                    Text(
                        text = "@${user.username} • ${user.role ?: "Master"}",
                        fontSize = 13.sp,
                        color = Color(0xFFB0C4DE)
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                color = Color(0xFF0A192F)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(20.dp)
                            .background(FijlkamRed, shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = if (user.isAdmin == true) "MASTER - GESTIONE NAZIONALE FIJLKAM" else "UFFICIALE DI GARA - ${user.region?.uppercase() ?: "FIJLKAM"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FijlkamGold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformSummaryCards(
    competitionsCount: Int,
    usersCount: Int,
    availabilitiesPercent: Int,
    onCardClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Riepilogo Piattaforma",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = FijlkamNavy
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCardClick(1) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📅", fontSize = 22.sp)
                    Text(
                        text = "$competitionsCount",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FijlkamNavy
                    )
                    Text(
                        text = "Gare ",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCardClick(2) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("👥", fontSize = 22.sp)
                    Text(
                        text = "$usersCount",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FijlkamNavy
                    )
                    Text(
                        text = "Ufficiali",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCardClick(3) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📊", fontSize = 22.sp)
                    Text(
                        text = "$availabilitiesPercent%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FijlkamNavy
                    )
                    Text(
                        text = "Disponibilità",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsAdmin(
    onCreateCompetition: () -> Unit,
    onRegisterOfficial: () -> Unit,
    onSwitchPortal: () -> Unit,
    onViewReports: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Azioni Rapide Admin",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = FijlkamNavy
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCreateCompetition,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamBlueButton)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                   // Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Gare", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onRegisterOfficial,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamGreen)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  //  Text("👤+", fontSize = 16.sp, color = Color.White)
                    Text("Ufficiali di Gara", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Button(
            onClick = onSwitchPortal,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Passa a Vista Portale Ufficiale", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(
            onClick = onViewReports,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📊", fontSize = 16.sp)
                Text("Visualizza Report & Statistiche Dettagliate", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FijlkamNavy)
            }
        }
    }
}

@Composable
fun FijlkamBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        Pair("Panoramica", "🎛"),
        Pair("Gare", "📅"),
        Pair("Ufficiali", "👥"),
        Pair("Statistiche", "📊")
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FijlkamDarkNavy,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (title, icon) ->
                val isSelected = selectedTab == index
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isSelected) {
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            color = Color(0xFF2A437E)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 20.sp, color = Color.White)
                            }
                        }
                    } else {
                        Text(icon, fontSize = 20.sp, color = Color(0xFF8A99AD))
                    }
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF8A99AD)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(
    loggedUser: Udg,
    users: List<Udg>,
    competitions: List<Competition>,
    availabilities: List<Availability>,
    sports: List<sport> = emptyList(),
    udgRepo: UdgRepository,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isOfficialViewMode by remember { mutableStateOf(false) }

    if (isOfficialViewMode) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFFF3CD)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("👑 VISTA PORTALE UFFICIALE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF856404))
                    TextButton(onClick = { isOfficialViewMode = false }) {
                        Text("Torna ad Admin ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FijlkamNavy)
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                OfficialDashboard(
                    loggedUser = loggedUser,
                    users = users,
                    competitions = competitions,
                    availabilities = availabilities,
                    notifications = emptyList(),
                    compRepo = compRepo,
                    onRefresh = onRefresh,
                    onLogout = onLogout
                )
            }
        }
        return
    }

    val availPercent = if (availabilities.isNotEmpty()) {
        (availabilities.count { it.status == "Confermato" || it.status == "Disponibile" } * 100) / availabilities.size
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FijlkamBg)
    ) {
        FijlkamHeaderBar(onRefresh = onRefresh, onLogout = onLogout)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            UserProfileCard(user = loggedUser)
                        }
                        item {
                            PlatformSummaryCards(
                                competitionsCount = competitions.size,
                                usersCount = users.size,
                                availabilitiesPercent = availPercent,
                                onCardClick = { cardIndex -> selectedTab = cardIndex }
                            )
                        }
                        item {
                            QuickActionsAdmin(
                                onCreateCompetition = { selectedTab = 1 },
                                onRegisterOfficial = { selectedTab = 2 },
                                onSwitchPortal = { isOfficialViewMode = true },
                                onViewReports = { selectedTab = 3 }
                            )
                        }
                    }
                }
                1 -> Box(modifier = Modifier.fillMaxSize().padding(16.dp)) { AdminCompetitionsTab(competitions, availabilities, users, compRepo, onRefresh, loggedUser, sports) }
                2 -> Box(modifier = Modifier.fillMaxSize().padding(16.dp)) { AdminUsersTab(users, udgRepo, onRefresh, sports) }
                3 -> Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item { AdminStatsTab(competitions, availabilities, users, sports) }
                    }
                }
            }
        }

        FijlkamBottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}

@Composable
fun EditCompetitionDialog(
    competition: Competition,
    onDismiss: () -> Unit,
    onSave: (Competition) -> Unit
) {
    var title by remember { mutableStateOf(competition.title ?: "") }
    var startDate by remember { mutableStateOf(competition.startDate ?: "") }
    var endDate by remember { mutableStateOf(competition.endDate ?: "") }
    var region by remember { mutableStateOf(competition.region ?: "Lombardia") }
    var scope by remember { mutableStateOf(competition.scope ?: "Nazionale") }
    var selectedSport by remember { mutableStateOf(getSportNameById(competition.id_sport)) }
    var address by remember { mutableStateOf(competition.address ?: "") }
    var notes by remember { mutableStateOf(competition.notes ?: "") }
    var attachedFileName by remember { mutableStateOf(competition.attachedFileName ?: "") }
    var requiredUdg by remember { mutableStateOf((competition.requiredUdg ?: 3).toString()) }
    var requiredPdg by remember { mutableStateOf((competition.requiredPdg ?: 1).toString()) }
    var noticeDays by remember { mutableStateOf((competition.noticeDays ?: 15).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val updated = Competition(
                        id = competition.id,
                        title = title,
                        startDate = startDate,
                        endDate = endDate,
                        region = region,
                        scope = scope,
                        address = address,
                        notes = notes,
                        attachedFileName = attachedFileName.ifEmpty { null },
                        attachedFilePath = attachedFileName.ifEmpty { null },
                        requiredUdg = requiredUdg.toIntOrNull() ?: 3,
                        requiredPdg = requiredPdg.toIntOrNull() ?: 1,
                        noticeDays = noticeDays.toIntOrNull() ?: 15,
                        createdAt = competition.createdAt,
                        id_sport = getSportIdByName(selectedSport)
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
            ) {
                Text("Salva Correzioni", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = Color.Gray)
            }
        },
        title = { Text("✏️ Correggi Dettagli Gara", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titolo Gara") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DatePickerField(label = "Data Inizio", selectedDate = startDate, onDateSelected = { startDate = it }, modifier = Modifier.weight(1f))
                    DatePickerField(label = "Data Fine", selectedDate = endDate, onDateSelected = { endDate = it }, modifier = Modifier.weight(1f))
                }
                SportDropdown(selectedSport = selectedSport, onSportSelected = { selectedSport = it }, label = "Disciplina / Sport")
                RegionDropdown(selectedRegion = region, onRegionSelected = { region = it }, label = "Regione Gara")
                RoleDropdown(selectedRole = scope, onRoleSelected = { scope = it }, label = "Livello Gara (Regionale / Nazionale 1, 2, 3)")
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Indirizzo / Luogo") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = requiredUdg, onValueChange = { requiredUdg = it }, label = { Text("UDG Richiesti") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = requiredPdg, onValueChange = { requiredPdg = it }, label = { Text("PDG Richiesti") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = noticeDays, onValueChange = { noticeDays = it }, label = { Text("Giorni Preavviso") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = attachedFileName, onValueChange = { attachedFileName = it }, label = { Text("Allegato (File / Programma)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Note / Programma") }, modifier = Modifier.fillMaxWidth())
            }
        }
    )
}

@Composable
fun CompetitionSyntheticCard(
    comp: Competition,
    loggedUser: Udg,
    users: List<Udg>,
    availabilities: List<Availability>,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit,
    isAdmin: Boolean = false,
    onEditClick: ((Competition) -> Unit)? = null,
    onSendNoticeClick: ((Competition) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var statusMsg by remember { mutableStateOf("") }
    val existingAvail = availabilities.find { it.competitionId == comp.id && it.userId == loggedUser.id }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comp.title ?: "Senza titolo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FijlkamNavy
                    )
                    Text(
                        text = "🥋 ${getSportNameById(comp.id_sport)} • 📅 ${comp.startDate} / ${comp.endDate} • ${comp.region} (${comp.scope ?: "Nazionale"})",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isAdmin && onEditClick != null) {
                        IconButton(onClick = { onEditClick(comp) }, modifier = Modifier.size(32.dp)) {
                            Text("✏️", fontSize = 16.sp)
                        }
                    }
                    Text(if (expanded) "🔼 Dettagli" else "🔽 Dettagli", fontSize = 11.sp, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                }
            }

            if (expanded) {
                Divider(color = Color(0xFFE2E8F0))
                Text("📍 Indirizzo / Luogo: ${comp.address ?: "-"}", fontSize = 13.sp, color = Color.DarkGray)
                Text("👥 UDG Richiesti: ${comp.requiredUdg ?: 0} | PDG Richiesti: ${comp.requiredPdg ?: 0}", fontSize = 13.sp, color = Color.DarkGray)
                if (!comp.attachedFileName.isNullOrEmpty()) {
                    Text("📎 Allegato / Programma: ${comp.attachedFileName}", fontSize = 13.sp, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                }
                if (!comp.notes.isNullOrEmpty()) {
                    Text("📝 Note: ${comp.notes}", fontSize = 12.sp, color = Color.Gray)
                }

                if (isAdmin) {
                    AdminCompetitionAvailabilitiesSection(
                        competition = comp,
                        availabilities = availabilities,
                        users = users,
                        compRepo = compRepo,
                        onRefresh = onRefresh
                    )
                } else {
                    ConfirmedOfficialsSection(
                        competitionId = comp.id,
                        availabilities = availabilities,
                        users = users
                    )
                }

                Button(
                    onClick = {
                        addToCalendar(
                            title = "Gara FIJLKAM: ${comp.title}",
                            startDate = comp.startDate ?: "2025-06-01",
                            endDate = comp.endDate ?: "2025-06-02",
                            location = comp.address ?: comp.region ?: "FIJLKAM",
                            notes = comp.notes ?: "Gara ${comp.scope} FIJLKAM"
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
                ) {
                    Text("📅 Aggiungi Promemoria a Calendario (Allarme 24h)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (isAdmin && onSendNoticeClick != null) {
                    Button(
                        onClick = { onSendNoticeClick(comp) },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FijlkamBlueButton)
                    ) {
                        Text("🔔 Invia Notifica Sollecito agli Ufficiali", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            if (statusMsg.isNotEmpty()) {
                Text(statusMsg, fontSize = 12.sp, color = FijlkamNavy, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (existingAvail != null && !existingAvail.status.isNullOrEmpty()) {
                    val status = existingAvail.status
                    val isConfirmed = status == "Confermato"
                    val isDisponibile = status == "Disponibile"
                    Surface(
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        color = when {
                            isConfirmed -> Color(0xFFD4EDDA)
                            isDisponibile -> Color(0xFFFFF3CD)
                            else -> Color(0xFFF8D7DA)
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isConfirmed -> FijlkamGreen
                                isDisponibile -> Color(0xFF856404)
                                else -> FijlkamRed
                            }
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = when {
                                    isConfirmed -> "✅ Convocato (Disponibilità OK)"
                                    isDisponibile -> "⏳ Disponibilità Inviata (In attesa Admin)"
                                    else -> "❌ Disponibilità KO"
                                },
                                color = when {
                                    isConfirmed -> FijlkamGreen
                                    isDisponibile -> Color(0xFF856404)
                                    else -> FijlkamRed
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                val rawLogs = mutableListOf<String>()
                                fun addLog(m: String) { rawLogs.add("[Rigo ${rawLogs.size + 1}] $m") }

                                addLog("🚀 Invio dichiarazione di disponibilità per la gara '${comp.title}' (ID: ${comp.id})...")
                                addLog("👤 Ufficiale: @${loggedUser.username} (userId=${loggedUser.id})")

                                val avail = Availability(
                                    id = existingAvail?.id,
                                    competitionId = comp.id,
                                    userId = loggedUser.id,
                                    status = "Disponibile",
                                    note = "Disponibile - ${loggedUser.username}",
                                    updatedAt = null
                                )
                                val (ok, logRes) = compRepo.insertAvailabilityWithLog(avail)
                                addLog("💾 Esito disponibilità: $logRes")
                                if (ok && comp.id != null && loggedUser.id != null) {
                                    val (delOk, delLog) = compRepo.deleteNotificationForUserAndCompetition(loggedUser.id, comp.id)
                                    addLog("🗑️ Rimozione notifica: $delLog")
                                    addLog("✅ Disponibilità confermata e notifica rimossa con successo!")
                                }

                                statusMsg = rawLogs.joinToString("\n")
                                onRefresh()
                            }
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FijlkamGreen)
                    ) {
                        Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                val rawLogs = mutableListOf<String>()
                                fun addLog(m: String) { rawLogs.add("[Rigo ${rawLogs.size + 1}] $m") }

                                addLog("🚀 Registro non-disponibilità per la gara '${comp.title}' (ID: ${comp.id})...")
                                addLog("👤 Ufficiale: @${loggedUser.username} (userId=${loggedUser.id})")

                                val avail = Availability(
                                    id = existingAvail?.id,
                                    competitionId = comp.id,
                                    userId = loggedUser.id,
                                    status = "Non Disponibile",
                                    note = "Non disponibile",
                                    updatedAt = null
                                )
                                val (ok, logRes) = compRepo.insertAvailabilityWithLog(avail)
                                addLog("💾 Esito disponibilità: $logRes")
                                if (ok && comp.id != null && loggedUser.id != null) {
                                    val (delOk, delLog) = compRepo.deleteNotificationForUserAndCompetition(loggedUser.id, comp.id)
                                    addLog("🗑️ Rimozione notifica: $delLog")
                                    addLog("ℹ️ Non disponibilità salvata e notifica rimossa.")
                                }

                                statusMsg = rawLogs.joinToString("\n")
                                onRefresh()
                            }
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FijlkamRed)
                    ) {
                        Text("x", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        addToCalendar(
                            title = "Gara FIJLKAM: ${comp.title}",
                            startDate = comp.startDate ?: "2025-06-01",
                            endDate = comp.endDate ?: "2025-06-02",
                            location = comp.address ?: comp.region ?: "FIJLKAM",
                            notes = comp.notes ?: "Gara ${comp.scope} FIJLKAM"
                        )
                    },
                    modifier = Modifier.height(38.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Text("📅", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AdminCompetitionAvailabilitiesSection(
    competition: Competition,
    availabilities: List<Availability>,
    users: List<Udg>,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var statusMsg by remember { mutableStateOf("") }

    val compAvails = availabilities.filter { av ->
        av.competitionId == competition.id && (av.status == "Disponibile" || av.status == "Confermato")
    }

    val checkedUserIds = remember(availabilities, competition.id) {
        mutableStateMapOf<Long, Boolean>().apply {
            compAvails.forEach { av ->
                if (av.userId != null) {
                    put(av.userId, av.status == "Confermato")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9), shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "👥 Gestione Convocazioni Ufficiali Disponibili (${compAvails.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = FijlkamNavy
        )

        if (statusMsg.isNotEmpty()) {
            Text(statusMsg, fontSize = 11.sp, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        }

        if (compAvails.isEmpty()) {
            Text(
                text = "Nessun ufficiale ha ancora inviato la disponibilità per questa gara.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        } else {
            compAvails.forEach { av ->
                val official = users.find { it.id == av.userId }
                val uId = av.userId ?: return@forEach
                val isChecked = checkedUserIds[uId] ?: false
                val isAlreadyConfirmed = av.status == "Confermato"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, if (isChecked) FijlkamGreen else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { checkedUserIds[uId] = !isChecked }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checkedUserIds[uId] = it }
                            )
                            Column {
                                Text(
                                    text = "${official?.firstname ?: ""} ${official?.lastname ?: ""} (@${official?.username ?: uId})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FijlkamNavy
                                )
                                Text(
                                    text = "Ruolo: ${official?.role ?: "Regionale"} • Regione: ${official?.region ?: "-"}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                            color = if (isAlreadyConfirmed) Color(0xFFD4EDDA) else Color(0xFFFFF3CD)
                        ) {
                            Text(
                                text = if (isAlreadyConfirmed) "✅ Confermato" else "⏳ Disponibile",
                                fontSize = 10.sp,
                                color = if (isAlreadyConfirmed) FijlkamGreen else Color(0xFF856404),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val rawLogs = mutableListOf<String>()
                        fun addLog(m: String) { rawLogs.add("[Rigo ${rawLogs.size + 1}] $m") }

                        val compTitle = competition.title ?: "Gara ID ${competition.id}"
                        addLog("👑 Avvio conferma ufficiali selezionati per la gara '$compTitle'...")
                        var confirmCount = 0

                        compAvails.forEach { av ->
                            val uId = av.userId ?: return@forEach
                            val official = users.find { it.id == uId }
                            val isChecked = checkedUserIds[uId] ?: false
                            val newStatus = if (isChecked) "Confermato" else "Disponibile"

                            val updated = Availability(
                                id = av.id,
                                competitionId = competition.id,
                                userId = uId,
                                status = newStatus,
                                note = av.note,
                                updatedAt = null
                            )
                            val (ok, logRes) = compRepo.insertAvailabilityWithLog(updated)

                            if (ok && isChecked && av.status != "Confermato") {
                                confirmCount++
                                addLog("✅ Convocazione confermata per @${official?.username ?: uId}: $logRes")
                                val notif = Notification(
                                    userId = uId,
                                    title = "🎉 PARTECIPAZIONE CONFERMATA: $compTitle",
                                    message = "La tua partecipazione alla gara '$compTitle' è stata UFFICIALMENTE CONFERMATA dall'Amministratore!",
                                    competitionId = competition.id
                                )
                                compRepo.insertNotificationWithLog(notif)
                            }
                        }

                        addLog("🏁 Operazione completata: $confirmCount nuove convocazioni confermate!")
                        statusMsg = rawLogs.joinToString("\n")
                        onRefresh()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
            ) {
                Text(
                    text = "✉️ Conferma Ufficiali Selezionati & Invia Notifiche",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ConfirmedOfficialsSection(
    competitionId: Long?,
    availabilities: List<Availability>,
    users: List<Udg>
) {
    val confirmedAvails = availabilities.filter { av ->
        av.competitionId == competitionId && (av.status == "Confermato" || av.status == "Disponibile")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9), shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "👥 Ufficiali Confermati (${confirmedAvails.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FijlkamNavy
            )
        }

        if (confirmedAvails.isEmpty()) {
            Text(
                text = "Nessun ufficiale ha ancora confermato la presenza",
                fontSize = 11.sp,
                color = Color.Gray
            )
        } else {
            confirmedAvails.forEach { av ->
                val official = users.find { it.id == av.userId }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${official?.firstname ?: "Ufficiale"} ${official?.lastname ?: ""} (${official?.role ?: "Regionale"})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                    Surface(
                        color = Color(0xFFD1E7DD),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = av.status ?: "Confermato",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F5132),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminCompetitionsTabPreview() {
    AdminCompetitionsTab()
}

@Composable
fun AdminCompetitionsTab(
    competitions: List<Competition> = emptyList(),
    availabilities: List<Availability> = emptyList(),
    users: List<Udg> = emptyList(),
    compRepo: CompetitionRepository = CompetitionRepository(),
    onRefresh: () -> Unit = {},
    loggedUser: Udg = Udg(username = "", password = ""),
    sports: List<sport> = emptyList()
) {
    val todayStr = remember { formatMillisToDateString(getSystemTodayMillis()) }
    var title by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(todayStr) }
    var endDate by remember { mutableStateOf(todayStr) }
    
    val userLoginSport = loggedUser.scope?.trim()?.ifEmpty { null }
    var selectedSport by remember(loggedUser.scope) {
        mutableStateOf(userLoginSport ?: "Karate")
    }

    var region by remember { mutableStateOf("Regione") }
    var scope by remember { mutableStateOf("Regionale") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attachedFileName by remember { mutableStateOf("") }
    var requiredUdg by remember { mutableStateOf("") }
    var requiredPdg by remember { mutableStateOf("") }
    var noticeDays by remember { mutableStateOf("15") }
    var scopeMsg by remember { mutableStateOf("") }
    var showAddForm by remember { mutableStateOf(false) }
    var editingComp by remember { mutableStateOf<Competition?>(null) }
    val scopeCoroutine = rememberCoroutineScope()

    if (editingComp != null) {
        EditCompetitionDialog(
            competition = editingComp!!,
            onDismiss = { editingComp = null },
            onSave = { updated ->
                scopeCoroutine.launch {
                    val ok = compRepo.updateCompetition(updated)
                    if (ok) {
                        scopeMsg = "Dettagli gara aggiornati con successo!"
                        editingComp = null
                        onRefresh()
                    } else {
                        scopeMsg = "Errore durante l'aggiornamento della gara"
                    }
                }
            }
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Button(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
            ) {
                Text(if (showAddForm) "▲ Chiudi Modulo Inserimento" else "➕ Inserisci Nuova Gara", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (showAddForm) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Inserisci Nuova Gara FIJLKAM", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titolo Gara") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DatePickerField(
                                label = "Data Inizio",
                                selectedDate = startDate,
                                onDateSelected = { startDate = it },
                                modifier = Modifier.weight(1f)
                            )
                            DatePickerField(
                                label = "Data Fine",
                                selectedDate = endDate,
                                onDateSelected = { endDate = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (!userLoginSport.isNullOrBlank()) {
                            OutlinedTextField(
                                value = userLoginSport,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Disciplina / Sport (fissata dal tuo login: $userLoginSport)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            )
                        } else {
                            SportDropdown(
                                selectedSport = selectedSport,
                                onSportSelected = { selectedSport = it },
                                label = "Disciplina / Sport"
                            )
                        }

                        RegionDropdown(
                            selectedRegion = region,
                            onRegionSelected = { region = it },
                            label = "Regione Gara"
                        )
                        RoleDropdown(
                            selectedRole = scope,
                            onRoleSelected = { scope = it },
                            label = "Livello Gara (Regionale / Nazionale 1, 2, 3)"
                        )
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Indirizzo / Luogo") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = requiredUdg, onValueChange = { requiredUdg = it }, label = { Text("UDG Richiesti") }, modifier = Modifier.weight(1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            OutlinedTextField(value = requiredPdg, onValueChange = { requiredPdg = it }, label = { Text("PDG Richiesti") }, modifier = Modifier.weight(1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        }
                        OutlinedTextField(value = noticeDays, onValueChange = { noticeDays = it }, label = { Text("Giorni Preavviso") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        OutlinedTextField(value = attachedFileName, onValueChange = { attachedFileName = it }, label = { Text("Allegato Gara (File / Programma / Nome File)") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Note / Programma") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))

                        Button(
                            onClick = {
                                scopeCoroutine.launch {
                                    val finalSport = userLoginSport ?: selectedSport
                                    val comp = Competition(
                                        title = title,
                                        startDate = startDate,
                                        endDate = endDate,
                                        region = region,
                                        scope = scope,
                                        address = address,
                                        notes = notes,
                                        attachedFileName = attachedFileName.ifEmpty { null },
                                        attachedFilePath = attachedFileName.ifEmpty { null },
                                        requiredUdg = requiredUdg.toIntOrNull() ?: 3,
                                        requiredPdg = requiredPdg.toIntOrNull() ?: 1,
                                        noticeDays = noticeDays.toIntOrNull() ?: 15,
                                        id_sport = getSportIdByName(finalSport, sports)
                                    )
                                    val rawLogs = mutableListOf<String>()
                                    fun addLog(msg: String) {
                                        val lineNum = rawLogs.size + 1
                                        rawLogs.add("[Rigo $lineNum] $msg")
                                    }

                                    addLog("🚀 Avvio inserimento gara: '$title' (sport: $finalSport, id_sport: ${getSportIdByName(finalSport, sports)})...")

                                    val insertedComp = compRepo.insertCompetition(comp)
                                    if (insertedComp != null) {
                                        val compId = insertedComp.id ?: comp.id
                                        addLog("✅ Gara inserita con successo! ID generato: $compId")

                                        var targetUsers = users.filter { official ->
                                            official.isAdmin != true && official.id != null && (
                                                official.sport_name?.equals(finalSport, ignoreCase = true) == true ||
                                                official.scope?.equals(finalSport, ignoreCase = true) == true ||
                                                official.scope?.equals(scope, ignoreCase = true) == true ||
                                                isOfficialEligibleForCompetition(official, scope, region, finalSport)
                                            )
                                        }

                                        if (targetUsers.isEmpty()) {
                                            addLog("⚠️ Nessun ufficiale corrisponde allo scope '$finalSport'. Invio notifiche a tutti gli UDG non-admin (${users.count { it.isAdmin != true }})...")
                                            targetUsers = users.filter { u -> u.isAdmin != true && u.id != null }
                                        } else {
                                            addLog("🔍 Trovati ${targetUsers.size} ufficiali idonei per lo scope '$finalSport'.")
                                        }

                                        var sentCount = 0
                                        targetUsers.forEachIndexed { index, official ->
                                            val notif = Notification(
                                                userId = official.id,
                                                title = "Richiesta Disponibilità: $title",
                                                message = "Nuova gara inserita per lo scope '$finalSport' ($scope) dal $startDate al $endDate. Dichiara la tua disponibilità nell'app.",
                                                competitionId = compId
                                            )
                                            val (success, logDetail) = compRepo.insertNotificationWithLog(notif)
                                            if (success) {
                                                sentCount++
                                                addLog("📩 Notifica [${index + 1}/${targetUsers.size}] a @${official.username} (userId=${official.id}): $logDetail")
                                            } else {
                                                addLog("❌ Notifica [${index + 1}/${targetUsers.size}] a @${official.username} (userId=${official.id}) FALLITA: $logDetail")
                                            }
                                        }

                                        addLog("🏁 PROCESSO COMPLETATO: $sentCount notifiche su ${targetUsers.size} inserite in tabella 'notifications'.")
                                        scopeMsg = rawLogs.joinToString("\n")
                                        title = ""
                                        notes = ""
                                        attachedFileName = ""
                                        showAddForm = false
                                        onRefresh()
                                    } else {
                                        addLog("❌ ERRORE: Inserimento gara su Supabase fallito.")
                                        scopeMsg = rawLogs.joinToString("\n")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
                        ) {
                            Text("Salva Gara & Invia Preavviso", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (scopeMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(scopeMsg, color = FijlkamNavy, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Elenco Gare Non Chiuse (${competitions.size})", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        }

        items(competitions) { comp ->
            CompetitionSyntheticCard(
                comp = comp,
                loggedUser = loggedUser,
                users = users,
                availabilities = availabilities,
                compRepo = compRepo,
                onRefresh = onRefresh,
                isAdmin = true,
                onEditClick = { editingComp = it },
                onSendNoticeClick = { competition ->
                    scopeCoroutine.launch {
                        val targetUsers = users.filter { u ->
                            u.isAdmin != true &&
                            isOfficialEligibleForCompetition(u, competition.scope, competition.region) &&
                            availabilities.none { av -> av.competitionId == competition.id && av.userId == u.id }
                        }
                        if (targetUsers.isEmpty()) {
                            scopeMsg = "Tutti gli ufficiali convocati hanno già inviato risposta!"
                        } else {
                            targetUsers.forEach { official ->
                                val notif = Notification(
                                    userId = official.id,
                                    title = "⚠️ SOLLECITO PREAVVISO: ${competition.title}",
                                    message = "Preavviso gara (${competition.noticeDays ?: 15} giorni) per '${competition.title}' (${competition.startDate} - ${competition.endDate}). Non hai ancora risposto: dichiara la tua disponibilità nell'app!",
                                    competitionId = competition.id
                                )
                                compRepo.insertNotification(notif)
                            }
                            scopeMsg = "Sollecito preavviso inviato a ${targetUsers.size} ufficiali!"
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun EditUserDialog(
    user: Udg,
    onDismiss: () -> Unit,
    onSave: (Udg) -> Unit
) {
    var firstname by remember { mutableStateOf(user.firstname ?: "") }
    var lastname by remember { mutableStateOf(user.lastname ?: "") }
    var username by remember { mutableStateOf(user.username ?: "") }
    var password by remember { mutableStateOf(user.password ?: "") }
    var region by remember { mutableStateOf(user.region ?: "Regione di appartenenza") }
    var role by remember { mutableStateOf(user.role ?: "Regionale") }
    var isAdmin by remember { mutableStateOf(user.isAdmin == true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val updated = Udg(
                        id = user.id,
                        username = username,
                        password = password,
                        firstname = firstname,
                        lastname = lastname,
                        region = region,
                        role = role,
                        scope = user.scope ?: "Karate",
                        isAdmin = isAdmin,
                        isFirstLogin = user.isFirstLogin ?: false
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
            ) {
                Text("Salva Correzioni", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = Color.Gray)
            }
        },
        title = { Text("✏️ Correggi Dettagli Ufficiale", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = firstname, onValueChange = { firstname = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lastname, onValueChange = { lastname = it }, label = { Text("Cognome") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                RegionDropdown(selectedRegion = region, onRegionSelected = { region = it }, label = "Regione Ufficiale")
                RoleDropdown(selectedRole = role, onRoleSelected = { role = it }, label = "Ruolo Ufficiale")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                    Text("Amministratore")
                }
            }
        }
    )
}

@Composable
fun OfficialSyntheticCard(
    user: Udg,
    onEditClick: (Udg) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${user.firstname ?: ""} ${user.lastname ?: ""}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FijlkamNavy
                    )
                    Text(
                        text = "@${user.username} • ${user.role ?: "Regionale"} • ${user.region ?: "FIJLKAM"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(onClick = { onEditClick(user) }, modifier = Modifier.size(32.dp)) {
                        Text("✏️", fontSize = 16.sp)
                    }
                    Text(if (expanded) "🔼 Dettagli" else "🔽 Dettagli", fontSize = 11.sp, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                }
            }

            if (expanded) {
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Text("👤 Nome e Cognome: ${user.firstname} ${user.lastname}", fontSize = 13.sp, color = Color.DarkGray)
                Text("🆔 Username: @${user.username}", fontSize = 13.sp, color = Color.DarkGray)
                Text("🔑 Password: ${user.password ?: "-"}", fontSize = 13.sp, color = Color.DarkGray)
                Text("📍 Regione: ${user.region ?: "-"}", fontSize = 13.sp, color = Color.DarkGray)
                Text("🎖 Ruolo: ${user.role ?: "-"}", fontSize = 13.sp, color = Color.DarkGray)
                Text("🛡 Amministratore: ${if (user.isAdmin == true) "Sì" else "No"}", fontSize = 13.sp, color = Color.DarkGray)
                Text("🚪 Primo Accesso: ${if (user.isFirstLogin == true) "In attesa del primo login" else "Completato"}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AdminUsersTab(
    users: List<Udg>,
    udgRepo: UdgRepository,
    onRefresh: () -> Unit,
    sports: List<sport> = emptyList()
) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    val generatedUsername = remember(firstname, lastname) {
        if (firstname.isNotBlank() && lastname.isNotBlank()) {
            val initial = firstname.trim().lowercase().take(1)
            val cleanLast = lastname.trim().lowercase().replace("\\s+".toRegex(), "")
            "$initial.$cleanLast"
        } else {
            ""
        }
    }
    var password by remember { mutableStateOf("user123") }
    var selectedSport by remember { mutableStateOf("Karate") }
    var region by remember { mutableStateOf("Regione di appartenenza") }
    var role by remember { mutableStateOf("Regionale") }
    var isAdmin by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    var showAddForm by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<Udg?>(null) }
    val scope = rememberCoroutineScope()

    if (editingUser != null) {
        EditUserDialog(
            user = editingUser!!,
            onDismiss = { editingUser = null },
            onSave = { updated ->
                scope.launch {
                    val ok = udgRepo.updateUser(updated)
                    if (ok) {
                        msg = "Ufficiale aggiornato con successo!"
                        editingUser = null
                        onRefresh()
                    } else {
                        msg = "Errore durante l'aggiornamento dell'ufficiale"
                    }
                }
            }
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Button(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
            ) {
                Text(if (showAddForm) "▲ Chiudi Modulo Registrazione" else "➕ Registra Nuovo Ufficiale", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (showAddForm) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Inserisci Nuovo Ufficiale UDG / PDG", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = firstname, onValueChange = { firstname = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        OutlinedTextField(value = lastname, onValueChange = { lastname = it }, label = { Text("Cognome") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        OutlinedTextField(
                            value = generatedUsername,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Username (generato automaticamente)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password temporanea") }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        SportDropdown(
                            selectedSport = selectedSport,
                            onSportSelected = { selectedSport = it },
                            label = "Disciplina / Sport Ufficiale",
                            sportsList = sports
                        )
                        RegionDropdown(selectedRegion = region, onRegionSelected = { region = it }, label = "Regione Ufficiale")
                        RoleDropdown(selectedRole = role, onRoleSelected = { role = it }, label = "Livello / Ruolo Ufficiale (Regionale / Nazionale 1, 2, 3)")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                            Text("Amministratore")
                        }
                        Button(
                            onClick = {
                                if (generatedUsername.isEmpty()) {
                                    msg = "[Rigo 1] ⚠️ Inserisci nome e cognome per generare lo username."
                                    return@Button
                                }
                                scope.launch {
                                    val rawLogs = mutableListOf<String>()
                                    fun addLog(m: String) { rawLogs.add("[Rigo ${rawLogs.size + 1}] $m") }

                                    addLog("👤 Avvio registrazione nuovo ufficiale: '$firstname $lastname' (@$generatedUsername)...")
                                    addLog("📍 Regione: '$region', Ruolo: '$role', Sport: '$selectedSport', Amministratore: $isAdmin")

                                    val newUser = Udg(
                                        username = generatedUsername,
                                        password = password,
                                        firstname = firstname,
                                        lastname = lastname,
                                        region = region,
                                        role = "",
                                        scope = role ,//selectedSport,
                                       // sport_name = selectedSport,
                                        isAdmin = isAdmin,
                                        isFirstLogin = true
                                    )

                                    val (success, insertedUser, logRes) = udgRepo.insertUserWithLog(newUser)
                                    addLog("💾 Esito registrazione Supabase (udg): $logRes")

                                    if (success) {
                                        val newUserId = insertedUser?.id?.toString() ?: ""
                                        val sportId = (getSportIdByName(selectedSport, sports) ?: 1).toString()
                                        if (newUserId.isNotEmpty()) {
                                            val udgSport = Udg_sport(id_udg = newUserId, id_sport = sportId)
                                            val (udgSportOk, udgSportLog) = udgRepo.insertUdgSport(udgSport)
                                            addLog("🥋 Inserimento riga in 'udg_sport' (id_udg=$newUserId, id_sport=$sportId): $udgSportLog")
                                        }
                                        addLog("✅ Ufficiale @$generatedUsername inserito con successo!")
                                        firstname = ""
                                        lastname = ""
                                        showAddForm = false
                                        onRefresh()
                                    } else {
                                        addLog("❌ ERRORE: Registrazione ufficiale fallita su Supabase.")
                                    }
                                    msg = rawLogs.joinToString("\n")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FijlkamNavy)
                        ) {
                            Text("Salva Ufficiale", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (msg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(msg, color = FijlkamNavy, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Ufficiali UDG/PDG Registrati (${users.size})", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        }

        items(users) { u ->
            OfficialSyntheticCard(
                user = u,
                onEditClick = { editingUser = it }
            )
        }
    }
}

@Composable
fun AdminAvailabilitiesTab(
    availabilities: List<Availability>,
    competitions: List<Competition>,
    users: List<Udg>,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var statusMsg by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Gestione Convocazioni & Disponibilità (${availabilities.size})", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        if (statusMsg.isNotEmpty()) {
            Text(statusMsg, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        }

        if (availabilities.isEmpty()) {
            Text("Nessuna disponibilità registrata al momento.", fontSize = 12.sp, color = Color.Gray)
        } else {
            availabilities.forEach { av ->
                val comp = competitions.find { it.id == av.competitionId }
                val user = users.find { it.id == av.userId }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Gara: ${comp?.title ?: "ID ${av.competitionId}"}", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                        Text("Ufficiale: ${user?.firstname ?: ""} ${user?.lastname ?: ""} (@${user?.username ?: av.userId})", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = when (av.status) {
                                "Confermato" -> "Stato: ✅ Convocazione Confermata dall'Admin"
                                "Disponibile" -> "Stato: ⏳ Ufficiale Disponibile (Scegli se confermare o meno)"
                                "Non Disponibile" -> "Stato: ❌ Ufficiale Non Disponibile"
                                "Rifiutato" -> "Stato: ❌ Convocazione Rifiutata"
                                else -> "Stato: ${av.status ?: "In attesa"}"
                            },
                            fontWeight = FontWeight.Medium,
                            color = when (av.status) {
                                "Confermato" -> FijlkamGreen
                                "Disponibile" -> Color(0xFF856404)
                                else -> FijlkamRed
                            }
                        )
                        Text("Nota: ${av.note ?: "-"}", fontSize = 12.sp, color = Color.Gray)

                    if (av.status == "Confermato" || av.status == "Rifiutato" || av.status == "Non Disponibile") {
                        val isOk = av.status == "Confermato"
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(38.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = if (isOk) Color(0xFFD4EDDA) else Color(0xFFF8D7DA),
                            border = BorderStroke(1.dp, if (isOk) FijlkamGreen else FijlkamRed)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = if (isOk) "✅ Convocato dall'Admin (Disponibilità OK)" else "❌ Non Convocato (Disponibilità KO)",
                                    color = if (isOk) FijlkamGreen else FijlkamRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val rawLogs = mutableListOf<String>()
                                        fun addLog(m: String) { rawLogs.add("[Rigo ${rawLogs.size + 1}] $m") }

                                        val compName = comp?.title ?: "Gara ID ${av.competitionId}"
                                        val officialName = "@${user?.username ?: av.userId}"
                                        addLog("👑 Avvio conferma partecipazione per l'ufficiale $officialName alla gara '$compName'...")

                                        val updated = Availability(
                                            id = av.id,
                                            competitionId = av.competitionId,
                                            userId = av.userId,
                                            status = "Confermato",
                                            note = av.note,
                                            updatedAt = null
                                        )
                                        val (availOk, availLog) = compRepo.insertAvailabilityWithLog(updated)
                                        addLog("💾 Esito salvataggio disponibilità: $availLog")

                                        if (availOk && av.userId != null) {
                                            addLog("📩 Invio notifica di convocazione a $officialName (userId=${av.userId})...")
                                            val notif = Notification(
                                                userId = av.userId,
                                                title = "🎉 PARTECIPAZIONE CONFERMATA: $compName",
                                                message = "La tua partecipazione alla gara '$compName' è stata UFFICIALMENTE CONFERMATA dall'Amministratore!",
                                                competitionId = av.competitionId
                                            )
                                            val (notifOk, notifLog) = compRepo.insertNotificationWithLog(notif)
                                            if (notifOk) {
                                                addLog("✅ Notifica di conferma inviata con successo: $notifLog")
                                            } else {
                                                addLog("⚠️ Errore durante l'invio della notifica: $notifLog")
                                            }
                                        }
                                        addLog("🏁 Conferma partecipazione completata!")
                                        statusMsg = rawLogs.joinToString("\n")
                                        onRefresh()
                                    }
                                },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FijlkamGreen)
                            ) {
                                Text("Conferma Partecipazione", color = Color.White)
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val rawLogs = mutableListOf<String>()
                                        fun addLog(m: String) { rawLogs.add("[Rigo ${rawLogs.size + 1}] $m") }

                                        val compName = comp?.title ?: "Gara ID ${av.competitionId}"
                                        val officialName = "@${user?.username ?: av.userId}"
                                        addLog("🚫 Registrazione rifiuto convocazione per l'ufficiale $officialName alla gara '$compName'...")

                                        val updated = Availability(
                                            id = av.id,
                                            competitionId = av.competitionId,
                                            userId = av.userId,
                                            status = "Rifiutato",
                                            note = av.note,
                                            updatedAt = null
                                        )
                                        val (availOk, availLog) = compRepo.insertAvailabilityWithLog(updated)
                                        addLog("💾 Esito: $availLog")
                                        addLog("🏁 Operazione completata.")
                                        statusMsg = rawLogs.joinToString("\n")
                                        onRefresh()
                                    }
                                },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            ) {
                                Text("Rifiuta", color = FijlkamRed)
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStatsTab(
    competitions: List<Competition> = emptyList(),
    availabilities: List<Availability> = emptyList(),
    users: List<Udg> = emptyList(),
    sports: List<sport> = emptyList()
) {
    var selectedSportFilter by remember { mutableStateOf("Tutti") }
    val compRepo = remember { CompetitionRepository() }

    val filterSportId = getSportIdByName(selectedSportFilter, sports)
    val officialStats = remember(competitions, availabilities, users, filterSportId, sports) {
        compRepo.calculateOfficialConvocatedStats(users, availabilities, competitions, filterSportId, sports)
    }

    val activeSports = if (sports.isNotEmpty()) sports else listOf(
        sport(id = 1, sport_name = "Karate", descrizione = "Karate FIJLKAM"),
        sport(id = 2, sport_name = "Judo", descrizione = "Judo FIJLKAM"),
        sport(id = 3, sport_name = "Lotta", descrizione = "Lotta FIJLKAM")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Report e Statistiche FIJLKAM", style = MaterialTheme.typography.titleLarge, color = FijlkamNavy, fontWeight = FontWeight.Bold)

        // Card 1: Panoramica generale
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📊 Panoramica Generale", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                Text("Totale Gare inserite: ${competitions.size}", fontWeight = FontWeight.Medium)
                Text("Totale Ufficiali UDG/PDG: ${users.size}", fontWeight = FontWeight.Medium)
                Text("Totale Disponibilità registrate: ${availabilities.size}", fontWeight = FontWeight.Medium)
                Text("Convocati Confermati: ${availabilities.count { it.status == "Confermato" }}", color = FijlkamGreen, fontWeight = FontWeight.Bold)
                Text("In attesa / Non convocati: ${availabilities.count { it.status != "Confermato" }}", color = Color.Gray)
            }
        }

        // Card 2: Statistiche Gare per Sport (estratto dalla tabella sport)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🥋 Ripartizione Gare per Disciplina (Tabella Sport)", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)

                activeSports.forEach { sp ->
                    val sportId = sp.id?.toInt() ?: 0
                    val sportName = sp.sport_name ?: "Sport $sportId"
                    val sportDesc = sp.descrizione ?: ""
                    val compsCount = competitions.count { it.id_sport == sportId }
                    val sportCompIds = competitions.filter { it.id_sport == sportId }.mapNotNull { it.id }.toSet()
                    val sportConvocations = availabilities.count { it.status == "Confermato" && it.competitionId in sportCompIds }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("$sportName (ID $sportId)", fontWeight = FontWeight.SemiBold)
                            if (sportDesc.isNotBlank()) {
                                Text(sportDesc, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Text("$compsCount gare • $sportConvocations convocati", color = FijlkamNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Card 3: Statistiche Arbitri/Ufficiali che hanno dato disponibilità
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("📋 Statistiche Convocazioni Arbitri", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                Text("Elenco dei soli arbitri che hanno fornito disponibilità.", fontSize = 12.sp, color = Color.Gray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtra:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val filterOptions = listOf("Tutti") + activeSports.mapNotNull { it.sport_name }
                    filterOptions.forEach { sportName ->
                        FilterChip(
                            selected = selectedSportFilter == sportName,
                            onClick = { selectedSportFilter = sportName },
                            label = { Text(sportName, fontSize = 11.sp) }
                        )
                    }
                }

                if (officialStats.isEmpty()) {
                    Text("Nessun arbitro ha fornito disponibilità per il filtro selezionato.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    officialStats.forEach { stat ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = FijlkamBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${stat.firstname ?: ""} ${stat.lastname ?: ""}".trim().ifEmpty { stat.username },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = FijlkamNavy
                                    )
                                    Text(
                                        "Username: @${stat.username}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF003380)
                                    )
                                    Text(
                                        "Sport: ${stat.sportName ?: "Non specificato"}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Convocati: ${stat.totalConvocations} / ${stat.totalAvailabilities}",
                                        fontWeight = FontWeight.Bold,
                                        color = FijlkamGreen,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "${stat.convocationPercentage.toInt()}% Convocazioni",
                                        fontSize = 11.sp,
                                        color = FijlkamNavy,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfficialBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        Pair("Convocazioni", "📩"),
        Pair("Tutte le Gare", "📅"),
        Pair("Notifiche", "🔔")
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FijlkamDarkNavy,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (title, icon) ->
                val isSelected = selectedTab == index
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isSelected) {
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            color = Color(0xFF2A437E)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 20.sp, color = Color.White)
                            }
                        }
                    } else {
                        Text(icon, fontSize = 20.sp, color = Color(0xFF8A99AD))
                    }
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF8A99AD)
                    )
                }
            }
        }
    }
}

@Composable
fun OfficialPendingConvocationsTab(
    loggedUser: Udg,
    users: List<Udg>,
    competitions: List<Competition>,
    availabilities: List<Availability>,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }

    val pendingCompetitions = competitions.filter { comp ->
        val matchesUser = isOfficialEligibleForCompetition(loggedUser, comp.scope, comp.region)
        val hasResponded = availabilities.any { it.competitionId == comp.id && it.userId == loggedUser.id }
        matchesUser && !hasResponded
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Convocazioni in Preavviso in Attesa (${pendingCompetitions.size})",
            style = MaterialTheme.typography.titleMedium,
            color = FijlkamNavy,
            fontWeight = FontWeight.Bold
        )

        if (message.isNotEmpty()) {
            Text(message, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        }

        if (pendingCompetitions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ Nessuna convocazione in attesa! Hai risposto a tutte le gare in preavviso.", color = FijlkamGreen, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                }
            }
        } else {
            pendingCompetitions.forEach { comp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(comp.title ?: "Senza Titolo", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Surface(color = Color(0xFFFFF3CD), shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)) {
                                Text("⏳ Preavviso ${comp.noticeDays ?: 15}gg", fontSize = 11.sp, color = Color(0xFF856404), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Periodo: ${comp.startDate} - ${comp.endDate} | Regione: ${comp.region}")
                        Text("Indirizzo: ${comp.address ?: "-"}")
                        if (!comp.attachedFileName.isNullOrEmpty()) {
                            Text("📎 Allegato: ${comp.attachedFileName}", color = FijlkamNavy, fontWeight = FontWeight.Bold)
                        }

                        ConfirmedOfficialsSection(
                            competitionId = comp.id,
                            availabilities = availabilities,
                            users = users
                        )

                        val existingAvail = availabilities.find { it.competitionId == comp.id && it.userId == loggedUser.id }

                        if (existingAvail != null && !existingAvail.status.isNullOrEmpty()) {
                            val status = existingAvail.status
                            val isConfirmed = status == "Confermato"
                            val isDisponibile = status == "Disponibile"
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                color = when {
                                    isConfirmed -> Color(0xFFD4EDDA)
                                    isDisponibile -> Color(0xFFFFF3CD)
                                    else -> Color(0xFFF8D7DA)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        isConfirmed -> FijlkamGreen
                                        isDisponibile -> Color(0xFF856404)
                                        else -> FijlkamRed
                                    }
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = when {
                                            isConfirmed -> "🎉 CONVOCAZIONE CONFERMATA DALL'ADMIN (OK)"
                                            isDisponibile -> "⏳ Disponibilità Inviata (In Attesa Convocazione Admin)"
                                            else -> "❌ Disponibilità KO / Non Convocato"
                                        },
                                        color = when {
                                            isConfirmed -> FijlkamGreen
                                            isDisponibile -> Color(0xFF856404)
                                            else -> FijlkamRed
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val avail = Availability(
                                                id = existingAvail?.id,
                                                competitionId = comp.id,
                                                userId = loggedUser.id,
                                                status = "Disponibile",
                                                note = "Disponibile - ${loggedUser.username}",
                                                updatedAt = null
                                            )
                                            val ok = compRepo.insertAvailability(avail)
                                            if (ok && comp.id != null && loggedUser.id != null) {
                                                compRepo.deleteNotificationForUserAndCompetition(loggedUser.id, comp.id)
                                            }
                                            message = "Confermata disponibilità per ${comp.title} (notifica rimossa)"
                                            onRefresh()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FijlkamGreen)
                                ) {
                                    Text("Disponibile", color = Color.White, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val avail = Availability(
                                                id = existingAvail?.id,
                                                competitionId = comp.id,
                                                userId = loggedUser.id,
                                                status = "Non Disponibile",
                                                note = "Non disponibile",
                                                updatedAt = null
                                            )
                                            val ok = compRepo.insertAvailability(avail)
                                            if (ok && comp.id != null && loggedUser.id != null) {
                                                compRepo.deleteNotificationForUserAndCompetition(loggedUser.id, comp.id)
                                            }
                                            message = "Registrata non disponibilità per ${comp.title} (notifica rimossa)"
                                            onRefresh()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                ) {
                                    Text("Non Disponibile", color = FijlkamRed, fontSize = 12.sp)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                addToCalendar(
                                    title = "Gara FIJLKAM: ${comp.title}",
                                    startDate = comp.startDate ?: "2025-06-01",
                                    endDate = comp.endDate ?: "2025-06-02",
                                    location = comp.address ?: comp.region ?: "FIJLKAM",
                                    notes = comp.notes ?: "Gara ${comp.scope} FIJLKAM"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                        ) {
                            Text("📅 Invia a Calendario (Allarme 24h)", color = FijlkamNavy, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfficialDashboard(
    loggedUser: Udg,
    users: List<Udg>,
    competitions: List<Competition>,
    availabilities: List<Availability>,
    notifications: List<Notification>,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FijlkamBg)
    ) {
        FijlkamHeaderBar(onRefresh = onRefresh, onLogout = onLogout)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            UserProfileCard(user = loggedUser)
                        }
                        item {
                            OfficialPendingConvocationsTab(loggedUser, users, competitions, availabilities, compRepo, onRefresh)
                        }
                    }
                }
                1 -> Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OfficialCompetitionsTab(loggedUser, users, competitions, availabilities, compRepo, onRefresh)
                }
                2 -> Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OfficialNotificationsTab(loggedUser, notifications, compRepo, onRefresh)
                }
            }
        }

        OfficialBottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}

@Composable
fun OfficialCompetitionsTab(
    loggedUser: Udg,
    users: List<Udg>,
    competitions: List<Competition>,
    availabilities: List<Availability>,
    compRepo: CompetitionRepository,
    onRefresh: () -> Unit
) {
    val regionCompetitions = competitions.filter { comp ->
        isOfficialEligibleForCompetition(loggedUser, comp.scope, comp.region)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Gare Non Chiuse (${regionCompetitions.size})", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)

        regionCompetitions.forEach { comp ->
            CompetitionSyntheticCard(
                comp = comp,
                loggedUser = loggedUser,
                users = users,
                availabilities = availabilities,
                compRepo = compRepo,
                onRefresh = onRefresh,
                isAdmin = false
            )
        }
    }
}

@Composable
fun OfficialNotificationsTab(
    loggedUser: Udg,
    notifications: List<Notification>,
    compRepo: CompetitionRepository = CompetitionRepository(),
    onRefresh: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val userNotifications = notifications.filter { it.userId == loggedUser.id }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Le tue Notifiche (${userNotifications.size})", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
        }
        if (userNotifications.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("🔔 Nessuna notifica presente al momento.", color = Color.Gray)
                    }
                }
            }
        } else {
            items(userNotifications) { notif ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title ?: "Notifica", style = MaterialTheme.typography.titleMedium, color = FijlkamNavy, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notif.message ?: "", fontSize = 13.sp, color = Color.DarkGray)
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (notif.id != null) {
                                        compRepo.deleteNotificationById(notif.id)
                                    } else if (notif.competitionId != null && loggedUser.id != null) {
                                        compRepo.deleteNotificationForUserAndCompetition(loggedUser.id, notif.competitionId)
                                    }
                                    onRefresh()
                                }
                            }
                        ) {
                            Text("🗑️", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun FijlkamOverviewPreview() {
    val sampleUser = Udg(
        id = 1,
        username = "v.angellotti",
        password = "password",
        firstname = "Valentino",
        lastname = "Angellotti",
        region = "Campania",
        role = "Master Admin",
        scope = "Karate",
        isAdmin = true,
        isFirstLogin = false
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FijlkamBg)
        ) {
            FijlkamHeaderBar()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserProfileCard(user = sampleUser)
                PlatformSummaryCards(
                    competitionsCount = 3,
                    usersCount = 4,
                    availabilitiesPercent = 0
                )
                QuickActionsAdmin(
                    onCreateCompetition = {},
                    onRegisterOfficial = {},
                    onSwitchPortal = {},
                    onViewReports = {}
                )
            }
            FijlkamBottomNavigationBar(
                selectedTab = 0,
                onTabSelected = {}
            )
        }
    }
}
