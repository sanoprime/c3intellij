package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.index.ModuleIndex
import org.c3lang.intellij.psi.*

@Suppress("DuplicatedCode")
object ImportCompletionContributor : CompletionProvider<CompletionParameters>() {
    private val log = Logger.getInstance(ImportCompletionContributor::class.java)
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

        parameters.moduleDefinition ?: return
        val elementRange = parameters.textRange
        val matcher = parameters.matcher
        val moduleDefinition = parameters.moduleDefinition ?: return
        val project = parameters.position.project

        StubIndex.getInstance().getAllKeys(
            ModuleIndex.KEY,
            project
        ).asSequence().filter { matcher.matches(it) || it.isBlank() }.flatMap { key ->
            StubIndex.getElements(
                ModuleIndex.KEY,
                key,
                project,
                GlobalSearchScope.allScope(project),
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
                .withInsertHandler(ImportInsertHandler(elementRange, moduleDefinition))
        }.distinct().forEach(result::addElement)
    }

    @JvmRecord
    private data class ImportInsertHandler(val range: TextRange, val moduleDefinition: C3ModuleDefinition) :
        InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3FullyQualifiedNamePsiElement
            val textToInsert = element.fqName.fullName

            WriteCommandAction.runWriteCommandAction(context.project) {
                document.replaceString(
                    range.startOffset,
                    editor.caretModel.offset,
                    textToInsert
                )
            }
        }
    }
}
