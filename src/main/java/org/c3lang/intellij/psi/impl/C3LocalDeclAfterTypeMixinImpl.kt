package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import org.c3lang.intellij.psi.C3LocalDeclAfterType
import org.c3lang.intellij.psi.C3Types

abstract class C3LocalDeclAfterTypeMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3LocalDeclAfterType {

    override fun getName(): String? {
        return nameIdentifier?.text
    }

    override fun setName(name: String): PsiElement? {
        val ident = nameIdentifier as? LeafPsiElement
        ident?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return firstChild.takeIf { it.elementType == C3Types.IDENT }
    }

    override val nameIdent: String?
        get() = name

    override fun getTextRange(): TextRange? {
        return nameIdentifier?.textRange ?: super.getTextRange()
    }

}