package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.siblings
import com.intellij.refactoring.suggested.startOffset
import org.c3lang.intellij.psi.C3ExprStmt
import org.c3lang.intellij.psi.C3ModuleDefinition
import org.c3lang.intellij.psi.C3OptionalType
import org.c3lang.intellij.psi.C3Parameter
import org.c3lang.intellij.psi.C3StructMemberDeclaration

private val log = Logger.getInstance("org.c3lang.intellij.completion.CompletionExtensionsKt")

val CompletionParameters.moduleDefinition: C3ModuleDefinition?
    get() = parentOfType<C3ModuleDefinition>()

val CompletionParameters.lookupTarget: PsiElement
    get() {
        parentOfType<C3ExprStmt>()?.let {
            // PathIdentExpr + PsiError;
            return it.siblings().first()
        }

        parentOfType<C3OptionalType>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        parentOfType<C3StructMemberDeclaration>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        parentOfType<C3Parameter>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        if (originalPosition is PsiWhiteSpace) {
            // we are at "dummy"
            return position
        }

        log.error("position $position or originalPosition $originalPosition not (yet) supported.")
        // other cases?
        TODO("Not implemented")
    }

inline fun <reified T : PsiElement> CompletionParameters.parentOfType(): T? {
    return (originalPosition?.parentOfType<T>() ?: position.parentOfType<T>())
}

val CompletionParameters.matcher
    get() = NameUtil.buildMatcher(
        "*$lookupString*",
        NameUtil.MatchingCaseSensitivity.NONE
    )

val CompletionParameters.textRange: TextRange get() = lookupTarget.textRange

val CompletionParameters.lookupString
    get() = editor.document.getText(
        TextRange.create(
            lookupTarget.startOffset,
            editor.caretModel.offset
        )
    )

const val DUMMY_IDENTIFIER: String = "dummy;"