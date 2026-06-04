package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.bible.BibleData
import com.example.data.bible.SearchResult
import com.example.data.model.BudgetTransaction
import com.example.data.model.Comment
import com.example.data.model.DiscussionPost
import com.example.data.model.Member
import com.example.data.model.DailyVerse
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.Tab
import kotlin.math.cos
import kotlin.math.sin

// Theme Constants (Clean Minimalism theme)
val NavyMain = Color(0xFF2563EB) // Royal Modern Blue
val NavyDark = Color(0xFF0F172A)  // Deep Sleek Slate
val GoldAccent = Color(0xFF1D4ED8) // Saturated accent blue
val CreamBg = Color(0xFFFDFBFF)    // Clean slate/lavender off-white bg
val GoldLight = Color(0xFFEFF6FF)  // Soft light blue selection bg
val SuccessGreen = Color(0xFF059669) // Clean modern emerald
val ErrorRed = Color(0xFFDC2626)     // High-contrast clean red

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appViewModel: AppViewModel = viewModel()
                    FPFiAppContainer(viewModel = appViewModel)
                }
            }
        }
    }
}

@Composable
fun FPFiAppContainer(viewModel: AppViewModel) {
    when (viewModel.currentScreen) {
        Screen.Splash -> SplashScreenView(viewModel)
        Screen.Login -> LoginScreenView(viewModel)
        Screen.Main -> MainDashboardView(viewModel)
    }
}

// ==========================================
// 1. SPLASH SCREEN (10 Seconds Cinematic)
// ==========================================
@Composable
fun SplashScreenView(viewModel: AppViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashWalk")
    val stepAnimValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WalkCycle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyMain))),
        contentAlignment = Alignment.Center
    ) {
        // Overlayed animated background particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = GoldAccent.copy(alpha = 0.1f), radius = 300f, center = Offset(size.width * 0.2f, size.height * 0.3f))
            drawCircle(color = Color.White.copy(alpha = 0.05f), radius = 150f, center = Offset(size.width * 0.8f, size.height * 0.7f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Cinematic Walking / Transition phase
            if (viewModel.splashProgress < 0.75f) {
                Text(
                    text = "Mandeha any am-piangonana ny mpikambana...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful custom walk rendering canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    val width = size.width
                    val height = size.height

                    // Draw grass path
                    drawPath(
                        path = Path().apply {
                            moveTo(0f, height * 0.8f)
                            quadraticTo(width * 0.5f, height * 0.75f, width, height * 0.82f)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        },
                        color = Color(0xFF0F224D)
                    )

                    // Draw a cute church building silhouette on the hill
                    drawPath(
                        path = Path().apply {
                            val cx = width * 0.85f
                            val cy = height * 0.8f
                            moveTo(cx - 30f, cy)
                            lineTo(cx - 30f, cy - 60f)
                            lineTo(cx, cy - 90f) // roof peak
                            lineTo(cx + 30f, cy - 60f)
                            lineTo(cx + 30f, cy)
                            close()
                        },
                        color = GoldAccent.copy(alpha = 0.8f)
                    )
                    // Draw Cross on roof
                    drawLine(
                        color = GoldAccent,
                        start = Offset(width * 0.85f, height * 0.8f - 90f),
                        end = Offset(width * 0.85f, height * 0.8f - 110f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = GoldAccent,
                        start = Offset(width * 0.85f - 10f, height * 0.8f - 102f),
                        end = Offset(width * 0.85f + 10f, height * 0.8f - 102f),
                        strokeWidth = 4f
                    )

                    // Draw family walk silhouettes moving across hill
                    val startOffset = (stepAnimValue / 100f) * (width * 0.6f)
                    val py = height * 0.78f

                    // Father silhouette
                    drawCircle(color = Color.White.copy(alpha = 0.9f), radius = 8f, center = Offset(20f + startOffset, py - 35f))
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(20f + startOffset, py - 27f), end = Offset(20f + startOffset, py - 10f), strokeWidth = 5f)
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(20f + startOffset, py - 10f), end = Offset(15f + startOffset, py), strokeWidth = 4f)
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(20f + startOffset, py - 10f), end = Offset(25f + startOffset, py), strokeWidth = 4f)

                    // Mother silhouette
                    drawCircle(color = Color.White.copy(alpha = 0.9f), radius = 7f, center = Offset(45f + startOffset, py - 32f))
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(45f + startOffset, py - 25f), end = Offset(45f + startOffset, py - 8f), strokeWidth = 5f)
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(45f + startOffset, py - 8f), end = Offset(40f + startOffset, py), strokeWidth = 4f)
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(45f + startOffset, py - 8f), end = Offset(50f + startOffset, py), strokeWidth = 4f)

                    // Child walking holding hands
                    drawCircle(color = Color.White.copy(alpha = 0.9f), radius = 5f, center = Offset(32f + startOffset, py - 20f))
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(32f + startOffset, py - 15f), end = Offset(32f + startOffset, py - 3f), strokeWidth = 4f)
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(32f + startOffset, py - 3f), end = Offset(28f + startOffset, py), strokeWidth = 3f)
                    drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(32f + startOffset, py - 3f), end = Offset(36f + startOffset, py), strokeWidth = 3f)
                }
            } else {
                // Reveal transition of church building and emblem logo (exactly like photo 1)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FPFiCorporateLogo(modifier = Modifier.size(240.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tonga soa eto amin'ny Fiangonana!",
                        color = GoldAccent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Cinematic progress bar and counter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = viewModel.activeSplashStep,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { viewModel.splashProgress },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = GoldAccent,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(viewModel.splashProgress * 10).toInt()} s / 10 s",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// Custom corporate emblem exactly like Photo 1
@Composable
fun FPFiCorporateLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White, CircleShape)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Drawn Golden dove flying over Holy Bible book & cross outline
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val cx = size.width / 2
                val cy = size.height / 2

                // Golden Cross
                drawLine(
                    color = GoldAccent,
                    start = Offset(cx + 25f, cy - 25f),
                    end = Offset(cx + 25f, cy + 45f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = GoldAccent,
                    start = Offset(cx + 5f, cy - 5f),
                    end = Offset(cx + 45f, cy - 5f),
                    strokeWidth = 6f
                )

                // Book (Holy Bible pages layers on left)
                val bookPath = Path().apply {
                    moveTo(cx - 50f, cy + 10f)
                    quadraticTo(cx - 35f, cy + 30f, cx, cy + 45f)
                    quadraticTo(cx - 20f, cy + 15f, cx - 40f, cy - 10f)
                    close()
                }
                drawPath(path = bookPath, color = GoldAccent.copy(alpha = 0.2f))
                drawPath(path = bookPath, color = GoldAccent, style = Stroke(width = 3f))

                val bookPage2 = Path().apply {
                    moveTo(cx - 55f, cy + 5f)
                    quadraticTo(cx - 40f, cy + 25f, cx - 5f, cy + 40f)
                    quadraticTo(cx - 25f, cy + 10f, cx - 45f, cy - 15f)
                    close()
                }
                drawPath(path = bookPage2, color = GoldAccent, style = Stroke(width = 2f))

                // Golden Flying Dove (Symmetrical wings, beak and sleek design)
                val dovePath = Path().apply {
                    moveTo(cx - 20f, cy - 15f) // Tail end
                    quadraticTo(cx - 40f, cy - 35f, cx - 10f, cy - 40f) // Left Wing
                    quadraticTo(cx + 5f, cy - 50f, cx + 20f, cy - 50f)  // Peak top
                    quadraticTo(cx + 35f, cy - 40f, cx + 45f, cy - 35f) // Beak
                    quadraticTo(cx + 25f, cy - 20f, cx + 15f, cy - 5f)  // Sinking torso
                    quadraticTo(cx - 5f, cy + 5f, cx - 20f, cy - 15f)   // Under curve
                    close()
                }
                drawPath(path = dovePath, color = GoldAccent)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "F.P.Fi",
                color = GoldAccent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "FIANGONANA PROTESTANTA",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "FIFOHAZANA",
                color = GoldAccent,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// =================================================
// 2. CONNECTED LOGIN GATEWAY (Cascade Background)
// =================================================
@Composable
fun LoginScreenView(viewModel: AppViewModel) {
    var isAdminLoginTab by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cascade waterfall flowing animated background
        CascadeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Beautiful header emblem
            FPFiCorporateLogo(modifier = Modifier.size(150.dp))

            Spacer(modifier = Modifier.height(20.dp))

            // Glassmorphism card container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.92f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fidirana amin'ny Fampiharana",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyMain
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(4.dp)
                    ) {
                        Button(
                            onClick = { isAdminLoginTab = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isAdminLoginTab) NavyMain else Color.Transparent,
                                contentColor = if (!isAdminLoginTab) Color.White else Color.DarkGray
                            ),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("tab_mpikambana")
                        ) {
                            Text("Mpikambana", fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                isAdminLoginTab = true
                                // Prefill details for quick testing
                                viewModel.loginName = "ADMIN"
                                viewModel.loginPhone = "0342994417"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdminLoginTab) GoldAccent else Color.Transparent,
                                contentColor = if (isAdminLoginTab) Color.White else Color.DarkGray
                            ),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("tab_mpitantana")
                        ) {
                            Text("Mpitantana (Admin)", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Inputs
                    OutlinedTextField(
                        value = viewModel.loginName,
                        onValueChange = { viewModel.loginName = it },
                        label = { Text("ANARANA (Anarana feno)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_name_input"),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NavyMain) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.loginPhone,
                        onValueChange = { viewModel.loginPhone = it },
                        label = { Text("Laharana finday") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_phone_input"),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NavyMain) },
                        singleLine = true
                    )

                    // Error feedback
                    viewModel.authError?.let { err ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = err,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_log_gate"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdminLoginTab) GoldAccent else NavyMain
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isAdminLoginTab) "Hiverina Admin" else "Hampiditra",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // Member sign-up gateway
                    if (!isAdminLoginTab) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { isRegistering = true }
                        ) {
                            Text(
                                text = "Misoratra anarana ho Mpikambana vaovao",
                                color = NavyMain,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // SignUp Registration form dialog
        if (isRegistering) {
            Dialog(onDismissRequest = { isRegistering = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fisoratana anarana feno",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyMain
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.signupName,
                            onValueChange = { viewModel.signupName = it },
                            label = { Text("Anarana feno") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = viewModel.signupPhone,
                            onValueChange = { viewModel.signupPhone = it },
                            label = { Text("Finday (Ohatra: 034...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = viewModel.signupAddress,
                            onValueChange = { viewModel.signupAddress = it },
                            label = { Text("Adiresy fonenana") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Choose Dropdown Selection Simulation
                        Text(
                            text = "Sampana idirana",
                            color = NavyDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 4.dp)
                        )
                        val listSampana = listOf(
                            "Sampana Tanora Kristiana (STK)",
                            "Sampana Vehivavy Lovasoa (SVL)",
                            "Sampana Sekoly Alahady (SSA)",
                            "Sokajy Lehilahy"
                        )
                        Column {
                            listSampana.forEach { s ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.signupSampana = s }
                                        .background(if (viewModel.signupSampana == s) GoldLight else Color.Transparent)
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = viewModel.signupSampana == s,
                                        onClick = { viewModel.signupSampana = s },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                    )
                                    Text(s, fontSize = 13.sp, color = Color.Black)
                                }
                            }
                        }

                        // Success feedback
                        viewModel.signupSuccessMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(msg, color = SuccessGreen, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.signup()
                                if (viewModel.signupSuccessMessage != null) {
                                    viewModel.loginName = ""
                                    viewModel.loginPhone = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyMain),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Alefaso mangata-pankatoavana", color = Color.White)
                        }

                        TextButton(onClick = { isRegistering = false }) {
                            Text("Hiverina hiditra", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// Kinetic Cascade animated water droplets Canvas backing
@Composable
fun CascadeBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "WaterfallAnim")
    val waterPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaterMove"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Flowing color backdrop
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F172A), // Slate Dark
                    Color(0xFF0284C7), // Sky blue
                    Color(0xFF0891B2)  // Cyan
                )
            ),
            size = size
        )

        // Draw layered flowing water waves and bubble particles
        val shift = (waterPosition / 100f) * h
        for (i in 0 until 12) {
            val rx = (i * w / 10) + cos((shift + i * 30f) * 0.05f) * 40f
            val ry = (shift + i * h / 12) % h
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = 15f + (i * 2 % 10),
                center = Offset(rx, ry)
            )
        }

        // Flowing mountain ledge waterfall curves
        val path1 = Path().apply {
            moveTo(w * 0.3f, 0f)
            quadraticTo(w * 0.35f, h * 0.5f, w * 0.25f, h)
            lineTo(w * 0.45f, h)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.42f, 0f)
            close()
        }
        drawPath(path1, color = Color.White.copy(alpha = 0.08f))

        val path2 = Path().apply {
            moveTo(w * 0.65f, 0f)
            quadraticTo(w * 0.6f, h * 0.5f, w * 0.72f, h)
            lineTo(w * 0.85f, h)
            quadraticTo(w * 0.75f, h * 0.5f, w * 0.8f, 0f)
            close()
        }
        drawPath(path2, color = Color.White.copy(alpha = 0.07f))
    }
}

// ===============================================
// 3. MAIN DASHBOARD HUB (Edge-to-Edge Navigation)
// ===============================================
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardView(viewModel: AppViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "F.P.Fi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Text(
                                text = "Fiangonana Protestanta Fifohazana",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    },
                    actions = {
                        // Score meter and Logout handle aligned exactly like the top items of Baiboly mockup
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFD3E4FF),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "Isa: ${user?.points ?: 0} pts",
                                    color = Color(0xFF1E3A8A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFFEF4444)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Mivoaka", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFDFBFF),
                        titleContentColor = Color(0xFF0F172A)
                    )
                )
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF7F9FF),
                tonalElevation = 0.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                val tabs = listOf(
                    Triple(Tab.Tongasoa, Icons.Default.Home, "Tongasoa"),
                    Triple(Tab.Baiboly, Icons.Default.Book, "Baiboly"),
                    Triple(Tab.Sampana, Icons.Default.People, "Sampana"),
                    Triple(Tab.Kilalao, Icons.Default.PlayArrow, "Kilalao"),
                    Triple(Tab.Hafa, Icons.Default.Menu, "Hafa")
                )

                tabs.forEach { (tab, icon, label) ->
                    val active = viewModel.currentTab == tab
                    NavigationBarItem(
                        selected = active,
                        onClick = { viewModel.currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (active) Color(0xFF1E3A8A) else Color(0xFF64748B)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (active) Color(0xFF1E3A8A) else Color(0xFF64748B),
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFD3E4FF)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBg)
                .padding(innerPadding)
        ) {
            when (viewModel.currentTab) {
                Tab.Tongasoa -> TongasoaTabView(viewModel)
                Tab.Baiboly -> BaibolyTabView(viewModel)
                Tab.Sampana -> SampanaTabView(viewModel)
                Tab.Kilalao -> KilalaoTabView(viewModel)
                Tab.Hafa -> HafaTabView(viewModel)
            }
        }
    }
}

// ============================================
// A. TONGASOA TAB VIEW (Welcome, Slogan & Verse)
// ============================================
@Composable
fun TongasoaTabView(viewModel: AppViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val rawVerse by viewModel.dailyVerse.collectAsStateWithLifecycle()
    val activeVerse = rawVerse ?: DailyVerse(1, "Mifohaza ianareo izay matory, ary mitsangàna amin'ny maty, fa hampahazava anao i Kristy.", "Efesiana 5:14")

    var isEditingVerse by remember { mutableStateOf(false) }
    var inputVerseText by remember { mutableStateOf("") }
    var inputVerseRef by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Church beautiful header banner greeting (Clean minimalist card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FPFiCorporateLogo(modifier = Modifier.size(90.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Fiangonana Protestanta Fifohazana",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tonga soa, ${user?.name ?: "Mpikambana"}!",
                    color = Color(0xFF2563EB),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Sampa Tanora : ${user?.sampana ?: ""}",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        // BLUE SLOGAN: « Mifohaza ianareo izay matory... »
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)), // Soft blue
            border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "« Mifohaza ianareo izay matory, ary mitsangàna amin'ny maty, fa hampahazava anao i Kristy. »",
                    color = Color(0xFF1E40AF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Text(
                    text = "- Efesiana 5:14",
                    color = Color(0xFF1D4ED8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // SECTION: Mana isan'andro / Andininy anio (MODIFIABLE BY ADMINISTRATOR ONLY)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD3E4FF)), // Ice Blue
            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFBFDBFE),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "MANA ISAN'ANDRO",
                                color = Color(0xFF1E3A8A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Admin Privileged Action Button
                    if (user?.isAdmin == true) {
                        IconButton(onClick = {
                            inputVerseText = activeVerse.text
                            inputVerseRef = activeVerse.reference
                            isEditingVerse = !isEditingVerse
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Ahitsio andininy", tint = Color(0xFF1E3A8A))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isEditingVerse) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Ahitsio ny teny masina ny andro (Admin)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputVerseText,
                            onValueChange = { inputVerseText = it },
                            label = { Text("Teny Masina") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputVerseRef,
                            onValueChange = { inputVerseRef = it },
                            label = { Text("Toko sy Andininy (Reference)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { isEditingVerse = false }) { Text("Ajanona", color = Color.Gray) }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.editDailyVerse(inputVerseText, inputVerseRef)
                                    isEditingVerse = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Tehirizo", color = Color.White)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "“ ${activeVerse.text} ”",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "- ${activeVerse.reference}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                }
            }
        }

        // SECTION: Vaovao sy Filazana (Fivoriana, Hetsika, Sokajy hafa)
        Text(
            text = "Vaovao sy Filazana",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)
        )

        // Fivoriana card
        NewsCategoryCard(
            title = "Fivoriana an-tsampana (STK)",
            details = "Ny alatsinainy hariva amin'ny 6 ora ao amin'ny tanàn-dehibe.",
            tag = "FIVORIANA",
            tagColor = Color(0xFF2563EB)
        )

        // Hetsika card
        NewsCategoryCard(
            title = "Fandaharan'asa Lehibe Fifohazana",
            details = "Hetsika fiderana lehibe hisian'ny famonjena sy fanasitranana ny Alahady tolakandro amin'ny 2:30 tolakandro.",
            tag = "HETSIKA",
            tagColor = Color(0xFFD97706)
        )

        // Sokajy hafa card
        NewsCategoryCard(
            title = "Andro fandraisana ny Fandaharana",
            details = "Fanokanana ny fanomezana sy adidy ho an'ny trano fiangonana an-toerana.",
            tag = "SOKAJY HAFA",
            tagColor = Color(0xFF64748B)
        )
    }
}

@Composable
fun NewsCategoryCard(title: String, details: String, tag: String, tagColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tagColor.copy(alpha = 0.12f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = tag.take(3).uppercase(),
                        color = tagColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ============================================
// B. BAIBOLY TAB VIEW (Holy Scriptures grid)
// ============================================
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BaibolyTabView(viewModel: AppViewModel) {
    var scriptureResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isShowingVersesSheet by remember { mutableStateOf(false) }

    val filteredBooks = remember(viewModel.bibleBookSearchQuery, viewModel.bibleTestamentIsNew) {
        BibleData.books.filter { book ->
            book.isNewTestament == viewModel.bibleTestamentIsNew &&
                    book.name.contains(viewModel.bibleBookSearchQuery, ignoreCase = true)
        }
    }

    val selectedBook = remember(viewModel.selectedBookId) {
        BibleData.books.find { it.id == viewModel.selectedBookId } ?: BibleData.books.first()
    }

    val currentChapterVerses = remember(viewModel.selectedBookId, viewModel.selectedChapter) {
        BibleData.generateVerses(viewModel.selectedBookId, viewModel.selectedChapter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Core Layout Header modeled after PHOTO 2
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Baiboly Masina", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(
                            text = "KATOLIKA SY PROTESTANTA • 66 BOKI",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FONT SCALE SLIDER: "HABON'NY SORATRA" (Matching Photo 2)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HABON'NY SORATRA:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Slider(
                        value = viewModel.bibleTextSize,
                        onValueChange = { viewModel.bibleTextSize = it },
                        valueRange = 12f..28f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .testTag("bible_font_slider"),
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF2563EB), activeTrackColor = Color(0xFF2563EB))
                    )
                    Text(
                        text = "${viewModel.bibleTextSize.toInt()}px",
                        fontSize = 12.sp,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // KEYWORD FIND TEXT SEARCHING: "Hitady andininy..." (Photo 2)
        OutlinedTextField(
            value = viewModel.bibleVerseSearchQuery,
            onValueChange = {
                viewModel.bibleVerseSearchQuery = it
                scriptureResults = BibleData.searchVerses(it)
            },
            placeholder = { Text("Hitady andininy (Ohatra: finoana)...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("bible_verse_search"),
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyMain)
        )

        // Show keyword matching database results if active query exists
        if (viewModel.bibleVerseSearchQuery.isNotBlank()) {
            Text(
                text = "Tombony hita (${scriptureResults.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyMain,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            scriptureResults.forEach { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            // auto navigate
                            val matchingBook = BibleData.books.find { it.name == res.bookName }
                            if (matchingBook != null) {
                                viewModel.selectedBookId = matchingBook.id
                                viewModel.selectedChapter = res.chapter
                                isShowingVersesSheet = true
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = GoldLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${res.bookName} ${res.chapter}:${res.verseNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        Text(res.text, fontSize = 13.sp, color = NavyDark)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // TABS Version selection: PROTESTANTA vs KATOLIKA (Photo 2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = { viewModel.bibleVersionIsProtestant = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.bibleVersionIsProtestant) Color(0xFF2563EB) else Color.White,
                    contentColor = if (viewModel.bibleVersionIsProtestant) Color.White else Color(0xFF475569)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, if (!viewModel.bibleVersionIsProtestant) Color(0xFFE2E8F0) else Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Text("📖 PROTESTANTA (FJKM)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.bibleVersionIsProtestant = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!viewModel.bibleVersionIsProtestant) Color(0xFF2563EB) else Color.White,
                    contentColor = if (!viewModel.bibleVersionIsProtestant) Color.White else Color(0xFF475569)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, if (viewModel.bibleVersionIsProtestant) Color(0xFFE2E8F0) else Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Text("✝️ KATOLIKA (DIEM/EKAR)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // TABS Testament filter: Tes. T vs Tes. V (Photo 2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = { viewModel.testamentIsNew(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!viewModel.bibleTestamentIsNew) Color(0xFF2563EB) else Color.White,
                    contentColor = if (!viewModel.bibleTestamentIsNew) Color.White else Color(0xFF475569)
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (viewModel.bibleTestamentIsNew) Color(0xFFE2E8F0) else Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Text("📙 Tes. T (39)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.testamentIsNew(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.bibleTestamentIsNew) Color(0xFF2563EB) else Color.White,
                    contentColor = if (viewModel.bibleTestamentIsNew) Color.White else Color(0xFF475569)
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (!viewModel.bibleTestamentIsNew) Color(0xFFE2E8F0) else Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Text("✝️ Tes. V (27)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // BOOK SEARCH sivana: "Sivana boki (Ohatra: Jaona)..."
        OutlinedTextField(
            value = viewModel.bibleBookSearchQuery,
            onValueChange = { viewModel.bibleBookSearchQuery = it },
            placeholder = { Text("Sivana boki (Ohatra: Jaona)...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("bible_book_search"),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        // Symmetrical Two-Column grid of Canonical Books listing
        Text("Zahao ny boky rehetra:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
        Column {
            for (idx in filteredBooks.indices step 2) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    BookButton(
                        book = filteredBooks[idx],
                        selected = viewModel.selectedBookId == filteredBooks[idx].id,
                        onClick = {
                            viewModel.selectedBookId = filteredBooks[idx].id
                            viewModel.selectedChapter = 1 // reset onto first chapter of that book
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp, bottom = 8.dp)
                    )
                    if (idx + 1 < filteredBooks.size) {
                        BookButton(
                            book = filteredBooks[idx + 1],
                            selected = viewModel.selectedBookId == filteredBooks[idx + 1].id,
                            onClick = {
                                viewModel.selectedBookId = filteredBooks[idx + 1].id
                                viewModel.selectedChapter = 1
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp, bottom = 8.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // CHAPTER SELECTOR GRID: "TOKO AO AMIN'NY SALAMO (150):"
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "TOKO AO AMIN'NY ${selectedBook.name.uppercase()} (${selectedBook.chaptersCount}):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = NavyDark,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        // Circle wrapping chapter selector buttons grid (exactly like photo 2)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 8
        ) {
            (1..selectedBook.chaptersCount).forEach { chap ->
                val active = viewModel.selectedChapter == chap
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (active) NavyMain else Color.White)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (active) NavyMain else Color.LightGray
                            ), CircleShape
                        )
                        .clickable {
                            viewModel.selectedChapter = chap
                            isShowingVersesSheet = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$chap",
                        color = if (active) Color.White else Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // POPUP SCRIPTURE DRAWER TO READ VERSES AT SPECIFIC FONT VALUE
        if (isShowingVersesSheet) {
            Dialog(onDismissRequest = { isShowingVersesSheet = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CreamBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .heightIn(max = 500.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedBook.name} • Toko ${viewModel.selectedChapter}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = NavyMain
                            )
                            IconButton(onClick = { isShowingVersesSheet = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(currentChapterVerses) { (vNo, valText) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$vNo",
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Black,
                                        fontSize = (viewModel.bibleTextSize - 1f).sp,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    Text(
                                        text = valText,
                                        fontSize = viewModel.bibleTextSize.sp, // RESPONSIVE FONT SLIDER
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = (viewModel.bibleTextSize * 1.4f).sp
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

// Helper to toggling testament inside viewModel
fun AppViewModel.testamentIsNew(isNew: Boolean) {
    this.bibleTestamentIsNew = isNew
}

@Composable
fun BookButton(book: com.example.data.bible.BibleBook, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(55.dp)
            .clickable { onClick() }
            .border(
                BorderStroke(2.dp, if (selected) GoldAccent else Color.Transparent),
                RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) GoldLight else Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = book.name,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = NavyDark
            )
            Text(
                text = "${book.chaptersCount} TOKO",
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================
// C. SAMPANA TAB VIEW (Profile, balance, forum)
// ============================================
@Composable
fun SampanaTabView(viewModel: AppViewModel) {
    var activeSubTab by remember { mutableStateOf("Profile") } // Profile, Audit, Forum

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab indicator header bar
        ScrollableTabRow(
            selectedTabIndex = when (activeSubTab) {
                "Profile" -> 0
                "Audit" -> 1
                else -> 2
            },
            containerColor = Color(0xFFF7F9FF),
            contentColor = Color(0xFF2563EB),
            edgePadding = 8.dp
        ) {
            Tab(
                selected = activeSubTab == "Profile",
                onClick = { activeSubTab = "Profile" },
                text = { Text("1. Profil", fontSize = 13.sp, color = if (activeSubTab == "Profile") Color(0xFF2563EB) else Color(0xFF64748B), fontWeight = if (activeSubTab == "Profile") FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeSubTab == "Audit",
                onClick = { activeSubTab = "Audit" },
                text = { Text("2. Balance & Vola", fontSize = 13.sp, color = if (activeSubTab == "Audit") Color(0xFF2563EB) else Color(0xFF64748B), fontWeight = if (activeSubTab == "Audit") FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeSubTab == "Forum",
                onClick = { activeSubTab = "Forum" },
                text = { Text("3. Dinika / Forum", fontSize = 13.sp, color = if (activeSubTab == "Forum") Color(0xFF2563EB) else Color(0xFF64748B), fontWeight = if (activeSubTab == "Forum") FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSubTab) {
                "Profile" -> SubTabMemberProfileView(viewModel)
                "Audit" -> SubTabBalanceConsultationView(viewModel)
                "Forum" -> SubTabDiscussionFeedView(viewModel)
            }
        }
    }
}

// 1. Profil Membre tab (Photo 3 styled visual layout card)
@Composable
fun SubTabMemberProfileView(viewModel: AppViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    var isSavingNotify by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile identity board EXACTLY modeled after PHOTO 3
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = NavyMain),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar simulation on left containing yellow border
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .border(BorderStroke(3.dp, GoldAccent), CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(90.dp),
                        tint = NavyMain
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Detail components on right
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "F.P.F.I MPIKAMBANA",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }

                    // Contact phone Row
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        // Cleanly formatted contact layout e.g. "034 29 944 17"
                        val rawFinday = user?.phone ?: "0342994417"
                        val formatted = if (rawFinday.length >= 10) {
                            "${rawFinday.substring(0, 3)} ${rawFinday.substring(3, 5)} ${rawFinday.substring(5, 8)} ${rawFinday.substring(8)}"
                        } else rawFinday

                        Text(
                            text = formatted,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Address row
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user?.address ?: "Lot 26 ter mahamasina",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    // Sampana Category tag row
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user?.sampana ?: "Sampana Tanora Kristiana (STK)",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Symmetrical large Badge indicator on bottom left of card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (user?.isAdmin == true) "MPITANTANA (ADMIN)" else "MPIKAMBANA TSARA TAIZA",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }

        // ESPACE PROJETS: allows member to manage their projects locally (saved to room DB)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Espace Projets (Ny Tetikasako)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Soraty eto ambany ny tetikasa, na ny adidy, ary tsindrio \"Tehirizo\" mba haditiana amin'ny banky angona.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = viewModel.editProjectsText,
                    onValueChange = { viewModel.editProjectsText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("member_projects_input"),
                    placeholder = { Text("Ohatra:\n1. Adidy STK alahady faha-25 may\n2. Fanasitranana fandraisana kely...") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (isSavingNotify) {
                    Text("Tetikasa voatahiry soa aman-tsara ao amin'ny database!", color = SuccessGreen, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                }

                Button(
                    onClick = {
                        viewModel.saveMemberProjects(viewModel.editProjectsText)
                        isSavingNotify = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tehirizo ny Tetikasa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

// 2. Financial Ledger tab (Consultative ledger open to all; Secretary/Admin can record changes)
@Composable
fun SubTabBalanceConsultationView(viewModel: AppViewModel) {
    val transactionList by viewModel.transactions.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    val totalRecette = remember(transactionList) {
        transactionList.filter { it.type == "RECETTE" }.sumOf { it.amount }
    }
    val totalDepense = remember(transactionList) {
        transactionList.filter { it.type == "DEPENSE" }.sumOf { it.amount }
    }
    val activeBalance = totalRecette - totalDepense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Color ledger summary scorecard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Drafitra fitantanam-bola an-tsampana (STK)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%,.2f", activeBalance)} Ar",
                    color = GoldAccent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text("Harena feno ankehitriny", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sokajy Recettes (+)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%,.0f", totalRecette)} Ar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sokajy Dépenses (-)", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%,.0f", totalDepense)} Ar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Secretary addition priviledges (Form card reveals to secretary/admin only)
        if (user?.isAdmin == true) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(BorderStroke(1.dp, GoldAccent), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = GoldLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fampidirana fitantanam-bola vaovao (Secrétaire / Mpitantana)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.budgetTypeInput == "RECETTE",
                                onClick = { viewModel.budgetTypeInput = "RECETTE" }
                            )
                            Text("Recette (+)", fontSize = 12.sp, color = Color.Black)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.budgetTypeInput == "DEPENSE",
                                onClick = { viewModel.budgetTypeInput = "DEPENSE" }
                            )
                            Text("Dépense (-)", fontSize = 12.sp, color = Color.Black)
                        }
                    }

                    OutlinedTextField(
                        value = viewModel.budgetAmountInput,
                        onValueChange = { viewModel.budgetAmountInput = it },
                        label = { Text("Vidin'ny vola (Ar)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.budgetLabelInput,
                        onValueChange = { viewModel.budgetLabelInput = it },
                        label = { Text("Hevitra / Label fanazavana") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.addBudgetTransaction() },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyMain),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Tepaho hitahirizana", color = Color.White)
                    }
                }
            }
        } else {
            // Friendly badge signaling read-only view
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = NavyMain)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vakiana fotsiny ihany (Ianao dia tsy Secrétaire ka tsy afaka manova).",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Ledger list
        Text("Tantara fitantanam-bola hita:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark, modifier = Modifier.padding(bottom = 8.dp))
        if (transactionList.isEmpty()) {
            Text("Tsy mbola misy tantara fitantanam-bola.", color = Color.Gray, fontStyle = FontStyle.Italic)
        } else {
            transactionList.forEach { ledger ->
                val isReceipt = ledger.type == "RECETTE"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isReceipt) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(
                                            alpha = 0.15f
                                        ), CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isReceipt) Icons.Default.Add else Icons.Default.Remove,
                                    contentDescription = null,
                                    tint = if (isReceipt) SuccessGreen else ErrorRed
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(ledger.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text(
                                    text = "Nampidirin'i ${ledger.authorName} • ${ledger.dateString}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Text(
                            text = "${if (isReceipt) "+" else "-"} ${String.format("%,.0f", ledger.amount)} Ar",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (isReceipt) SuccessGreen else ErrorRed
                        )
                    }
                }
            }
        }
    }
}

// 3. Forums and messaging tab (Photo 3 styled discussion sheet)
@Composable
fun SubTabDiscussionFeedView(viewModel: AppViewModel) {
    val posts by viewModel.postsFlow.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    var isReadingCommentsByPostId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick message publisher box at the top
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.newPostContent,
                    onValueChange = { viewModel.newPostContent = it },
                    placeholder = { Text("Inona no vaovao eo amin'ny Sampana?", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("forum_post_input"),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.addPost() },
                    modifier = Modifier
                        .background(NavyMain, CircleShape)
                        .size(45.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Publish", tint = Color.White)
                }
            }
        }

        // Post list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            items(posts) { post ->
                val likedList = post.likedByPhones.split(",").filter { it.isNotEmpty() }
                val isLiked = likedList.contains(user?.phone ?: "")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(GoldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.authorName.take(2).uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(post.authorName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text("${post.authorSampana} • Vaovao", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(post.content, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 19.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Like actions
                            Row(
                                modifier = Modifier.clickable { viewModel.likePost(post.id) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isLiked) ErrorRed else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${post.likesCount} Tian-drafitra",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            // Comments section toggling
                            Row(
                                modifier = Modifier.clickable { isReadingCommentsByPostId = post.id },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Comment, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                val commentsCount = viewModel.parseComments(post.commentsJson).size
                                Text(
                                    text = "$commentsCount Haneho hevitra",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active expanded discussion comments modal dialog sheets
        isReadingCommentsByPostId?.let { pid ->
            val post = posts.find { it.id == pid }
            if (post != null) {
                Dialog(onDismissRequest = { isReadingCommentsByPostId = null }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .heightIn(max = 450.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Lahatsoratra Hanehoana Hevitra", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyMain)
                                IconButton(onClick = { isReadingCommentsByPostId = null }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(post.content, fontSize = 13.sp, color = Color.Gray, fontStyle = FontStyle.Italic, modifier = Modifier.padding(bottom = 10.dp))

                            val cList = viewModel.parseComments(post.commentsJson)
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (cList.isEmpty()) {
                                    item {
                                        Text("Tsy mbola misy hevitra mipetraka.", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                } else {
                                    items(cList) { comment ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 6.dp),
                                            colors = CardDefaults.cardColors(containerColor = GoldLight)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(comment.authorName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyMain)
                                                Text(comment.content, fontSize = 13.sp, color = NavyDark)
                                            }
                                        }
                                    }
                                }
                            }

                            // Input comments footer fields in modal
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = viewModel.newCommentContent,
                                    onValueChange = { viewModel.newCommentContent = it },
                                    placeholder = { Text("Soraty ny hevitra...", fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        viewModel.addComment(post.id)
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = NavyMain)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// D. KILALAO TAB VIEW (Bible games suite)
// ============================================
@Composable
fun KilalaoTabView(viewModel: AppViewModel) {
    var selectedGameOption by remember { mutableStateOf("Quiz") } // Quiz vs Wheel

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark)
                .padding(8.dp)
        ) {
            Button(
                onClick = { selectedGameOption = "Quiz" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGameOption == "Quiz") GoldAccent else Color.Transparent,
                    contentColor = if (selectedGameOption == "Quiz") Color.Black else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("🔴 Quiz Biblique", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = { selectedGameOption = "Wheel" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGameOption == "Wheel") GoldAccent else Color.Transparent,
                    contentColor = if (selectedGameOption == "Wheel") Color.Black else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("🎡 Bonus Spin / Wheel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (selectedGameOption == "Quiz") {
                GameBibleQuizModule(viewModel)
            } else {
                GameBonusSpinModule(viewModel)
            }
        }
    }
}

@Composable
fun GameBibleQuizModule(viewModel: AppViewModel) {
    val activeQuestion = viewModel.getActiveQuizQuestion()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome and Game stats card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Lalao: Quiz Bibliaka mampahery", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sarotra : ${viewModel.selectedDifficulty}", color = GoldAccent, fontWeight = FontWeight.Bold)
                    Text("Haavo (Level): ${viewModel.quizActiveLevel} / 50", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                // Difficulty selector rows
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Mora", "Salasala", "Sarotra").forEach { diff ->
                        val active = viewModel.selectedDifficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) GoldAccent else Color.White.copy(alpha = 0.1f))
                                .clickable {
                                    viewModel.selectedDifficulty = diff
                                    viewModel.startQuizGame()
                                }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(diff, color = if (active) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (viewModel.isQuizGameOver) {
            // Failure screen: Start over at level 1 as mandated!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.12f)),
                border = BorderStroke(2.dp, ErrorRed)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tsarovy ny baiboly!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Diso ny valiny na lany ny fotoana! Araka ny fitsipika, miverina any amin'ny Level 1 ianao.",
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startQuizGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text("Hilalao indray (Level 1)", color = Color.White)
                    }
                }
            }
        } else if (viewModel.isQuizLevelCompleted) {
            // Perfect score success level complete
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.12f)),
                border = BorderStroke(2.dp, SuccessGreen)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Fandresen-dahatra lehibe! 🎉", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Azonao ny valiny 5/5 rehetra tamin'ity ambaratonga ity! Hahazo tombony +30 XP ary mivoha ny ambaratonga manaraka.",
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startQuizGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Andao hanomboka Level ${viewModel.quizActiveLevel}", color = Color.White)
                    }
                }
            }
        } else if (activeQuestion != null) {
            // ACTIVE QUESTION PORTION
            // Progress tracker index and Timer ticks
            Row(
                modifier = Modifier
                    .fillModifierCompactWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fanontaniana faha ${viewModel.quizQuestionIndex + 1} / 5",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (viewModel.quizCountdownSeconds < 10) ErrorRed else NavyMain,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${viewModel.quizCountdownSeconds}s",
                        color = if (viewModel.quizCountdownSeconds < 10) ErrorRed else NavyMain,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Simple timer progression bar
            LinearProgressIndicator(
                progress = { viewModel.quizCountdownSeconds / 30f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = if (viewModel.quizCountdownSeconds < 10) ErrorRed else NavyMain,
                trackColor = Color.LightGray.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Question
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = activeQuestion.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = NavyMain,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Spliced Options cards list
            activeQuestion.options.forEach { opt ->
                val isSelected = viewModel.quizSelectedAnswer == opt
                val isCorrect = opt == activeQuestion.correctAnswer
                val cardColor = when {
                    viewModel.isQuizAnswerSubmitted && isCorrect -> SuccessGreen.copy(alpha = 0.15f)
                    viewModel.isQuizAnswerSubmitted && isSelected && !isCorrect -> ErrorRed.copy(alpha = 0.15f)
                    isSelected -> GoldLight
                    else -> Color.White
                }
                val borderColor = when {
                    viewModel.isQuizAnswerSubmitted && isCorrect -> SuccessGreen
                    viewModel.isQuizAnswerSubmitted && isSelected && !isCorrect -> ErrorRed
                    isSelected -> GoldAccent
                    else -> Color.LightGray.copy(alpha = 0.4f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable(enabled = !viewModel.isQuizAnswerSubmitted) {
                            viewModel.submitQuizAnswer(opt)
                        },
                    border = BorderStroke(2.dp, borderColor),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = opt,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )

                        // feedback icons
                        if (viewModel.isQuizAnswerSubmitted) {
                            if (isCorrect) {
                                Icon(Icons.Default.Check, contentDescription = "Marina", tint = SuccessGreen)
                            } else if (isSelected) {
                                Icon(Icons.Default.Close, contentDescription = "Diso", tint = ErrorRed)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isQuizAnswerSubmitted) {
                Button(
                    onClick = { viewModel.nextQuizQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyMain),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hifindra amin'ny manaraka ➡️", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        } else {
            // First run trigger helper
            Button(
                onClick = { viewModel.startQuizGame() },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Hanomboka hilalao", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun GameBonusSpinModule(viewModel: AppViewModel) {
    val activeSelectionAngle by animateFloatAsState(
        targetValue = if (viewModel.isSpinning) 1440f else 0f,
        animationSpec = tween(durationMillis = 2500, easing = CubicBezierEasing(0.1f, 0.8f, 0.3f, 1f)),
        label = "WheelRot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Warning description as mandated by user prompt!
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = GoldLight),
            border = BorderStroke(1.5.dp, GoldAccent)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Torolalana sy Fitsipika kodiarana:",
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                // STRICT MANDATE MESSAGE
                Text(
                    text = "« Ahodino isaky ny 15 mn ny kodiarana! Tombony : +10, +20, +50 pts. Tandremo ny Baomba 💣 ! »",
                    fontSize = 13.sp,
                    color = NavyMain,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
            }
        }

        // Kinetic kinetic spin wheel canvas drawing
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(Color.White, CircleShape)
                .border(BorderStroke(4.dp, NavyDark), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(activeSelectionAngle)
            ) {
                val r = size.width / 2
                val listColors = listOf(
                    Color(0xFFFBBF24), Color(0xFFEF4444), Color(0xFF60A5FA),
                    Color(0xFF34D399), Color(0xFFC084FC), Color(0xFFF472B6),
                    Color(0xFF38BDF8), Color(0xFFA7F3D0)
                )

                // segment arcs
                for (i in 0 until 8) {
                    drawArc(
                        color = listColors[i],
                        startAngle = i * 45f,
                        sweepAngle = 45f,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                }

                // draw circular core boundaries
                drawCircle(color = NavyDark, radius = 24f, center = Offset(r, r))
            }

            // Arrow pin selector pointing downward represent top selection
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.TopCenter)
                    .rotate(90f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spin triggers
        Text(
            text = "Kodia tavela: ${viewModel.currentSpinsLeft} spins / 5 spins",
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = NavyDark
        )

        if (viewModel.spinCooldownSeconds > 0) {
            val mins = viewModel.spinCooldownSeconds / 60
            val secs = viewModel.spinCooldownSeconds % 60
            Text(
                text = "Miandry cooldown: ${String.format("%02d:%02d", mins, secs)}",
                color = ErrorRed,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.spinBonusWheel() },
            colors = ButtonDefaults.buttonColors(containerColor = NavyMain),
            enabled = !viewModel.isSpinning && viewModel.currentSpinsLeft > 0 && viewModel.spinCooldownSeconds == 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (viewModel.isSpinning) "Miodina..." else "Ahodino ny Kodiarana 🎡",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Outcome notify displays
        viewModel.spinOutcome?.let { res ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GoldLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Vokany: $res",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = NavyMain
                    )
                    viewModel.spinPointsNotification?.let { notif ->
                        Text(notif, fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }

        // Holy scripture popup if lands on Bible
        viewModel.activeSpinBibleVerse?.let { bScripture ->
            Dialog(onDismissRequest = { viewModel.activeSpinBibleVerse = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamBg)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tenin'Andriamanitra sarobidy:", fontWeight = FontWeight.Bold, color = NavyMain)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = bScripture,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.activeSpinBibleVerse = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
                        ) {
                            Text("Efa voavaky (Haka +50 pts)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Modifier utility
fun Modifier.fillModifierCompactWidth() = this.fillMaxWidth()

// ============================================
// E. HAFA TAB VIEW (Schedules, Offerings, Admin)
// ============================================
@Composable
fun HafaTabView(viewModel: AppViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val memberList by viewModel.members.collectAsStateWithLifecycle()

    var activeHafaSection by remember { mutableStateOf("Index") } // Index, Filazana, Fandaharana, Giving, Admin

    Column(modifier = Modifier.fillMaxSize()) {
        if (activeHafaSection != "Index") {
            // Symmetrical back arrow navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .clickable { activeHafaSection = "Index" }
                    .padding(14.dp)
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF2563EB))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Hiverina amin'ny lisitra", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeHafaSection) {
                "Index" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Fandaminana sy vaovao hafa", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NavyDark, modifier = Modifier.padding(bottom = 12.dp))

                        HafaItemRow(title = "📣 Fiangonana Filazana Lehibe", sub = "Vakio ny fitambaran'ireo Filazana rehetra ankehitriny.") { activeHafaSection = "Filazana" }
                        HafaItemRow(title = "📅 Fandaharana & Cultes", sub = "Fandaharam-potoana ny am-piangonana isan-tsampana.") { activeHafaSection = "Fandaharana" }
                        HafaItemRow(title = "💝 Fanomezana & Rakitra Masina", sub = "Information MVola, Orange Money handefasanao Rakitra.") { activeHafaSection = "Giving" }

                        // ONLY Admin account can access Fitantanana (Account Validation list as directed)
                        if (user?.isAdmin == true) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Admin Privileged Area", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent, modifier = Modifier.padding(bottom = 8.dp))
                            HafaItemRow(
                                title = "⚙️ Fitantanana sy Fankatoavana",
                                sub = "Fankatoavana ireo fizorana mangataka vaovao."
                            ) { activeHafaSection = "Admin" }
                        }
                    }
                }
                "Filazana" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text("Fiangonana Filazana", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NavyDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        ExpandedDetailCard(
                            title = "Fanoloran'ny Dabilio Vaovao",
                            text = "Mankasitraka ireo malala-tanana nanolotra dabilio hazo vaovao 5 ho an'ny trano fiangonantsika. Hitahy anareo ny Tompo."
                        )
                        ExpandedDetailCard(
                            title = "Famonjena sy fitaizana ny ankizy",
                            text = "Sekoly Alahady rehetra dia miara-mivory ny asabotsy amin'ny 2 ora tolakandro ho amin'ny fanazaran-kira lehibe."
                        )
                    }
                }
                "Fandaharana" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text("Fandaharana fitsompoana", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NavyDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        ExpandedDetailCard(
                            title = "Fandaharana Alahady 7 Jona 2026",
                            text = "Amboarampeo STK hihira amin'ny fanompoam-potoana voalohany amin'ny 6:30 maraina. Tongava maro hitsena ny fahasoavana."
                        )
                        ExpandedDetailCard(
                            title = "Tilikambo sy vavaka alatsinainy",
                            text = "Fotoam-bavaka asandratra isan-tanàna ny alatsinainy amin'ny 5 ora tolaka. Hozonina ny herin'ny maizina."
                        )
                    }
                }
                "Giving" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fanomezana & Rakitra Masina", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NavyDark, textAlign = TextAlign.Center)
                        Text(
                            text = "Azonao atao ny mandefa ny Rakitra, ny Adidy, na ny adidy isan-tsampana amin'ny alalan'ny finday handraisana anjara mivantana:",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        GivingContactMethodCard(logo = "MVOLA", number = "034 29 944 17", name = "F.P.Fi Treasurer")
                        GivingContactMethodCard(logo = "ORANGE MONEY", number = "032 11 222 33", name = "Secretary F.P.Fi")
                    }
                }
                "Admin" -> {
                    // Membership Approvals and Registration controller (supports at least 500 records)
                    val pending = remember(memberList) { memberList.filter { it.status == "PENDING" } }
                    val approved = remember(memberList) { memberList.filter { it.status == "APPROVED" } }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Text("Lisitry ny Fitsapana & Fankatoavana", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NavyDark)
                            Text("Azonao atao ny manome fankatoavana amin'ireo fidirana vaovao mangataka.", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Pending entries header
                        item {
                            Text("Miandry Fankatoavana (${pending.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ErrorRed, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        if (pending.isEmpty()) {
                            item {
                                Text("Tsy misy fangatahana miandry amin'izao fotoana izao.", color = Color.Gray, fontStyle = FontStyle.Italic, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
                            }
                        } else {
                            items(pending) { m ->
                                AdminApprovalMemberRow(viewModel = viewModel, member = m)
                            }
                        }

                        // Verified accepted registered list (database load size supports 500 members)
                        item {
                            Text("Mpikambana Efa Voasoratra (${approved.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        if (approved.isEmpty()) {
                            item {
                                Text("Tsy mbola misy mpikambana efa nankatoavina.", color = Color.Gray, fontStyle = FontStyle.Italic, fontSize = 13.sp)
                            }
                        } else {
                            items(approved) { m ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(m.name, fontWeight = FontWeight.Bold, color = NavyDark)
                                        Text("${m.phone} • ${m.sampana}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = SuccessGreen)
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
fun AdminApprovalMemberRow(viewModel: AppViewModel, member: Member) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .border(BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
            Text("Finday: ${member.phone}", fontSize = 13.sp, color = Color.DarkGray)
            Text("Adiresy: ${member.address}", fontSize = 12.sp, color = Color.Gray)
            Text("Sampana: ${member.sampana}", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { viewModel.validateMember(member.phone, false) },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Lavina", fontSize = 12.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.validateMember(member.phone, true) },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Ankasitraho", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GivingContactMethodCard(logo: String, number: String, name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(logo, fontWeight = FontWeight.Black, color = NavyMain, fontSize = 15.sp)
                Text(number, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Text(name, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun ExpandedDetailCard(title: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
        }
    }
}

@Composable
fun HafaItemRow(title: String, sub: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
                Text(sub, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
