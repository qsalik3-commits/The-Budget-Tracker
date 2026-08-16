package com.example.ui.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BudgetViewModel
import com.example.ui.CurrencyInfo
import kotlinx.coroutines.launch
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(viewModel: BudgetViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Normal", "SIP", "EMI", "AI Forecast & Split")
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Financial Calculators", fontWeight = FontWeight.Normal) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> NormalCalculator()
                    1 -> SIPCalculator(currency = selectedCurrency)
                    2 -> EMICalculator(currency = selectedCurrency)
                    3 -> AiToolsTab(viewModel = viewModel, currency = selectedCurrency)
                }
            }
        }
    }
}

@Composable
fun NormalCalculator() {
    var display by remember { mutableStateOf("0") }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var shouldClearDisplay by remember { mutableStateOf(false) }

    fun onNumberClick(number: String) {
        if (display == "0" || shouldClearDisplay) {
            display = number
            shouldClearDisplay = false
        } else {
            display += number
        }
    }

    fun onOperatorClick(op: String) {
        if (operand1 == null) {
            operand1 = display.toDoubleOrNull()
        } else if (!shouldClearDisplay && operator != null) {
            val op2 = display.toDoubleOrNull()
            if (op2 != null) {
                val result = when (operator) {
                    "+" -> operand1!! + op2
                    "-" -> operand1!! - op2
                    "×" -> operand1!! * op2
                    "÷" -> if (op2 != 0.0) operand1!! / op2 else 0.0
                    else -> operand1!!
                }
                display = result.toString().removeSuffix(".0")
                operand1 = result
            }
        }
        operator = op
        shouldClearDisplay = true
    }

    fun onEqualClick() {
        if (operand1 != null && operator != null) {
            val op2 = display.toDoubleOrNull()
            if (op2 != null) {
                val result = when (operator) {
                    "+" -> operand1!! + op2
                    "-" -> operand1!! - op2
                    "×" -> operand1!! * op2
                    "÷" -> if (op2 != 0.0) operand1!! / op2 else 0.0
                    else -> operand1!!
                }
                display = result.toString().removeSuffix(".0")
                operand1 = null
                operator = null
                shouldClearDisplay = true
            }
        }
    }

    fun onClearClick() {
        display = "0"
        operand1 = null
        operator = null
        shouldClearDisplay = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (operand1 != null && operator != null) "${operand1.toString().removeSuffix(".0")} $operator" else "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = display,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        // Keypad filling remaining space
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        val modifier = if (btn == "0") Modifier.weight(2f) else Modifier.weight(1f)
                        val bgColor = when (btn) {
                            "C", "±", "%" -> MaterialTheme.colorScheme.errorContainer
                            "÷", "×", "-", "+", "=" -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val textColor = when (btn) {
                            "C", "±", "%" -> MaterialTheme.colorScheme.onErrorContainer
                            "÷", "×", "-", "+", "=" -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Button(
                            onClick = {
                                when (btn) {
                                    "C" -> onClearClick()
                                    "÷", "×", "-", "+" -> onOperatorClick(btn)
                                    "=" -> onEqualClick()
                                    "±" -> {
                                        val current = display.toDoubleOrNull()
                                        if (current != null && current != 0.0) {
                                            display = (-current).toString().removeSuffix(".0")
                                        }
                                    }
                                    "%" -> {
                                        val current = display.toDoubleOrNull()
                                        if (current != null) {
                                            display = (current / 100).toString().removeSuffix(".0")
                                        }
                                    }
                                    else -> onNumberClick(btn)
                                }
                            },
                            modifier = modifier.fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(containerColor = bgColor, contentColor = textColor),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = btn, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SIPCalculator(currency: CurrencyInfo) {
    var monthlyInvestment by remember { mutableStateOf("") }
    var expectedReturnRate by remember { mutableStateOf("") }
    var timePeriodYears by remember { mutableStateOf("") }

    val mInvestment = monthlyInvestment.toDoubleOrNull() ?: 0.0
    val eReturnRate = expectedReturnRate.toDoubleOrNull() ?: 0.0
    val tYears = timePeriodYears.toDoubleOrNull() ?: 0.0

    val i = (eReturnRate / 12) / 100
    val n = tYears * 12

    val futureValue = if (i > 0 && n > 0 && mInvestment > 0) {
        mInvestment * (( (1 + i).pow(n) - 1 ) / i) * (1 + i)
    } else if (n > 0 && mInvestment > 0) {
        mInvestment * n
    } else {
        0.0
    }

    val totalInvestment = mInvestment * n
    val estReturns = futureValue - totalInvestment

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = monthlyInvestment,
            onValueChange = { monthlyInvestment = it },
            label = { Text("Monthly Investment (${currency.symbol})") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = expectedReturnRate,
            onValueChange = { expectedReturnRate = it },
            label = { Text("Expected Return Rate (p.a %)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = timePeriodYears,
            onValueChange = { timePeriodYears = it },
            label = { Text("Time Period (Years)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ResultRow(label = "Invested Amount", value = currency.format(totalInvestment))
                ResultRow(label = "Est. Returns", value = currency.format(estReturns))
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                ResultRow(
                    label = "Total Future Value",
                    value = currency.format(futureValue),
                    isTotal = true
                )
            }
        }
    }
}

@Composable
fun EMICalculator(currency: CurrencyInfo) {
    var loanAmount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var loanTenure by remember { mutableStateOf("") }

    val p = loanAmount.toDoubleOrNull() ?: 0.0
    val rAnnual = interestRate.toDoubleOrNull() ?: 0.0
    val tYears = loanTenure.toDoubleOrNull() ?: 0.0

    val r = (rAnnual / 12) / 100
    val n = tYears * 12

    val emi = if (r > 0 && n > 0 && p > 0) {
        val factor = (1 + r).pow(n)
        (p * r * factor) / (factor - 1)
    } else if (n > 0 && p > 0) {
        p / n
    } else {
        0.0
    }

    val totalPayment = emi * n
    val totalInterest = totalPayment - p

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = loanAmount,
            onValueChange = { loanAmount = it },
            label = { Text("Loan Amount (${currency.symbol})") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = interestRate,
            onValueChange = { interestRate = it },
            label = { Text("Interest Rate (p.a %)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = loanTenure,
            onValueChange = { loanTenure = it },
            label = { Text("Loan Tenure (Years)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ResultRow(label = "Monthly EMI", value = currency.format(emi), isTotal = true)
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                ResultRow(label = "Principal Amount", value = currency.format(p))
                ResultRow(label = "Total Interest Payable", value = currency.format(totalInterest))
                ResultRow(label = "Total Payment", value = currency.format(totalPayment))
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun AiToolsTab(viewModel: BudgetViewModel, currency: CurrencyInfo) {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTool by remember { mutableStateOf(0) } // 0: Forecast, 1: Split

    val coroutineScope = rememberCoroutineScope()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedTool == 0,
                onClick = { selectedTool = 0; result = ""; query = "" },
                label = { Text("Scenario Simulator") }
            )
            FilterChip(
                selected = selectedTool == 1,
                onClick = { selectedTool = 1; result = ""; query = "" },
                label = { Text("Bill Splitter") }
            )
        }

        val placeholder = if (selectedTool == 0) {
            "e.g., What happens to my savings goal if I rent a $1200 apartment instead of $1000?"
        } else {
            "e.g., Dinner was $120. Alice had steak $50, Bob had pasta $30, we split the $40 wine."
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(if (selectedTool == 0) "Ask a 'What if' question" else "Describe the shared bill") },
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 5
        )

        Button(
            onClick = {
                if (query.isBlank()) return@Button
                isLoading = true
                coroutineScope.launch {
                    val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                        result = "Please set your Gemini API key in AI Studio Secrets to use AI tools."
                        isLoading = false
                        return@launch
                    }

                    val contextText = if (selectedTool == 0) {
                        val income = transactions.filter { it.isIncome }.sumOf { it.amount }
                        val expense = transactions.filter { !it.isIncome }.sumOf { it.amount }
                        "Context: User's total income is ${currency.format(income)}, total expenses are ${currency.format(expense)}. Currency is ${currency.symbol}."
                    } else {
                        "Context: Extract who owes what from the following bill description. Determine exact amounts per person. Currency is ${currency.symbol}."
                    }

                    val prompt = "$contextText\n\nUser request: $query\nProvide a concise, professional, and mathematically accurate financial breakdown."
                    
                    try {
                        val request = com.example.gemini.GenerateContentRequest(
                            contents = listOf(com.example.gemini.Content(parts = listOf(com.example.gemini.Part(text = prompt))))
                        )
                        val response = com.example.gemini.RetrofitClient.service.generateContent(apiKey, request)
                        result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "No response from AI."
                    } catch (e: Exception) {
                        result = "Error connecting to AI: ${e.message}"
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(if (selectedTool == 0) "Simulate Scenario" else "Split Bill")
            }
        }

        if (result.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Response", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(result, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }
}

