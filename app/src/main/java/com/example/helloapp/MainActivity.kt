package com.example.helloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .padding(bottom = 80.dp)
        ) {
            // 状态栏
            StatusBar()

            // 日历区域
            WeekCalendar(
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it }
            )

            // 训练列表
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
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
            }

            Spacer(modifier = Modifier.weight(1f))

            // 底部按钮
            ActionButtons()
        }

        // 底部导航栏
        BottomNavigation(
            selectedItem = selectedNavItem,
            onItemSelected = { selectedNavItem = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2d3748)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("📶", fontSize = 12.sp)
            Text("📡", fontSize = 12.sp)
            Text("🔋", fontSize = 12.sp)
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
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2d3748)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = details,
                fontSize = 15.sp,
                color = Color(0xFF4a5568)
            )
        }

        Box(
            modifier = Modifier
                .size(80.dp)
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
                fontSize = 36.sp
            )
        }
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 25.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xB3FFFFFF)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "自由训练",
                color = Color(0xFF2d3748),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xCC6DD5C3)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "开始训练",
                color = Color(0xFF2d3748),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xB3FFFFFF)
            ),
            shape = RoundedCornerShape(18.dp)
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
                .padding(top = 12.dp, bottom = 28.dp),
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
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF2d3748) else Color(0xFF4a5568)
        )

        Spacer(modifier = Modifier.height(4.dp))

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
