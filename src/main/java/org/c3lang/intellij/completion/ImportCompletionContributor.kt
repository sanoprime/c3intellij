package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.index.ModuleIndex
import org.c3lang.intellij.psi.C3ImportPaths
import org.c3lang.intellij.psi.C3Module
import org.c3lang.intellij.psi.C3PathIdent
import org.c3lang.intellij.psi.C3PsiElement

@Suppress("DuplicatedCode")
object ImportCompletionContributor : CompletionProvider<CompletionParameters>() {
    private val log = Logger.getInstance(
        ImportCompletionContributor::class.java.name
    )
    private val pattern = or(
        // foo::<caret>
        psiElement().inside(C3PathIdent::class.java),
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val originalPosition = parameters.originalPosition

        if (!pattern.accepts(originalPosition)) {
            return
        }
        val completionContext = PathIdentContext(parameters)
        completionContext.importProvider ?: return

        val matcher = NameUtil.buildMatcher(
            "*${completionContext.lookupString}*",
            NameUtil.MatchingCaseSensitivity.NONE
        )

        StubIndex.getInstance().getAllKeys(
            ModuleIndex.KEY,
            completionContext.project
        ).asSequence().filter { matcher.matches(it) || it.isBlank() }.flatMap { key ->
            StubIndex.getElements(
                ModuleIndex.KEY,
                key,
                completionContext.project,
                GlobalSearchScope.allScope(completionContext.project),
                C3PsiElement::class.java
            )
        }.filterIsInstance<C3Module>().map { module ->

            val params = module.moduleParams

            val tail = StringBuilder()
            if (params != null) {
                tail.append("(<").append(params.text).append(">)")
            }
            val attributes = module.attributes
            if (attributes != null) {
                tail.append(" ").append(attributes.text)
            }
            LookupElementBuilder.create(checkNotNull(module.moduleName).value)
                .withTailText(tail.toString(), true)
                .withInsertHandler(FunctionInsertHandler(completionContext.range))
        }.distinct().forEach(result::addElement)
    }

    @JvmRecord
    private data class FunctionInsertHandler(val range: TextRange) : InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val textToInsert = item.lookupString

            document.replaceString(
                range.startOffset,
                editor.caretModel.offset,
                textToInsert
            )
        }
    }
}
