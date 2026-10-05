package com.opponify.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

fun Modifier.accessibleAction(description: String): Modifier = semantics {
    contentDescription = description
    role = Role.Button
}

fun Modifier.accessibleState(description: String): Modifier = semantics {
    contentDescription = description
}
