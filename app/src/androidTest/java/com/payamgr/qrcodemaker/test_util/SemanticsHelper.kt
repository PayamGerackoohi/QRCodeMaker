package com.payamgr.qrcodemaker.test_util

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.navigation.NavHostController
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions

fun SemanticsNodeInteraction.assertHasRole(role: Role) = SemanticsMatcher("is '$role'") {
    it.config.getOrNull(SemanticsProperties.Role) == role
}.let { assert(it) }

fun SemanticsNodeInteraction.assertHasError() = SemanticsMatcher("has error") {
    !it.isErrorEmpty
}.let { assert(it) }

fun SemanticsNodeInteraction.assertDoesNotHaveError() = SemanticsMatcher("does not have error") {
    it.isErrorEmpty
}.let { assert(it) }

private val SemanticsNode.isErrorEmpty get() = config.getOrNull(SemanticsProperties.Error).isNullOrBlank()

fun NavHostController.assertCurrentRoute(route: String): AbstractStringAssert<*> =
    Assertions.assertThat(currentBackStackEntry?.destination?.route).isEqualTo(route)
