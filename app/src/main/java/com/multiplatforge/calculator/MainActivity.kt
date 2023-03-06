package com.multiplatforge.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multiplatforge.calculator.ui.theme.CalculatorTheme
import java.text.DecimalFormat

val buttons = listOf(
    listOf('C', '(', ')', '÷'),
    listOf('7', '8', '9', '×'),
    listOf('4', '5', '6', '-'),
    listOf('1', '2', '3', '+'),
    listOf('0', '.', '='),
)

data class CalculatorState(
    val result: String = "0",
    val number: String? = null,
    val operation: Char? = null,
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(CalculatorState()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = state.number ?: state.result,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.End,
        )
        buttons.forEachIndexed { i, row ->
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEachIndexed { j, button ->
                    Button(
                        onClick = {
                            reduce(button, state, setState = { state = it })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(if (button != '0') 1f else 2f),

                        colors = when {
                            state.operation == button && state.number == null -> ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                            j == row.lastIndex -> ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                            )
                            i == 0 -> ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            )
                            else -> ButtonDefaults.buttonColors()
                        },
                    ) {
                        Text(
                            "$button",
                            style = MaterialTheme.typography.displaySmall,
                        )
                    }
                }
            }
        }
    }
}

private val formatter = DecimalFormat("#.##########")

private fun reduce(
    button: Char,
    state: CalculatorState,
    setState: (CalculatorState) -> Unit,
) {
    val (result, number, operation) = state
    when (button) {
        'C' -> {
            setState(CalculatorState())
        }
        in '0'..'9', '.' -> {
            if (number?.contains('.') == true && button == '.') return
            setState(state.copy(number = (number ?: "") + button))
        }
        in listOf('+', '-', '×', '÷', '=') -> {
            val oldNumber = result.toDoubleOrNull() ?: 0.0
            val newResult: Double? =
                if (number != null) {
                    val newNumber = number.toDouble()
                    when (operation) {
                        '+' -> oldNumber + newNumber
                        '-' -> oldNumber - newNumber
                        '×' -> oldNumber * newNumber
                        '÷' -> {
                            if (newNumber != 0.0)
                                oldNumber / newNumber
                            else
                                null
                        }
                        else -> newNumber
                    }
                } else {
                    oldNumber
                }
            setState(
                CalculatorState(
                    result = try {
                        formatter.format(newResult)
                    } catch (e: IllegalArgumentException) {
                        "Error"
                    },
                    number = null,
                    operation = button.takeIf { it != '=' },
                )
            )
        }
    }
}