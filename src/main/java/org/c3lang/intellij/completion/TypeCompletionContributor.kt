package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns.or
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.intention.AddImportQuickFix
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.stubs.C3TypeEnum
import javax.swing.Icon

@Suppress("DuplicatedCode")
object TypeCompletionContributor : CompletionProvider<CompletionParameters>() {
    private val log = Logger.getInstance(
        TypeCompletionContributor::class.java
    )

    private val pattern = or(
        // foo::<caret>
        psiElement().inside(C3PathIdent::class.java),
        psiElement(C3Types.TYPE_IDENT),
        psiElement().inside(C3FnParameterList::class.java),
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val originalPosition = parameters.originalPosition

        if (!pattern.accepts(originalPosition)) {
            return;
        }

        val moduleDefinition = parameters.moduleDefinition ?: return
        val matcher = parameters.matcher
        val project = parameters.position.project

        val insertHandler = StructInsertHandler(
            moduleDefinition = moduleDefinition,
            lookupTarget = parameters.lookupTarget
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
        }.filterIsInstance<C3TypeName>().forEach { typeName ->
            val icon: Icon? = when (typeName.typeEnum) {
                C3TypeEnum.FALLBACK -> null
                C3TypeEnum.STRUCT -> C3Icons.Nodes.STRUCT
                C3TypeEnum.INTERFACE -> C3Icons.Nodes.INTERFACE
                C3TypeEnum.ENUM -> C3Icons.Nodes.ENUM
                C3TypeEnum.UNION -> C3Icons.Nodes.UNION
                C3TypeEnum.BITSTRUCT -> C3Icons.Nodes.BITSTRUCT
                C3TypeEnum.FAULT -> C3Icons.Nodes.FAULT
            }

            val lookupElementBuilder = LookupElementBuilder
                .create(typeName, typeName.fqName.fullName)
                .withLookupStrings(
                    listOf(
                        typeName.fqName.fullName,
                        typeName.fqName.name,
                        typeName.fqName.suffixName
                    )
                )
                .withPsiElement(typeName)
                .withIcon(icon)
                .withPresentableText(typeName.fqName.fullName)
                .withInsertHandler(insertHandler)

            result.addElement(lookupElementBuilder)
        }
    }

    @Suppress("DuplicatedCode")
    private class StructInsertHandler(
        private val moduleDefinition: C3ModuleDefinition,
        private val lookupTarget: PsiElement,
    ) : InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3TypeName

            WriteCommandAction.runWriteCommandAction(context.project) {
                val importAction = AddImportQuickFix.addImportAsText(
                    element = element,
                    moduleSection = moduleDefinition,
                )

                val textToInsert = moduleDefinition.textToInsert(importAction?.moduleName, element)
                val endOffset = editor.caretModel.offset

                document.replaceString(
                    lookupTarget.startOffset,
                    endOffset,
                    textToInsert
                )
                editor.caretModel.moveToOffset(lookupTarget.startOffset + textToInsert.length)

                importAction?.write(document)

                PsiDocumentManager.getInstance(context.project).commitDocument(document)
            }
        }

    }

}
