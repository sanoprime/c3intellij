package org.c3lang.intellij.findUsages

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.c3lang.intellij.C3TokenSets
import org.c3lang.intellij.lexer.C3LexerAdapter
import org.c3lang.intellij.psi.*

class C3FindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            C3LexerAdapter(),
            TokenSet.create(
                C3Types.IDENT,
                C3Types.CONST_IDENT,
                C3Types.TYPE_IDENT,
            ),
            C3TokenSets.COMMENTS,
            C3TokenSets.STRINGS
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is C3NameIdentProvider
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return when (psiElement) {
            is C3LocalDeclAfterType,
            is C3StructMemberDeclaration -> "declaration"
            else -> null
        }
    }

    override fun getType(element: PsiElement): String {
        return when (element) {
            is C3ConstDeclarationStmt -> "Constant"
            is C3FuncName -> "Constant"
            is C3StructMemberDeclaration,
            is C3AccessIdent -> "Field"
            is C3Arg,
            is C3Parameter -> "Parameter"
            is C3BaseType -> "Type"
            is C3EnumConstant -> "Enum"
            is C3LocalDeclAfterType -> "Local declaration"
            is C3TypeName -> "Local declaration"
            // fallback
            is C3NameIdentProvider -> "Name identifier"
            else -> "Unknown"
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        // todo: show FQN or text representation of function
        return "Descriptive-of-${element.text}"
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return element.text;
    }
}