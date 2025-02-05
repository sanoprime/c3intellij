package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.or
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.intention.AddImportQuickFix
import org.c3lang.intellij.psi.*

@Suppress("DuplicatedCode")
object ConstCompletionContributor : CompletionProvider<CompletionParameters>() {
    private val log = Logger.getInstance(
        ConstCompletionContributor::class.java
    )
    private val pattern = or(
        // foo::<caret>
        psiElement().inside(C3PathConst::class.java),
//        psiElement(TokenType.WHITE_SPACE) // TODO more logic needed
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (!pattern.accepts(parameters.originalPosition)) {
            return;
        }

        val moduleDefinition = parameters.moduleDefinition ?: return
        val elementRange = parameters.textRange
        val matcher = parameters.matcher
        val project = parameters.position.project

        val insertHandler = ConstInsertHandler(
            moduleDefinition = moduleDefinition,
            range = elementRange
        )

        StubIndex.getInstance().getAllKeys(
            NameIndex.KEY,
            project
        ).filter { matcher.matches(it) || it.isBlank() }.flatMap { key ->
            StubIndex.getElements(
                NameIndex.KEY,
                key,
                project,
                GlobalSearchScope.allScope(project),
                C3PsiElement::class.java,
            )
        }.filterIsInstance<C3ConstDeclarationStmt>().forEach { constDeclaration ->
            val fullyQualifiedName = FullyQualifiedName.from(constDeclaration)

            val lookupElementBuilder = LookupElementBuilder
                .create(constDeclaration, fullyQualifiedName.fullName)
                .withLookupStrings(listOf(fullyQualifiedName.fullName, fullyQualifiedName.name))
                .withPsiElement(constDeclaration)
                .withIcon(C3Icons.Nodes.CONSTANT)
                .withPresentableText(fullyQualifiedName.fullName)
                .withInsertHandler(insertHandler)

            result.addElement(lookupElementBuilder)
        }
    }

    @Suppress("DuplicatedCode")
    private class ConstInsertHandler(
        private val moduleDefinition: C3ModuleDefinition,
        private val range: TextRange,
    ) : InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3ConstDeclarationStmt

            WriteCommandAction.runWriteCommandAction(context.project) {
                val imported = AddImportQuickFix.addImportAsText(
                    element = element,
                    moduleSection = moduleDefinition,
                )

                val textToInsert = moduleDefinition.textToInsert(imported?.moduleName, element)
                val endOffset = editor.caretModel.offset

                document.replaceString(
                    range.startOffset,
                    endOffset,
                    textToInsert
                )
                editor.caretModel.moveToOffset(range.startOffset + textToInsert.length)

                imported?.write(document)
            }
        }

    }

}
