package com.example.helloapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.helloapp.ui.theme.HelloAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloAppTheme {
                FitnessApp()
            }
        }
    }
}

@Composable
fun FitnessApp() {
    var selectedDay by remember { mutableStateOf(3) } // 周四选中
    var selectedNavItem by remember { mutableStateOf(0) }
    var showTrainingScreen by remember { mutableStateOf(false) }
    var currentExercise by remember { mutableStateOf("") }

    if (showTrainingScreen) {
        TrainingScreen(
            exerciseName = currentExercise,
            onBack = { showTrainingScreen = false }
        )
    } else {
        when (selectedNavItem) {
            0 -> HomeScreen(
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                selectedNavItem = selectedNavItem,
                onNavItemSelected = { selectedNavItem = it },
                onStartTraining = { exercise ->
                    currentExercise = exercise
                    showTrainingScreen = true
                }
            )
            1 -> AICoachScreen(
                selectedNavItem = selectedNavItem,
                onNavItemSelected = { selectedNavItem = it }
            )
            2 -> SettingsScreen(
                selectedNavItem = selectedNavItem,
                onNavItemSelected = { selectedNavItem = it }
            )
        }
    }
}

@Composable
fun HomeScreen(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    selectedNavItem: Int,
    onNavItemSelected: (Int) -> Unit,
    onStartTraining: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7B9DB8),
                        Color(0xFF9CB4C8)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
        ) {



            // 日历区域
            WeekCalendar(
                selectedDay = selectedDay,
                onDaySelected = onDaySelected
            )

            // 训练列表 - 可滚动
            TrainingList(selectedDay = selectedDay)

            // 底部按钮
            ActionButtons(onStartTraining = onStartTraining)
        }

        // 底部导航栏
        BottomNavigation(
            selectedItem = selectedNavItem,
            onItemSelected = onNavItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun AICoachScreen(
    selectedNavItem: Int,
    onNavItemSelected: (Int) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(text = "你好！我是你的AI教练，\n今天想练什么？", isUser = false),
            ChatMessage(text = "我想练胸肌，帮我安排一\n个计划。", isUser = true),
            ChatMessage(text = "计划已经置入", isUser = false),

        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8FA8BE))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    color = Color(0xFF2d3748),
                    modifier = Modifier.clickable { onNavItemSelected(0) }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "AI教练",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2d3748),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(40.dp))
            }

            // 聊天消息列表
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                messages.forEach { message ->
                    ChatMessageItem(message = message)
                }
            }

            // 输入栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 语音按钮
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB8C9D6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎤", fontSize = 24.sp)
                }

                // 输入框
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f),
                    placeholder = {
                        Text(
                            "输入消息...",
                            color = Color(0xFF6B7F92)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFB8C9D6),
                        unfocusedContainerColor = Color(0xFFB8C9D6),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                )

                // 发送按钮
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB8C9D6))
                        .clickable {
                            if (messageText.isNotEmpty()) {
                                messages.add(ChatMessage(messageText, true))
                                messageText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✈️", fontSize = 24.sp)
                }
            }
        }

        // 底部导航栏
        BottomNavigation(
            selectedItem = selectedNavItem,
            onItemSelected = onNavItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // AI头像
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9E4EC)),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // 消息气泡
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (message.isUser) Color(0xFFB8C9D6) else Color.White
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 15.sp,
                color = Color(0xFF2d3748),
                lineHeight = 20.sp
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // 用户头像
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9E4EC)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun SettingsScreen(
    selectedNavItem: Int,
    onNavItemSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7B9DB8),
                        Color(0xFF9CB4C8)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚙️",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "设置页面",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2d3748)
            )
        }

        // 底部导航栏
        BottomNavigation(
            selectedItem = selectedNavItem,
            onItemSelected = onNavItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)



@Composable
fun ColumnScope.TrainingList(selectedDay: Int) {
    when (selectedDay) {
        // 周五 (索引4) - 今日无训练计划
        4 -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "😌",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "今日无训练计划",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2d3748)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "好好休息，明天继续加油！",
                        fontSize = 14.sp,
                        color = Color(0xFF4a5568)
                    )
                }
            }
        }
        // 周六 (索引5) - 周六训练计划
        5 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TrainingCard(
                    title = "力量循环训练",
                    details = "5组 | 8次/组",
                    icon = "💪"
                )

                TrainingCard(
                    title = "间歇冲刺跑",
                    details = "6组 | 200米/组",
                    icon = "⚡"
                )

                TrainingCard(
                    title = "腹肌强化",
                    details = "4组 | 20次/组",
                    icon = "🔥"
                )

                TrainingCard(
                    title = "功能性训练",
                    details = "3组 | 15次/组",
                    icon = "🎯"
                )

                TrainingCard(
                    title = "恢复拉伸",
                    details = "1组 | 25分钟",
                    icon = "🧘‍♂️"
                )
            }
        }
        // 周四 (索引3) 和其他日期 - 默认训练计划
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TrainingCard(
                    title = "核心肌群激活",
                    details = "3组 | 15次/组",
                    icon = "🧘"
                )

                TrainingCard(
                    title = "全身爆发力训练",
                    details = "4组 | 10次/组",
                    icon = "🏋️"
                )

                TrainingCard(
                    title = "有氧耐力跑",
                    details = "1组 | 30分钟",
                    icon = "🏃"
                )

                TrainingCard(
                    title = "上肢力量训练",
                    details = "3组 | 12次/组",
                    icon = "💪"
                )

                TrainingCard(
                    title = "柔韧性拉伸",
                    details = "2组 | 20分钟",
                    icon = "🤸"
                )
                TrainingCard(
                    title = "力量循环训练",
                    details = "5组 | 8次/组",
                    icon = "💪"
                )
                TrainingCard(
                    title = "间歇冲刺跑",
                    details = "6组 | 200米/组",
                    icon = "⚡"
                )
                TrainingCard(
                    title = "腹肌强化",
                    details = "4组 | 20次/组",
                    icon = "🔥"
                )
                TrainingCard(
                    title = "功能性训练",
                    details = "3组 | 15次/组",
                    icon = "🎯"
                )
                TrainingCard(
                    title = "恢复拉伸",
                    details = "1组 | 25分钟",
                    icon = "🧘‍♂️"
                )
            }
        }
    }
}

@Composable
fun WeekCalendar(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    val dates = listOf("22", "23", "24", "25", "26", "27", "28")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, day ->
            DayItem(
                dayName = day,
                dayNumber = dates[index],
                isSelected = index == selectedDay,
                onClick = { onDaySelected(index) }
            )
        }
    }
}

@Composable
fun DayItem(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF2d3748) else Color(0xFF4a5568),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dayNumber,
            fontSize = 22.sp,
            color = Color(0xFF2d3748),
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF6DD5C3))
            )
        }
    }
}

@Composable
fun TrainingCard(
    title: String,
    details: String,
    icon: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x80BDCFDD))
            .clickable { }
            .padding(28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2d3748)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = details,
                fontSize = 16.sp,
                color = Color(0xFF4a5568)
            )
        }

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFBBDEFB)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 42.sp
            )
        }
    }
}

@Composable
fun ActionButtons(onStartTraining: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 35.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(
            onClick = { onStartTraining("自由训练") },
            modifier = Modifier
                .weight(1f)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xB3FFFFFF)
            ),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
        ) {
            Text(
                text = "自由训练",
                color = Color(0xFF2d3748),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = { onStartTraining("核心肌群激活 - 卷腹撑膝") },
            modifier = Modifier
                .weight(1f)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xCC6DD5C3)
            ),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
        ) {
            Text(
                text = "开始训练",
                color = Color(0xFF2d3748),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = { onStartTraining("模拟测试") },
            modifier = Modifier
                .weight(1f)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xB3FFFFFF)
            ),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
        ) {
            Text(
                text = "模拟测试",
                color = Color(0xFF2d3748),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BottomNavigation(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color(0xF2DCE6EE),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavItem(
                icon = "🏠",
                label = "首页",
                isSelected = selectedItem == 0,
                onClick = { onItemSelected(0) }
            )

            NavItem(
                icon = "🎧",
                label = "AI教练",
                isSelected = selectedItem == 1,
                onClick = { onItemSelected(1) }
            )

            NavItem(
                icon = "⚙️",
                label = "设置",
                isSelected = selectedItem == 2,
                onClick = { onItemSelected(2) }
            )
        }
    }
}

@Composable
fun NavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF2d3748) else Color(0xFF4a5568)
        )

        Spacer(modifier = Modifier.height(2.dp))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF6DD5C3))
            )
        }
    }
}

@Composable
fun TrainingScreen(
    exerciseName: String,
    onBack: () -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    var currentRep by remember { mutableStateOf(8) }
    var totalReps by remember { mutableStateOf(8) }
    var elapsedTime by remember { mutableStateOf(67) } // 秒数
    var hasCameraPermission by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 请求摄像头权限
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                hasCameraPermission = true
            }
            else -> {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3D4C5C))
    ) {
        // 摄像头预览区域
        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2d3748)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "需要摄像头权限",
                    fontSize = 24.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // 顶部信息栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.25f))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Text(
                    text = exerciseName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 进度条
                LinearProgressIndicator(
                    progress = currentRep.toFloat() / totalReps.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF6DD5C3),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$currentRep/$totalReps",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // 底部控制栏
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF2d3748).copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(bottom = 40.dp, top = 60.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 时间和次数显示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 完成次数
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4DD0C0)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$currentRep/$totalReps",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "次数",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                // 时间显示
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF5C6B7C)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val minutes = elapsedTime / 60
                        val seconds = elapsedTime % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "时间",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // 控制按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 停止按钮
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE85D5D))
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                }

                // 暂停/继续按钮
                IconButton(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5DD4C4))
                ) {
                    Text(
                        text = if (isPaused) "▶" else "⏸",
                        fontSize = 32.sp,
                        color = Color.White
                    )
                }

                // 设置按钮
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8A98A8))
                ) {
                    Text(
                        text = "✨",
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}
