package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.C3ImportProvider
import org.c3lang.intellij.psi.C3PathIdent

class PathIdentContext(
    parameters: CompletionParameters
) {
    val source = parameters.position

    val target = source.parentOfType<C3PathIdent>(true) ?: source

    val importProvider = target.parentOfType<C3ImportProvider>()

    val range = if (source is PsiWhiteSpace) {
        TextRange.create(
            parameters.editor.caretModel.offset,
            parameters.editor.caretModel.offset
        )
    } else {
        TextRange.create(
            target.textRange.startOffset,
            parameters.editor.caretModel.offset
        )
    }

    val lookupString = parameters.editor.document.getText(range).trim()

    val project = target.project

    val containingFileName = target.containingFile.name
}